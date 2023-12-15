CREATE TABLE IF NOT EXISTS "Account" (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255),
    balance DOUBLE PRECISION,
    currency_id INT,
    type VARCHAR(50),
    FOREIGN KEY (currency_id) REFERENCES Currency(currency_id)
);

