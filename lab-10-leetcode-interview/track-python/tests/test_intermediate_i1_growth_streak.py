import pytest

from app.growth_streak import longest_growth_streak


@pytest.mark.public
def test_i1_example_streak_length():
    volumes = [7, 2, 8, 3, 9, 4, 10, 1, 11]
    assert longest_growth_streak(volumes) == 5


@pytest.mark.hidden
def test_i1_empty_input_has_zero_streak():
    assert longest_growth_streak([]) == 0


@pytest.mark.hidden
def test_i1_single_value_has_streak_of_one():
    assert longest_growth_streak([42]) == 1


@pytest.mark.hidden
def test_i1_strictly_decreasing_has_streak_of_one():
    assert longest_growth_streak([9, 7, 5, 3, 1]) == 1


@pytest.mark.hidden
def test_i1_strictly_increasing_uses_the_whole_series():
    assert longest_growth_streak([1, 2, 3, 4, 5]) == 5


@pytest.mark.hidden
def test_i1_repeated_values_do_not_extend_the_streak():
    assert longest_growth_streak([1, 2, 2, 3]) == 3
