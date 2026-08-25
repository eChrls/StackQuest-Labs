"""Ticket E1 — Find a matching pair of transactions.

Reconciliation receives a list of transaction amounts and needs to find the
two transactions whose amounts sum exactly to a target amount.
"""

from __future__ import annotations


def find_pair(amounts: list[float], target: float) -> tuple[int, int] | None:
    """Return the (ascending) indices of the two amounts that sum to target.

    Return None if no such pair exists. If more than one pair sums to the
    target, returning any one valid pair is acceptable.
    """
    raise NotImplementedError("Implement find_pair for ticket E1")
