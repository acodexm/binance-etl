-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL,
    last_updated TIMESTAMP NOT NULL
);

-- Create user_balances_view hypertable
CREATE TABLE IF NOT EXISTS user_balances_view (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    asset VARCHAR(20) NOT NULL,
    free NUMERIC(20, 8) NOT NULL,
    locked NUMERIC(20, 8) NOT NULL,
    record_timestamp TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_balances_view_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create symbol_time_window_prices hypertable
CREATE TABLE IF NOT EXISTS symbol_time_window_prices (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    window_start_time TIMESTAMP NOT NULL,
    window_end_time TIMESTAMP NOT NULL,
    window_size VARCHAR(10) NOT NULL,
    open_price NUMERIC(20, 8) NOT NULL,
    high_price NUMERIC(20, 8) NOT NULL,
    low_price NUMERIC(20, 8) NOT NULL,
    close_price NUMERIC(20, 8) NOT NULL,
    volume NUMERIC(20, 8) NOT NULL,
    CONSTRAINT unique_symbol_window_size_time UNIQUE (symbol, window_size, window_start_time)
);

-- Create symbol_performance_view hypertable
CREATE TABLE IF NOT EXISTS symbol_performance_view (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    window_start_time TIMESTAMP NOT NULL,
    window_end_time TIMESTAMP NOT NULL,
    window_size VARCHAR(10) NOT NULL,
    price_change NUMERIC(20, 8) NOT NULL,
    price_change_percent NUMERIC(10, 4) NOT NULL,
    volume NUMERIC(20, 8) NOT NULL,
    CONSTRAINT unique_symbol_performance_window UNIQUE (symbol, window_size, window_start_time)
);

-- Create portfolio_value_view hypertable
CREATE TABLE IF NOT EXISTS portfolio_value_view (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    total_value_usd NUMERIC(20, 8) NOT NULL,
    CONSTRAINT fk_portfolio_value_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Add indexes for common query patterns
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_user_balances_view_user_id ON user_balances_view(user_id);
CREATE INDEX idx_user_balances_view_asset ON user_balances_view(asset);
CREATE INDEX idx_user_balances_view_timestamp ON user_balances_view(record_timestamp);
CREATE INDEX idx_symbol_time_window_prices_symbol ON symbol_time_window_prices(symbol);
CREATE INDEX idx_symbol_time_window_prices_window_size ON symbol_time_window_prices(window_size);
CREATE INDEX idx_symbol_performance_view_symbol ON symbol_performance_view(symbol);
CREATE INDEX idx_portfolio_value_view_user_id ON portfolio_value_view(user_id);
