# Identifying High-Impact Delays in Order Fulfillment

**Type:** SQL / CTE / Aggregation / Date Functions / Filtering

**Database:** MySQL

**Difficulty:** Intermediate

## Scenario

An e-commerce company wants to identify orders whose delivery delays were exceptionally high compared with the normal behavior of the month in which each order was placed.

The database contains one table.

### `orders`

| Column | Type | Constraints | Description |
| --- | --- | --- | --- |
| `id` | `INT` | `PRIMARY KEY` | Unique order identifier |
| `customer_id` | `VARCHAR(255)` | | Customer who placed the order |
| `order_date` | `DATE` | | Date on which the order was placed |
| `expected_delivery_date` | `DATE` | | Expected delivery date |
| `actual_delivery_date` | `DATE` | | Actual delivery date |

For each order, define its delivery delay as:

```text
delay_days = actual_delivery_date - expected_delivery_date
```

MySQL provides `DATEDIFF` for calculating this difference in days.

## Task

Write one MySQL query that returns only orders whose delay is exceptionally high compared with the average delay for the order's month.

The result must contain exactly these columns:

| Column | Definition |
| --- | --- |
| `order_id` | Identifier of the qualifying order |
| `customer_id` | Customer who placed the order |
| `delay_days` | Difference in days between actual and expected delivery |
| `average_monthly_delay` | Average delay of all orders placed in that year and month, rounded to two decimal places |

Apply these rules:

1. Calculate `delay_days` for every order.
2. Calculate the average delay using **all orders** placed in the same calendar year and month.
3. Determine the month from `order_date`, not from either delivery date.
4. Keep year and month together. Grouping only by month number would incorrectly mix, for example, January 2025 with January 2026.
5. Round the monthly average to two decimal places before using it in the threshold comparison.
6. Include an order only when `delay_days > 5 * average_monthly_delay`.
7. The comparison is strict: an order equal to exactly five times the rounded average does not qualify.
8. Return the rounded monthly average in `average_monthly_delay`, including two decimal places where appropriate.

The filtering criterion depends on aggregated information from other rows, so the task requires conceptually separate monthly-statistics and per-order evaluation stages.

## Example data

| id | customer_id | order_date | expected_delivery_date | actual_delivery_date |
| ---: | --- | --- | --- | --- |
| 2001 | U001 | 2026-03-03 | 2026-03-08 | 2026-03-10 |
| 2002 | U002 | 2026-03-07 | 2026-03-12 | 2026-03-22 |
| 2003 | U003 | 2026-03-15 | 2026-03-20 | 2026-03-22 |
| 2004 | U004 | 2026-04-01 | 2026-04-06 | 2026-04-08 |
| 2005 | U005 | 2026-04-04 | 2026-04-09 | 2026-11-02 |
| 2006 | U006 | 2026-04-10 | 2026-04-15 | 2026-04-17 |
| 2007 | U007 | 2026-04-13 | 2026-04-18 | 2026-04-20 |
| 2008 | U008 | 2026-04-15 | 2026-04-20 | 2026-04-20 |
| 2009 | U009 | 2026-04-17 | 2026-04-22 | 2026-04-22 |

For these rows, March has delays of `2`, `10` and `2` days, giving a rounded average of `4.67`. April has delays of `2`, `207`, `2`, `2`, `0` and `0` days, giving a rounded average of `35.50`.

## Expected result

| order_id | customer_id | delay_days | average_monthly_delay |
| ---: | --- | ---: | ---: |
| 2005 | U005 | 207 | 35.50 |

No March order exceeds `5 × 4.67 = 23.35`. In April, only order `2005` exceeds `5 × 35.50 = 177.50`.

## Interview follow-up questions

1. Why is this difficult to solve with only a `WHERE` clause directly over `orders`?
2. What is the difference between an aggregate function and a per-row function?
3. Why must the monthly average be calculated before evaluating individual orders?
4. Why can grouping only by `MONTH(order_date)` introduce a bug?
5. What is the difference between using a CTE and a subquery here?
6. What does `DATEDIFF` do in MySQL?
7. What happens to an order delivered exactly on its expected date?
8. What happens when `delay_days` is exactly five times `average_monthly_delay`?
9. Why does the requirement specify `ROUND(..., 2)`?
10. Is the monthly average based on `order_date`, `expected_delivery_date` or `actual_delivery_date`?
11. How would you show every order with its monthly average without filtering out non-outliers?
12. How would you replace the factor of five with a configurable parameter?

## Hints

<details>
<summary>Hint 1</summary>

Start by calculating each order's delay with a date function.

</details>

<details>
<summary>Hint 2</summary>

Before filtering individual orders, calculate the average delay for each year and month.

</details>

<details>
<summary>Hint 3</summary>

A CTE or subquery can calculate the monthly averages before you associate them with individual orders.

</details>
