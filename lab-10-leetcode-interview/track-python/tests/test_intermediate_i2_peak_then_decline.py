import pytest

from app.peak_then_decline import longest_peak_then_decline


@pytest.mark.public
def test_i2_example_window_length():
    activity = [3, 9, 5, 12, 7, 15, 4, 2]
    assert longest_peak_then_decline(activity) == 6


@pytest.mark.hidden
def test_i2_empty_input_has_zero_length():
    assert longest_peak_then_decline([]) == 0


@pytest.mark.hidden
def test_i2_single_value_has_length_one():
    assert longest_peak_then_decline([5]) == 1


@pytest.mark.hidden
def test_i2_strictly_increasing_series_counts_as_bitonic():
    assert longest_peak_then_decline([1, 2, 3, 4]) == 4


@pytest.mark.hidden
def test_i2_strictly_decreasing_series_counts_as_bitonic():
    assert longest_peak_then_decline([4, 3, 2, 1]) == 4


@pytest.mark.hidden
def test_i2_repeated_values_break_both_sides():
    assert longest_peak_then_decline([1, 1, 1]) == 1
