from decimal import Decimal

import pytest

from app.transaction_summary import summarize_transactions


@pytest.mark.public
def test_e2_example_summary_only_counts_approved():
    lines = [
        "2026-08-20T10:00|user-1|25.50|APPROVED",
        "2026-08-20T10:02|user-2|40.00|DECLINED",
        "2026-08-20T10:05|user-1|15.50|APPROVED",
    ]
    assert summarize_transactions(lines) == {"user-1": Decimal("41.00")}


@pytest.mark.hidden
def test_e2_malformed_lines_are_skipped_without_failing_the_batch():
    lines = [
        "not-a-valid-line",
        "2026-08-20T10:00|user-1|10.00|APPROVED",
        "2026-08-20T10:01|user-2|abc|APPROVED",
        "2026-08-20T10:02|user-1|5.00|APPROVED",
    ]
    assert summarize_transactions(lines) == {"user-1": Decimal("15.00")}


@pytest.mark.hidden
def test_e2_empty_input_returns_empty_summary():
    assert summarize_transactions([]) == {}


@pytest.mark.hidden
def test_e2_user_with_no_approved_transaction_is_excluded():
    lines = [
        "2026-08-20T10:00|user-1|25.50|DECLINED",
        "2026-08-20T10:01|user-1|10.00|PENDING",
    ]
    assert summarize_transactions(lines) == {}


@pytest.mark.hidden
def test_e2_status_match_is_case_sensitive():
    lines = ["2026-08-20T10:00|user-1|25.50|approved"]
    assert summarize_transactions(lines) == {}
