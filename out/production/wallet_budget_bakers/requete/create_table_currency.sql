-- Creating the Currency table if it doesn't exist
CREATE TABLE IF NOT EXISTS Currency (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(10)
);


INSERT INTO Currency (name, code)
VALUES ('Ariary', 'AR');

INSERT INTO Currency (name, code)
VALUES ('Euro', 'EUR');


