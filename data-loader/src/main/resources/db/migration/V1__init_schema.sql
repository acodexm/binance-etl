-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- User Balances View table
CREATE TABLE IF NOT EXISTS user_balances_view (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    asset VARCHAR(255) NOT NULL,
    free DECIMAL(19, 8) NOT NULL,
    locked DECIMAL(19, 8) NOT NULL,
    total_amount DECIMAL(19, 8) NOT NULL,
    record_timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Symbol Time Window Prices table
CREATE TABLE IF NOT EXISTS symbol_time_window_prices (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(255) NOT NULL,
    window_type VARCHAR(50) NOT NULL,
    window_start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    average_price DECIMAL(19, 8) NOT NULL,
    record_timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Symbol Performance View table
CREATE TABLE IF NOT EXISTS symbol_performance_view (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(255) NOT NULL,
    window_type VARCHAR(50) NOT NULL,
    window_start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    average_price DECIMAL(19, 8) NOT NULL,
    previous_average_price DECIMAL(19, 8),
    gain_loss_percent DECIMAL(19, 8),
    record_timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Portfolio Value View table
CREATE TABLE IF NOT EXISTS portfolio_value_view (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    total_value DECIMAL(19, 8) NOT NULL,
    asset_count INTEGER NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    base_currency VARCHAR(50) NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_user_balances_user_id ON user_balances_view(user_id);
CREATE INDEX IF NOT EXISTS idx_user_balances_asset ON user_balances_view(asset);
CREATE INDEX IF NOT EXISTS idx_user_balances_timestamp ON user_balances_view(record_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_symbol_prices_symbol ON symbol_time_window_prices(symbol);
CREATE INDEX IF NOT EXISTS idx_symbol_prices_window_type ON symbol_time_window_prices(window_type);
CREATE INDEX IF NOT EXISTS idx_symbol_prices_start_time ON symbol_time_window_prices(window_start_time DESC);

CREATE INDEX IF NOT EXISTS idx_symbol_performance_symbol ON symbol_performance_view(symbol);
CREATE INDEX IF NOT EXISTS idx_symbol_performance_window_type ON symbol_performance_view(window_type);
CREATE INDEX IF NOT EXISTS idx_symbol_performance_start_time ON symbol_performance_view(window_start_time DESC);

CREATE INDEX IF NOT EXISTS idx_portfolio_user_id ON portfolio_value_view(user_id);
CREATE INDEX IF NOT EXISTS idx_portfolio_timestamp ON portfolio_value_view(timestamp DESC);

-- Create unique constraints
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_balances_unique
    ON user_balances_view(user_id, asset, record_timestamp);

CREATE UNIQUE INDEX IF NOT EXISTS idx_symbol_prices_unique
    ON symbol_time_window_prices(symbol, window_type, window_start_time);

CREATE UNIQUE INDEX IF NOT EXISTS idx_symbol_performance_unique
    ON symbol_performance_view(symbol, window_type, window_start_time);
