
CREATE TABLE IF NOT EXISTS Transaction (
    id SERIAL PRIMARY KEY,
    label VARCHAR(255),
    amount DOUBLE PRECISION,
    date_time TIMESTAMP,
    type VARCHAR(50)
);

