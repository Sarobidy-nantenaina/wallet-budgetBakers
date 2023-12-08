CREATE TABLE IF NOT EXISTS BalanceHistory (
    id SERIAL PRIMARY KEY,
    account_id VARCHAR(50),
    date_time_from TIMESTAMP,
    date_time_to TIMESTAMP
);

INSERT INTO BalanceHistory (account_id, date_time_from, date_time_to)
VALUES ('1', '2023-12-01 00:00:00', '2023-12-02 00:00:00');

INSERT INTO BalanceHistory (account_id, date_time_from, date_time_to)
VALUES ('2', '2023-12-03 12:00:00', '2023-12-04 12:00:00');

