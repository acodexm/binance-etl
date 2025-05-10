CREATE TABLE raw_kline_mirror (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    interval VARCHAR(10) NOT NULL,
    open_time TIMESTAMP NOT NULL,
    close_time TIMESTAMP,
    open_price DECIMAL(19, 8),
    high_price DECIMAL(19, 8),
    low_price DECIMAL(19, 8),
    close_price DECIMAL(19, 8),
    volume DECIMAL(19, 8),
    is_closed BOOLEAN NOT NULL,
    CONSTRAINT uk_raw_kline_mirror UNIQUE (symbol, open_time)
);

CREATE INDEX idx_raw_kline_mirror_symbol ON raw_kline_mirror (symbol);
CREATE INDEX idx_raw_kline_mirror_open_time ON raw_kline_mirror (open_time);

CREATE TABLE aggregated_kline_data (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    window_type VARCHAR(10) NOT NULL,
    window_start_time TIMESTAMP NOT NULL,
    window_end_time TIMESTAMP NOT NULL,
    average_price DECIMAL(19, 8) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_aggregated_kline_data UNIQUE (symbol, window_type, window_start_time)
);

CREATE INDEX idx_aggregated_kline_data_symbol ON aggregated_kline_data (symbol);
CREATE INDEX idx_aggregated_kline_data_window_type ON aggregated_kline_data (window_type);
CREATE INDEX idx_aggregated_kline_data_window_start ON aggregated_kline_data (window_start_time);
