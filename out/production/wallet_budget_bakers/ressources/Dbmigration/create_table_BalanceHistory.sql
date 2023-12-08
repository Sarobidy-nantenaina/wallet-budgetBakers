CREATE TABLE IF NOT EXISTS BalanceHistory (
    id SERIAL PRIMARY KEY,
    account_id VARCHAR(50),
    date_time_from TIMESTAMP,
    date_time_to TIMESTAMP
);


