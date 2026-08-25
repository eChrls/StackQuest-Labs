"""Ticket I4 (main rotation) — Count fraud clusters (Number-of-Islands
pattern).

A fraud-review grid marks each cell as suspicious ("1") or clean ("0").
Two suspicious cells belong to the same cluster only if they are adjacent
horizontally or vertically (never diagonally). Count the clusters.
"""

from __future__ import annotations


def count_fraud_clusters(grid: list[list[str]]) -> int:
    """Return the number of 4-directionally connected clusters of '1' cells."""
    raise NotImplementedError("Implement count_fraud_clusters for ticket I4")
