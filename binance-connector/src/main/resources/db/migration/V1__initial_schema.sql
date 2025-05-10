-- Create user_balance table
CREATE TABLE IF NOT EXISTS user_balance (
    id BIGSERIAL PRIMARY KEY,
    asset VARCHAR(20) NOT NULL,
    free NUMERIC(20, 8) NOT NULL,
    locked NUMERIC(20, 8) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT unique_asset_timestamp UNIQUE (asset, timestamp)
);

-- Create kline_data table
CREATE TABLE IF NOT EXISTS kline_data (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    open_time TIMESTAMP NOT NULL,
    close_time TIMESTAMP NOT NULL,
    interval VARCHAR(5) NOT NULL,
    open NUMERIC(20, 8) NOT NULL,
    high NUMERIC(20, 8) NOT NULL,
    low NUMERIC(20, 8) NOT NULL,
    close NUMERIC(20, 8) NOT NULL,
    volume NUMERIC(20, 8) NOT NULL,
    quote_asset_volume NUMERIC(20, 8) NOT NULL,
    number_of_trades BIGINT NOT NULL,
    is_closed BOOLEAN NOT NULL,
    taker_buy_base_asset_volume NUMERIC(20, 8) NOT NULL,
    taker_buy_quote_asset_volume NUMERIC(20, 8) NOT NULL,
    CONSTRAINT unique_symbol_opentime_interval UNIQUE (symbol, open_time, interval)
);

-- Add index for common query patterns
CREATE INDEX idx_user_balance_asset ON user_balance(asset);
CREATE INDEX idx_user_balance_timestamp ON user_balance(timestamp);
CREATE INDEX idx_kline_data_symbol ON kline_data(symbol);
CREATE INDEX idx_kline_data_interval_opentime ON kline_data(interval, open_time);
