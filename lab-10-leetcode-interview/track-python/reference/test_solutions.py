from decimal import Decimal
from reference.solutions import balanced_events, fraud_clusters, growth_streak, pair_transactions, scheduling, transaction_summary

def test_reference_easy_public_and_evaluator():
    assert pair_transactions([20,70,110],90)==(0,1)
    assert pair_transactions([1,2],9) is None
    assert transaction_summary(["t|u|1.00|APPROVED","bad"])=={"u":Decimal("1.00")}
    assert balanced_events("PAY[OK]") and not balanced_events("([)]")

def test_reference_intermediate_public_and_evaluator():
    assert growth_streak([7,2,8,3,9,4,10,1,11])==5
    assert growth_streak([])==0
    assert fraud_clusters([["1","0"],["0","1"]])==2
    assert fraud_clusters([])==0
    assert scheduling([(1,3),(2,4),(3,5),(5,7)])==3
    assert scheduling([])==0
