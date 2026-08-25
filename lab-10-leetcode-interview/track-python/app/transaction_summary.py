"""Ticket E2 — Summarize approved transactions per user.

Each input line is a pipe-delimited record:
    "<ISO8601 timestamp>|<user_id>|<amount>|<STATUS>"
"""

from __future__ import annotations

from decimal import Decimal


def summarize_transactions(lines: list[str]) -> dict[str, Decimal]:
    """Return {user_id: total_approved_amount} for APPROVED transactions only.

    Users with no APPROVED transaction must not appear in the result.
    A line that is malformed (wrong number of fields or a non-numeric
    amount) must be skipped without breaking the rest of the batch.
    """
    raise NotImplementedError("Implement summarize_transactions for ticket E2")
