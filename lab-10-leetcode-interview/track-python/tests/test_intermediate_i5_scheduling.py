import pytest
from app.scheduling import max_non_overlapping

@pytest.mark.public
def test_i5_selects_maximum_windows(): assert max_non_overlapping([(1,3),(2,4),(3,5),(5,7)]) == 3
@pytest.mark.evaluator
def test_i5_touching_windows_are_compatible(): assert max_non_overlapping([(0,2),(2,3)]) == 2
@pytest.mark.evaluator
def test_i5_empty_schedule(): assert max_non_overlapping([]) == 0
