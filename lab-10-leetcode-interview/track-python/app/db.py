"""SQLite access for the ticket A1 transfer service.

Kept deliberately small: one table, one connection per call. The database
path is read from TRANSFER_DB_PATH on every call so tests can point each
run at an isolated file.
"""

from __future__ import annotations

import os
import sqlite3

DEFAULT_DB_PATH = "/workspace/data/transfers.db"


def get_db_path() -> str:
    return os.environ.get("TRANSFER_DB_PATH", DEFAULT_DB_PATH)


def get_connection() -> sqlite3.Connection:
    conn = sqlite3.connect(get_db_path(), timeout=30)
    conn.row_factory = sqlite3.Row
    return conn


def init_db() -> None:
    conn = get_connection()
    try:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS transfers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                idempotency_key TEXT NOT NULL {unique_key},
                user_id TEXT NOT NULL,
                amount TEXT NOT NULL,
                created_at TEXT NOT NULL
            )
            """.format(unique_key="UNIQUE" if os.environ.get("LAB_REFERENCE_MODE") == "true" else "")
        )
        conn.commit()
    finally:
        conn.close()


def reset_db() -> None:
    """Test helper: drop and recreate the table for an isolated run."""
    conn = get_connection()
    try:
        conn.execute("DROP TABLE IF EXISTS transfers")
        conn.commit()
    finally:
        conn.close()
    init_db()
