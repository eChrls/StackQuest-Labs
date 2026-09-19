WITH monthly_delays AS (
    SELECT
        YEAR(order_date) AS order_year,
        MONTH(order_date) AS order_month,
        CAST(
            ROUND(AVG(DATEDIFF(actual_delivery_date, expected_delivery_date)), 2)
            AS DECIMAL(10, 2)
        ) AS average_monthly_delay
    FROM orders
    GROUP BY YEAR(order_date), MONTH(order_date)
)
SELECT
    o.id AS order_id,
    o.customer_id,
    DATEDIFF(o.actual_delivery_date, o.expected_delivery_date) AS delay_days,
    m.average_monthly_delay
FROM orders AS o
INNER JOIN monthly_delays AS m
    ON m.order_year = YEAR(o.order_date)
   AND m.order_month = MONTH(o.order_date)
WHERE DATEDIFF(o.actual_delivery_date, o.expected_delivery_date)
      > 5 * m.average_monthly_delay;
