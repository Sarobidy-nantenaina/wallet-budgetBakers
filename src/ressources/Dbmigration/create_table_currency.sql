-- Creating the Currency table if it doesn't exist
CREATE TABLE IF NOT EXISTS Currency (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255),
    code VARCHAR(10)
);



