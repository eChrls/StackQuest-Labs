# Wishlist Products Summary for E-commerce Dashboard

**Type:** SQL / Aggregation / JOIN / GROUP BY

**Database:** MySQL

**Difficulty:** Beginner–Intermediate

## Scenario

An e-commerce platform wants a dashboard summary of the products customers have added to their wishlists. The report must distinguish products that are currently available from products that are out of stock and must measure both product variety and the total volume of wishlist activity.

The database contains two tables.

### `products`

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | `INT` | `PRIMARY KEY` | Product identifier |
| `name` | `VARCHAR(255)` | | Product name |
| `price` | `DECIMAL(6,2)` | | Product price |
| `in_stock` | `SMALLINT` | | `1` when available; `0` when out of stock |

### `wishlists`

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `product_id` | `INT` | References `products.id` | Product added to a wishlist |
| `customer_email` | `VARCHAR(255)` | | Customer email address |

## Task

Write one MySQL query that returns a summary grouped by stock status with exactly these columns:

| Column | Definition |
| --- | --- |
| `stock_status` | `In Stock` when `products.in_stock = 1`; otherwise `Out of Stock` |
| `total_products` | Number of distinct products that occur in at least one wishlist for that stock status |
| `total_wishlist_entries` | Total number of wishlist rows associated with products in that stock status |

The distinction between the two counts is important. If one product occurs in eight customers' wishlists, it contributes `1` to `total_products` and `8` to `total_wishlist_entries`.

Your query must:

1. Relate `products` to `wishlists`.
2. Transform the numeric stock flag into `In Stock` or `Out of Stock`.
3. Group the report by stock status.
4. Count unique products separately from total wishlist rows.
5. Return one aggregated row for every stock status represented by wishlist data.

## Example data

### `products`

| id | name | price | in_stock |
| ---: | --- | ---: | ---: |
| 1 | Mechanical Mouse | 49.90 | 1 |
| 2 | Desk Lamp | 79.50 | 1 |
| 3 | Running Watch | 199.00 | 0 |
| 4 | USB Hub | 35.00 | 0 |

### `wishlists`

| product_id | customer_email |
| ---: | --- |
| 1 | ana@example.com |
| 1 | bob@example.com |
| 1 | carla@example.com |
| 2 | daniel@example.com |
| 2 | eva@example.com |
| 3 | fred@example.com |
| 3 | george@example.com |
| 3 | helen@example.com |
| 3 | iris@example.com |
| 4 | john@example.com |

## Expected result

| stock_status | total_products | total_wishlist_entries |
| --- | ---: | ---: |
| In Stock | 2 | 5 |
| Out of Stock | 2 | 5 |

Row order is not relevant because the task does not require an `ORDER BY` clause.

## Interview follow-up questions

1. Why would `COUNT(*)` not calculate `total_products` correctly?
2. What is the difference between `COUNT(*)` and `COUNT(DISTINCT product_id)`?
3. Why is a `JOIN` needed?
4. Which columns or expressions must participate in `GROUP BY`?
5. What happens if one product occurs 1,000 times in `wishlists`?
6. What happens to a product that does not occur in any wishlist?
7. How would `INNER JOIN` and `LEFT JOIN` differ for this report?
8. How would you change the query to display available products first?

## Hints

<details>
<summary>Hint 1</summary>

Start by relating every wishlist entry to its product.

</details>

<details>
<summary>Hint 2</summary>

You need to count two different things: unique products and total rows.

</details>

<details>
<summary>Hint 3</summary>

`CASE` can turn the numeric `in_stock` value into a readable label.

</details>
