from datetime import datetime, timezone
from decimal import Decimal

from app.models import Payment


def test_health_reports_service_ready(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "ok"


def test_health_exposes_internal_elasticsearch_address(client):
    assert client.get("/health").json()["elasticsearch_url"] == "http://elasticsearch:9200"


def test_valid_record_can_be_ingested(client, db, valid_payload):
    response = client.post("/api/ingest", json=[valid_payload])
    assert response.status_code == 200
    assert response.json() == {"accepted": 1, "rejected": 0, "inserted": 1}
    assert db.query(Payment).count() == 1


def test_invalid_status_is_rejected(client, db, valid_payload):
    valid_payload["status"] = "UNKNOWN"
    response = client.post("/api/ingest", json=[valid_payload])
    assert response.json() == {"accepted": 0, "rejected": 1, "inserted": 0}
    assert db.query(Payment).count() == 0


def test_report_returns_requested_status(client, db):
    db.add(Payment(source_event_id="report-1", merchant_id="M-REPORT", amount=Decimal("7.00"), currency="EUR", status="FAILED", created_at=datetime.now(timezone.utc)))
    db.commit()
    response = client.get("/api/merchants/M-REPORT/report?status=FAILED")
    assert response.json() == {"merchant_id": "M-REPORT", "status": "FAILED", "count": 1, "total": "7.00"}


def test_missing_merchant_returns_not_found(client, db):
    response = client.get("/api/merchants/M-MISSING/report")
    assert response.status_code == 404
