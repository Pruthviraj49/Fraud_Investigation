CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_reference VARCHAR(50) NOT NULL UNIQUE,
    country VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    merchant VARCHAR(255) NOT NULL,
    device_id VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    transaction_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transaction_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);

CREATE TABLE investigations (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL,
    risk_score INTEGER,
    risk_level VARCHAR(20),
    summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_investigation_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transactions(id)
);

CREATE TABLE fraud_rules (
    id BIGSERIAL PRIMARY KEY,
    rule_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    risk_score INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_customer_id
    ON transactions(customer_id);

CREATE INDEX idx_transactions_timestamp
    ON transactions(transaction_timestamp);

CREATE INDEX idx_transactions_device_id
    ON transactions(device_id);

CREATE INDEX idx_investigations_status
    ON investigations(status);