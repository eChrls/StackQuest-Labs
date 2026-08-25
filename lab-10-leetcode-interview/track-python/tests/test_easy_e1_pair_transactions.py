import pytest

from app.pair_transactions import find_pair


def _assert_valid_pair(result, amounts, target):
    assert result is not None
    i, j = result
    assert i < j
    assert 0 <= i < len(amounts)
    assert 0 <= j < len(amounts)
    assert amounts[i] + amounts[j] == pytest.approx(target)


@pytest.mark.public
def test_e1_example_pair_is_found():
    amounts = [20, 70, 110, 150]
    assert find_pair(amounts, 90) == (0, 1)


@pytest.mark.hidden
def test_e1_no_pair_returns_none():
    amounts = [10, 20, 30]
    assert find_pair(amounts, 100) is None


@pytest.mark.hidden
def test_e1_duplicate_amounts_are_handled():
    amounts = [30, 30, 45]
    assert find_pair(amounts, 60) == (0, 1)


@pytest.mark.hidden
def test_e1_negative_amounts_are_valid_operations():
    amounts = [-10, 50, 60]
    assert find_pair(amounts, 50) == (0, 2)


@pytest.mark.hidden
def test_e1_any_valid_pair_is_accepted_when_several_exist():
    amounts = [5, 5, 10, 10]
    _assert_valid_pair(find_pair(amounts, 15), amounts, 15)
