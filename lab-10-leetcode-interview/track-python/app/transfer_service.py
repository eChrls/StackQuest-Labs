"""Ticket A1 — Money transfer service.

Creates a transfer record for a user. The client is expected to send an
Idempotency-Key header so a retried or double-submitted request does not
create a second transfer.
"""

from __future__ import annotations

from datetime import datetime, timezone

from app.db import get_connection


def _now_iso() -> str:
    return datetime.now(timezone.utc).isoformat()


def create_transfer(user_id: str, amount: str, idempotency_key: str) -> dict:
    conn = get_connection()
    try:
        cursor = conn.execute(
            "INSERT INTO transfers (idempotency_key, user_id, amount, created_at) "
            "VALUES (?, ?, ?, ?)",
            (idempotency_key, user_id, amount, _now_iso()),
        )
        conn.commit()
        row = conn.execute(
            "SELECT * FROM transfers WHERE id = ?", (cursor.lastrowid,)
        ).fetchone()
        return dict(row)
    finally:
        conn.close()


def list_transfers() -> list[dict]:
    conn = get_connection()
    try:
        rows = conn.execute("SELECT * FROM transfers ORDER BY id").fetchall()
        return [dict(row) for row in rows]
    finally:
        conn.close()
