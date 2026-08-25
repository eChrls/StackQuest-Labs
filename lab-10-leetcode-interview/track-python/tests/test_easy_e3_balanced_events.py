import pytest
from app.balanced_events import balanced_events

@pytest.mark.public
def test_e3_nested_events(): assert balanced_events("PAYMENT[AUTH{OK}]")
@pytest.mark.evaluator
def test_e3_crossed_events(): assert not balanced_events("([)]")
@pytest.mark.evaluator
def test_e3_empty_is_balanced(): assert balanced_events("")
