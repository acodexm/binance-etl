Okay, this is a significant shift towards a batch-oriented, scheduled system with different data granularity and a focus on user-specific data and aggregated time-window analysis. Removing WebSockets simplifies the real-time aspect but introduces a pull-based model for the front-end.

Let's break down this new architecture:

**Architecture Overview (Scheduled, Batch-Oriented)**

```
+--------------------+   +-------------------+    +-----------+   +-------------------+   +-----------+   +-------------------+   +--------------+
|                    |   |                   |    |  DB 1     |   |                   |   |  DB 2     |   |                   |   | DB 3 (TSDB)  |
| Binance API (REST) |   | binance-connector |    | (Postgres)|   | data-transformer  |   | (Postgres)|   | data-loader       |   | (TimescaleDB)|
| (Spot Endpoints)   +---> (Spring Boot)     +----> (Raw Kline)|   | (Spring Boot)     +---> (Agg Kline)|   | (Spring Boot)     +---> (Views)      |
|                    |   | (Scheduler)       |    |  User Bal)|   | (Consumes Kafka)  |   |           |   | (Consumes Kafka)  |   |              |
|                    |   | (Gets Balances)   |    +-----------+   | (Calculates Avg)  |   +-----------+   | (Creates Views)   |   +--------------+
|                    |   | (Gets Klines)     |          |         | (Saves to DB)     |         |         | (Saves to TSDB)   |          |
|                    |   | (Saves to DB 1)   +----------+         | (Produces Kafka)  +---------+         | (Exposes REST)    +----------+
|                    |   | (Produces Kafka)  |--------------------+-------------------+-------------------+                   |
+--------------------+   +-------------------+                    |   Kafka Broker    |                   |                   |
                                                                 | Topic: raw_kline_data |                   |                   |
                                                                 | Topic: user_balances  |                   |                   |
                                                                 | Topic: time_market_data|                   |                   |
                                                                 +-----------------------+-------------------+                   |
                                                                                                                                 |
                                                                                                                                 V
                                                                                                                          +-------------+
                                                                                                                          | React App   |
                                                                                                                          | (Consumes   |
                                                                                                                          |  REST API)  |
                                                                                                                          +-------------+
```

**Detailed Component Breakdown**

**1. Binance Connector Service ("binance-connector")**

*   **Purpose:** Periodically fetch user balances and 24h kline data for each asset, save to its local DB, and publish to Kafka.
*   **Binance API Key/Secret:** **Required** for fetching user data (balances).
*   **Implementation:**
    *   Spring Boot application.
    *   **Binance Connector Library:** Use the official `binance-connector-java` library for Spot API interactions.
    *   **Scheduler:** Use Spring's `@Scheduled` annotation on a method to trigger the use case (e.g., every 5 minutes, every hour, configurable).
    *   **Use Case Logic (within the scheduled method):**
        1.  **Fetch User Balances:**
            *   Use `binance-connector-java` to call the endpoint for account balances (e.g., `/api/v3/account`).
            *   This will return a list of assets the user holds with their quantities.
        2.  **For each asset in the user's balance:**
            *   Construct the symbol (e.g., if asset is "BTC", and trading pair is "USDT", symbol is "BTCUSDT". You'll need a strategy to determine the quote currency, often USDT or BUSD).
            *   **Fetch Kline Data (Last 24h):**
                *   Use `binance-connector-java` to call the kline/candlestick data endpoint (e.g., `/api/v3/klines`).
                *   Parameters: `symbol`, `interval` (e.g., "1h" or "1m" - choose an interval that makes sense for your 24h window and desired granularity), `limit` (calculate how many intervals fit in 24h).
                *   Alternatively, use `startTime` and `endTime` to specify the last 24 hours.
        3.  **Database Persistence (Local DB 1 - Postgres):**
            *   Use Spring Data JPA.
            *   **Table 1: `user_balances`**: Store user ID (if multi-user, otherwise a static ID), asset symbol, free amount, locked amount, timestamp of fetch.
                *   Entity: `UserBalanceEntity`
            *   **Table 2: `raw_kline_data`**: Store symbol, open_time, open, high, low, close, volume, etc., for each kline fetched.
                *   Entity: `RawKlineEntity`
            *   Save the fetched balances and all klines to their respective tables in this service's dedicated PostgreSQL database.
        4.  **Kafka Production:**
            *   Use Spring Kafka `KafkaTemplate`.
            *   **Topic 1: `raw_kline_data`**: For each kline fetched and saved, publish a message containing the full kline data. (e.g., `{"symbol": "BTCUSDT", "openTime": ..., "close": ..., ...}`).
            *   **Topic 2: `user_balances`**: Publish a message (or messages, one per asset) containing the user's balance information. (e.g., `{"userId": "user123", "asset": "BTC", "free": 0.5, "locked": 0.1, "timestamp": ...}`).
*   **Configuration:** Binance API Key/Secret, scheduler cron expression, Kafka brokers, DB 1 connection details.
*   **DB 1 Schema (Examples):**
    *   `user_balances` (user_id VARCHAR, asset VARCHAR, free DECIMAL, locked DECIMAL, fetch_timestamp TIMESTAMP, PRIMARY KEY (user_id, asset, fetch_timestamp))
    *   `raw_kline_data` (symbol VARCHAR, open_time TIMESTAMP, open DECIMAL, high DECIMAL, low DECIMAL, close DECIMAL, volume DECIMAL, PRIMARY KEY (symbol, open_time))

**2. Data Transformer Service ("data-transformer")**

*   **Purpose:** Listen to raw kline data, calculate aggregated prices for 1h, 1d, 1w windows, save to its local DB, and publish aggregated data.
*   **Implementation:**
    *   Spring Boot application.
    *   **Kafka Consumption:**
        *   Use Spring Kafka `@KafkaListener` to consume messages from the `raw_kline_data` topic.
        *   The listener receives individual kline data points.
    *   **Data Aggregation Logic:**
        *   This service needs to collect incoming klines and aggregate them. Since Kafka messages are individual klines, this service will need to maintain some state or query its own database to perform aggregations.
        *   **Strategy:** When a new `raw_kline_data` message arrives:
            1.  Store this raw kline in its *own* local database (DB 2). This helps build up the data needed for window calculations if messages arrive out of order or if the service restarts.
            2.  Periodically (e.g., after receiving a certain number of klines, or on another scheduler within this service, or triggered by the timestamp of the incoming kline), query its local DB 2 for klines within the required time windows (last hour, last 24 hours, last 7 days) for the given symbol.
            3.  Calculate the "average price" for each window. "Average price" needs definition:
                *   Could be `(sum of (open + high + low + close) / 4) / number of klines`.
                *   Could be `(window_open + window_high + window_low + window_close) / 4`.
                *   Could simply be the `close` price of the last kline in that window.
                *   Let's assume for simplicity it's the average of the `close` prices within the window.
    *   **Database Persistence (Local DB 2 - Postgres):**
        *   Use Spring Data JPA.
        *   **Table 1: `raw_kline_mirror` (optional but recommended for aggregation):** A copy of the klines received from Kafka, to facilitate window queries. (symbol, open_time, close, etc.)
        *   **Table 2: `aggregated_kline_data`**: Store symbol, window_type (e.g., "1h", "1d", "1w"), window_start_time, window_end_time, average_price, other aggregated metrics if needed.
            *   Entity: `AggregatedKlineEntity`
        *   Save the calculated aggregated data to this table.
    *   **Kafka Production:**
        *   Use Spring Kafka `KafkaTemplate`.
        *   **Topic: `time_market_data`**: Publish messages containing the aggregated data for each window.
            *   e.g., `{"symbol": "BTCUSDT", "window": "1h", "startTime": ..., "endTime": ..., "averagePrice": 25050.75}`
            *   e.g., `{"symbol": "BTCUSDT", "window": "1d", "startTime": ..., "endTime": ..., "averagePrice": 24900.10}`
*   **Configuration:** Kafka brokers, DB 2 connection details.
*   **DB 2 Schema (Examples):**
    *   `raw_kline_mirror` (symbol VARCHAR, open_time TIMESTAMP, close DECIMAL, PRIMARY KEY (symbol, open_time))
    *   `aggregated_kline_data` (symbol VARCHAR, window_type VARCHAR, window_start_time TIMESTAMP, average_price DECIMAL, PRIMARY KEY (symbol, window_type, window_start_time))

**3. Data Loader Service ("data-loader")**

*   **Purpose:** Listen to user balances and aggregated time kline data, create view models (calculating gain/loss), store in TimescaleDB, and expose via REST.
*   **Implementation:**
    *   Spring Boot application.
    *   **TimescaleDB Setup:** This service's PostgreSQL database (DB 3) must have the TimescaleDB extension enabled.
    *   **Kafka Consumption:**
        *   `@KafkaListener` for `user_balances` topic.
        *   `@KafkaListener` for `time_market_data` topic.
    *   **Data Processing & View Model Creation:**
        1.  **User Balances:**
            *   When a `user_balances` message arrives:
                *   Store it in a `user_balances_view` table in TimescaleDB (hypertables are good for time-series balance snapshots).
                *   Potentially calculate and store a `total_portfolio_value_view` if you can get current prices for all assets (this might require another call to Binance or using the `time_market_data`). For simplicity, let's assume it just stores raw balances first.
        2.  **Time Kline Data (Aggregated Prices):**
            *   When a `time_market_data` message arrives (e.g., average price for BTCUSDT over 1h):
                *   Store this aggregated price in a `symbol_time_window_prices` table in TimescaleDB (hypertable).
                *   **Gain/Loss Calculation:** This is the tricky part. To calculate gain/loss for a window (e.g., 1d), you need:
                    *   The current average price for that window (from the incoming message).
                    *   The average price from the *previous* corresponding window (e.g., the 1d average price from 24 hours ago).
                    *   This requires querying historical data from its *own* `symbol_time_window_prices` table in TimescaleDB.
                    *   Gain/Loss % = `((current_avg_price - previous_avg_price) / previous_avg_price) * 100`.
                *   Store the calculated gain/loss along with the window data in a `symbol_performance_view` table in TimescaleDB.
    *   **Database Persistence (DB 3 - TimescaleDB):**
        *   Use Spring Data JPA (or R2DBC for reactive if preferred, but JPA works with TimescaleDB).
        *   Define Entities and Repositories for:
            *   `UserBalanceViewEntity` (maps to `user_balances_view` hypertable)
            *   `AggregatedPriceViewEntity` (maps to `symbol_time_window_prices` hypertable)
            *   `SymbolPerformanceViewEntity` (maps to `symbol_performance_view` hypertable)
        *   Ensure tables are created as hypertables using SQL commands after Spring creates them (e.g., `SELECT create_hypertable('symbol_time_window_prices', 'window_start_time');`).
    *   **REST Controllers:**
        *   Use Spring Web (`@RestController`).
        *   **Endpoints for Time Window Data:**
            *   `/api/performance/{symbol}/{window}` (e.g., `/api/performance/BTCUSDT/1d`): Returns the latest calculated performance (average price, gain/loss %) for that symbol and window.
            *   `/api/performance/{symbol}`: Returns performance for all available windows for a symbol.
            *   `/api/history/performance/{symbol}/{window}?start_time=...&end_time=...`: Historical performance data.
        *   **Endpoints for User Balances:**
            *   `/api/balances/{userId}`: Returns the latest balances for a user.
            *   `/api/balances/summary/{userId}`: Returns a sum of all assets (you'd need to convert all assets to a common currency like USDT, which means you need current prices for *all* assets the user holds. This might involve this service also querying Binance for current ticker prices, or relying on the freshest `time_market_data` for 1h windows).
        *   These controllers will query the TimescaleDB view tables.
*   **Configuration:** Kafka brokers, DB 3 (TimescaleDB) connection details.
*   **DB 3 Schema (TimescaleDB - Examples, all likely Hypertables):**
    *   `user_balances_view` (user_id VARCHAR, asset VARCHAR, free DECIMAL, locked DECIMAL, record_timestamp TIMESTAMP, PRIMARY KEY (user_id, asset, record_timestamp))
    *   `symbol_time_window_prices` (symbol VARCHAR, window_type VARCHAR, window_start_time TIMESTAMP, average_price DECIMAL, PRIMARY KEY (symbol, window_type, window_start_time))
    *   `symbol_performance_view` (symbol VARCHAR, window_type VARCHAR, window_start_time TIMESTAMP, average_price DECIMAL, gain_loss_percent DECIMAL, PRIMARY KEY (symbol, window_type, window_start_time))

**4. React Frontend Application**

*   **Purpose:** Fetch and display performance data and user balances from the Data Loader's REST API.
*   **Implementation:**
    *   Standard React setup.
    *   Use `fetch` or `axios` to call the REST endpoints exposed by the Data Loader service.
    *   Display:
        *   A dashboard showing user's total portfolio value (if calculated by loader).
        *   A list of user's assets and their quantities.
        *   For selected symbols (e.g., BTCUSDT), display cards or sections for 1h, 1d, 1w windows showing:
            *   Current average price for that window.
            *   Gain/Loss percentage for that window.
        *   Potentially charts showing historical gain/loss or average prices over time by calling the history endpoints.
    *   Since there are no WebSockets, the UI will need a refresh button or implement polling (e.g., every 30 seconds) to get updated data.

**Key Changes and Considerations:**

*   **Decoupled Databases:** Each service managing its own DB schema is good for microservice independence.
*   **Batch Nature:** The system updates based on the `binance-connector`'s schedule. The UI will not be real-time tick-by-tick but will reflect the latest processed batch.
*   **Complexity in Data Transformer:** Aggregating klines received individually from Kafka into time windows requires careful state management or frequent querying of its local DB.
*   **Complexity in Data Loader (Gain/Loss):** Calculating gain/loss requires looking up previous period data, which means the loader's TimescaleDB becomes crucial for these calculations.
*   **User Context:** The `binance-connector` is now user-specific due to balance fetching. If this is a multi-user system, you'll need to manage multiple API keys/secrets or have a way to identify users. For a prototype, assuming a single user simplifies things.
*   **Determining "Average Price":** Be very clear on how this is calculated in the `data-transformer`.
*   **Initial Data Load:** When the system starts, how will historical data for gain/loss calculations be seeded? The `binance-connector` fetches last 24h, but for a 1-week gain/loss, you'd need more. This might require a separate one-time backfill process or the system will only show accurate gain/loss after it has run for a week.

This design is quite different but aligns with your new requirements for scheduled batch processing and specific view model creation.
