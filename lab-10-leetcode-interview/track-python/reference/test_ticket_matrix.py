from decimal import Decimal

from reference.solutions import balanced_events, fraud_clusters, growth_streak, pair_transactions, scheduling, transaction_summary


def test_reference_e1():
    assert pair_transactions([20, 70, 110], 90) == (0, 1)


def test_reference_e2():
    assert transaction_summary(["t|u|1.00|APPROVED"]) == {"u": Decimal("1.00")}


def test_reference_e3():
    assert balanced_events("PAY[OK]") and not balanced_events("([)]")


def test_reference_i1():
    assert growth_streak([7, 2, 8, 3, 9, 4, 10, 1, 11]) == 5


def test_reference_i4():
    assert fraud_clusters([["1", "0"], ["0", "1"]]) == 2


def test_reference_i5():
    assert scheduling([(1, 3), (2, 4), (3, 5), (5, 7)]) == 3
