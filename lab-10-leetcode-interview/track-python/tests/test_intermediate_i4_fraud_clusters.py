import pytest

from app.fraud_clusters import count_fraud_clusters


@pytest.mark.public
def test_i4_example_cluster_count():
    grid = [
        ["1", "1", "0", "0"],
        ["1", "1", "0", "0"],
        ["0", "0", "1", "0"],
        ["0", "0", "0", "1"],
    ]
    assert count_fraud_clusters(grid) == 3


@pytest.mark.hidden
def test_i4_diagonal_cells_are_not_connected():
    grid = [
        ["1", "0"],
        ["0", "1"],
    ]
    assert count_fraud_clusters(grid) == 2


@pytest.mark.hidden
def test_i4_empty_grid_has_no_clusters():
    assert count_fraud_clusters([]) == 0


@pytest.mark.hidden
def test_i4_all_clean_grid_has_no_clusters():
    grid = [
        ["0", "0"],
        ["0", "0"],
    ]
    assert count_fraud_clusters(grid) == 0
