from datetime import datetime, timezone
from decimal import Decimal

from app.models import Payment


def test_i2_reconciliation_reports_status_and_total_mismatch(client, db):
    expected = [
        {
            "source_event_id": "i2-captured",
            "merchant_id": "M-I2",
            "amount": "100.00",
            "currency": "EUR",
            "status": "CAPTURED",
            "created_at": "2026-03-01T10:00:00Z",
        },
        {
            "source_event_id": "i2-failed",
            "merchant_id": "M-I2",
            "amount": "30.00",
            "currency": "EUR",
            "status": "FAILED",
            "created_at": "2026-03-01T10:05:00Z",
        },
    ]
    db.add_all([
        Payment(source_event_id="i2-captured", merchant_id="M-I2", amount=Decimal("100.00"), currency="EUR", status="CAPTURED", created_at=datetime.now(timezone.utc)),
        Payment(source_event_id="i2-failed", merchant_id="M-I2", amount=Decimal("10.00"), currency="EUR", status="FAILED", created_at=datetime.now(timezone.utc)),
    ])
    db.commit()

    response = client.post("/api/reconcile", json=expected)

    assert response.status_code == 200
    body = response.json()
    assert body["expected_statuses"] == {
        "CAPTURED": {"count": 1, "total": "100.00"},
        "FAILED": {"count": 1, "total": "30.00"},
    }
    assert body["persisted_statuses"] == {
        "CAPTURED": {"count": 1, "total": "100.00"},
        "FAILED": {"count": 1, "total": "10.00"},
    }
    assert body["discrepancies"] == ["total mismatch", "status mismatch"]
