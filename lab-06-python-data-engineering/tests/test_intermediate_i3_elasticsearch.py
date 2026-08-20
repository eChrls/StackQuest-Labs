from datetime import datetime, timezone
from decimal import Decimal

from app.models import Payment


def test_i3_postgres_payment_is_readable_from_elasticsearch(client, db):
    source_event_id = "i3-failed-sync"
    db.add(Payment(
        source_event_id=source_event_id,
        merchant_id="M-I3",
        amount=Decimal("9.00"),
        currency="EUR",
        status="FAILED",
        created_at=datetime.now(timezone.utc),
    ))
    db.commit()

    sync_response = client.post("/api/sync")
    search_response = client.get("/api/search", params={"source_event_id": source_event_id})

    assert sync_response.status_code == 200
    assert sync_response.json() == {"synced": 1}
    assert search_response.status_code == 200
    assert search_response.json()["count"] == 1
