CREATE OR REPLACE FUNCTION calculate_money_flow(
    account_id INT,
    start_date TIMESTAMP,
    end_date TIMESTAMP
) RETURNS DOUBLE PRECISION AS $$
DECLARE
total_money_flow DOUBLE PRECISION;
BEGIN
SELECT COALESCE(SUM(CASE WHEN t.value > 0 THEN t.value ELSE 0 END), 0) -
       COALESCE(SUM(CASE WHEN t.value < 0 THEN t.value ELSE 0 END), 0)
INTO total_money_flow
FROM transactions t
WHERE t.date_time_transaction BETWEEN start_date AND end_date
  AND t.transaction_category_id IS NOT NULL;

RETURN total_money_flow;
END;
$$ LANGUAGE plpgsql;

SELECT calculate_money_flow(1, '2021-01-01 00:00:00', '2023-12-31 23:59:59');