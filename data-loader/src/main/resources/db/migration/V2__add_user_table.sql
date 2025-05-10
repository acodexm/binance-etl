-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Add default user
INSERT INTO users (user_id, created_at, active, last_updated)
VALUES ('default', NOW(), TRUE, NOW())
ON CONFLICT (user_id) DO NOTHING;

-- Add user_id foreign key to user_balances_view if needed
ALTER TABLE user_balances_view
ADD CONSTRAINT fk_user_balances_user
FOREIGN KEY (user_id)
REFERENCES users (user_id);

-- Add index for user lookup by activity
CREATE INDEX idx_users_active ON users(active);
