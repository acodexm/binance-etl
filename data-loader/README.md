# Data Loader Service

This service is responsible for consuming user balance and time market data from Kafka, calculating portfolio values and gain/loss metrics, and providing REST API endpoints for accessing this data.

## Features

- Consumes user balance data from the `user_balance_data` Kafka topic
- Consumes time market data from the `time_market_data` Kafka topic
- Calculates and tracks user portfolio values
- Calculates gain/loss percentages for different time windows (1h, 1d, 1w)
- Stores all data in TimescaleDB for efficient time-series analysis
- Exposes REST API endpoints for accessing the data

## REST API Endpoints

### Performance Data

- `GET /api/performance/{symbol}/{window}` - Get latest performance for a symbol and window
- `GET /api/performance/{symbol}` - Get performance for all available windows for a symbol
- `GET /api/history/performance/{symbol}/{window}?startTime=...&endTime=...` - Get historical performance data

### User Balance Data

- `GET /api/balances/{userId}` - Get latest balances for a user
- `GET /api/balances/summary/{userId}` - Get portfolio summary with total value in USDT

### System Status

- `GET /api/status` - Get service status and available symbols/assets

## Requirements

- Java 17 or higher
- TimescaleDB (PostgreSQL with TimescaleDB extension)
- Kafka

## Configuration

Main configuration parameters in `application.yml`:
