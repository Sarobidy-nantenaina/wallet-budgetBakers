
CREATE TABLE IF NOT EXISTS Transaction (
    id SERIAL PRIMARY KEY,
    label VARCHAR(255),
    amount DOUBLE PRECISION,
    date_time TIMESTAMP,
    type VARCHAR(50)
);

INSERT INTO Transaction (label, amount, date_time, type)
VALUES ('Purchase', 50.0, '2023-12-15 08:30:00', 'DEBIT');

INSERT INTO Transaction (label, amount, date_time, type)
VALUES ('Salary Deposit', 1000.0, '2023-12-16 14:45:00', 'CREDIT');

INSERT INTO Transaction (label, amount, date_time, type)
VALUES ('Withdrawal', 20.0, '2023-12-17 10:00:00', 'DEBIT');

