from datetime import datetime, timezone
from decimal import Decimal

from app.models import Payment


def test_e3_report_aggregates_only_requested_status(client, db):
    db.add_all([
        Payment(source_event_id="agg-1", merchant_id="M-AGG", amount=Decimal("100.00"), currency="EUR", status="CAPTURED", created_at=datetime.now(timezone.utc)),
        Payment(source_event_id="agg-2", merchant_id="M-AGG", amount=Decimal("50.00"), currency="EUR", status="CAPTURED", created_at=datetime.now(timezone.utc)),
        Payment(source_event_id="agg-3", merchant_id="M-AGG", amount=Decimal("30.00"), currency="EUR", status="FAILED", created_at=datetime.now(timezone.utc)),
    ])
    db.commit()
    response = client.get("/api/merchants/M-AGG/report?status=CAPTURED")
    assert response.status_code == 200
    assert response.json() == {"merchant_id": "M-AGG", "status": "CAPTURED", "count": 2, "total": "150.00"}
