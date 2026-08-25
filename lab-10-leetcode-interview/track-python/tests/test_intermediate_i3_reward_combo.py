import pytest

from app.reward_combo import min_rewards


@pytest.mark.public
def test_i3_example_minimum_reward_count():
    assert min_rewards([1, 5, 10, 25], 37) == 4


@pytest.mark.hidden
def test_i3_unreachable_target_returns_minus_one():
    assert min_rewards([3, 7], 5) == -1


@pytest.mark.hidden
def test_i3_zero_target_needs_no_rewards():
    assert min_rewards([1, 5, 10, 25], 0) == 0


@pytest.mark.hidden
def test_i3_small_exact_combination():
    assert min_rewards([1, 5, 10, 25], 6) == 2
