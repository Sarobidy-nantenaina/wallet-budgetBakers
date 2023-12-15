CREATE OR REPLACE FUNCTION calculate_category_amounts(
    account_id_param INT,
    start_date_param TIMESTAMP,
    end_date_param TIMESTAMP
) RETURNS TABLE (restaurant DOUBLE PRECISION, salaire DOUBLE PRECISION) AS $$
BEGIN
RETURN QUERY
SELECT
    COALESCE(SUM(CASE WHEN tc.name = 'Restaurant' THEN t.value ELSE 0 END), 0) AS restaurant,
    COALESCE(SUM(CASE WHEN tc.name = 'Salaire' THEN t.value ELSE 0 END), 0) AS salaire
FROM transaction_categories tc
         LEFT JOIN transactions t
                   ON tc.transaction_category_id = t.transaction_category_id
                       AND t.date_time_transaction BETWEEN start_date_param AND end_date_param
WHERE t.transaction_id IN (
    SELECT transaction_id
    FROM account_transactions
    WHERE account_id = account_id_param
)
GROUP BY tc.name;
END;
$$ LANGUAGE plpgsql;



    SELECT * FROM calculate_category_amounts(1, '2023-10-11 00:00:00', '2024-12-02 23:59:59');
