CREATE TABLE IF NOT EXISTS "Account" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    balance DOUBLE PRECISION,
    currency_id INT,
    type VARCHAR(50),
    FOREIGN KEY (currency_id) REFERENCES Currency(currency_id)
);


INSERT INTO Account (name, balance, currency_id, type)
VALUES ('Current Account', 100000, 1, 'BANK');

INSERT INTO Account (name, balance, currency_id, type)
VALUES ('Savings Account', 50000, 2, 'CASH');

INSERT INTO Account (name, balance, currency_id, type)
VALUES ('Mobile Money Account', 20000, 3, 'MOBILE_MONEY');
