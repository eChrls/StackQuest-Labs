SELECT
    CASE
        WHEN p.in_stock = 1 THEN 'In Stock'
        ELSE 'Out of Stock'
    END AS stock_status,
    COUNT(DISTINCT p.id) AS total_products,
    COUNT(*) AS total_wishlist_entries
FROM products AS p
INNER JOIN wishlists AS w
    ON w.product_id = p.id
GROUP BY p.in_stock;
