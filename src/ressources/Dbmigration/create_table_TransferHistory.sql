CREATE TABLE IF NOT EXISTS TransferHistory (
    debitorTransactionId VARCHAR(50) PRIMARY KEY,
    creditorTransactionId VARCHAR(255) PRIMARY KEY,
    transferAmount DOUBLE PRECISION,
    transferDate TIMESTAMP
);