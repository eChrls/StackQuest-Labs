"""Ticket I3 (optional rotation) — Minimum reward combination (Coin Change /
minimum-coins pattern).

A loyalty program has a fixed set of reward denominations. Find the minimum
number of rewards that add up to exactly the required number of points.
"""

from __future__ import annotations


def min_rewards(reward_values: list[int], required_points: int) -> int:
    """Return the minimum count of rewards summing to required_points.

    Return -1 if required_points cannot be reached exactly with the given
    denominations. required_points == 0 must return 0.
    """
    raise NotImplementedError("Implement min_rewards for ticket I3")
