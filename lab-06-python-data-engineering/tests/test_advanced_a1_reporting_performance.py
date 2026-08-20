from datetime import datetime, timezone

from sqlalchemy import event, text

from app.db import engine

DATE_FROM = datetime(2024, 6, 1, tzinfo=timezone.utc)
DATE_TO = datetime(2024, 6, 11, tzinfo=timezone.utc)


class QueryCounter:
    def __init__(self):
        self.count = 0

    def __enter__(self):
        event.listen(engine, "before_cursor_execute", self._before)
        return self

    def __exit__(self, *exc_info):
        event.remove(engine, "before_cursor_execute", self._before)

    def _before(self, conn, cursor, statement, parameters, context, executemany):
        normalized = statement.strip().lower()
        if normalized.startswith("select") and "payments" in normalized:
            self.count += 1


def _seed_large_dataset(db):
    db.execute(text(
        """
        INSERT INTO payments (source_event_id, merchant_id, amount, currency, status, created_at)
        SELECT
            'bench-' || i,
            'M-BENCH-' || lpad(((i % 400) + 1)::text, 4, '0'),
            (5 + (i % 495))::numeric(12,2),
            'EUR',
            (ARRAY['CAPTURED','FAILED','PENDING','REFUNDED'])[1 + (i % 4)],
            TIMESTAMP '2024-01-01' + ((i % 397) || ' days')::interval + ((i % 24) || ' hours')::interval
        FROM generate_series(1, 200000) AS s(i)
        """
    ))
    db.execute(text("ANALYZE payments"))
    db.commit()


def _plan_uses_seq_scan_on_payments(node) -> bool:
    if node.get("Node Type") == "Seq Scan" and node.get("Relation Name") == "payments":
        return True
    return any(_plan_uses_seq_scan_on_payments(child) for child in node.get("Plans", []))


def test_a1_leaderboard_report_scales_with_index_and_single_query(client, db):
    _seed_large_dataset(db)

    http_params = {
        "status": "CAPTURED",
        "date_from": DATE_FROM.isoformat(),
        "date_to": DATE_TO.isoformat(),
        "limit": 10,
    }
    with QueryCounter() as counter:
        response = client.get("/api/reports/merchant-leaderboard", params=http_params)
    assert response.status_code == 200
    assert counter.count == 1, (
        f"the leaderboard must run one grouped query, not one query per merchant (ran {counter.count})"
    )

    sql_params = {"status": "CAPTURED", "date_from": DATE_FROM, "date_to": DATE_TO}
    plan = db.execute(text(
        """
        EXPLAIN (ANALYZE, FORMAT JSON)
        SELECT merchant_id, count(*), sum(amount)
        FROM payments
        WHERE status = :status AND created_at >= :date_from AND created_at < :date_to
        GROUP BY merchant_id
        ORDER BY sum(amount) DESC
        LIMIT 10
        """
    ), sql_params).scalar()
    top_node = plan[0]["Plan"]
    assert not _plan_uses_seq_scan_on_payments(top_node), (
        "the filtered aggregate must use an index on (status, created_at), not a full table scan"
    )
