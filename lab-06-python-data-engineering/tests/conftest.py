import pytest
from fastapi.testclient import TestClient

from app.db import SessionLocal
from app.main import app
from app.models import Payment


@pytest.fixture()
def client():
    with TestClient(app) as test_client:
        yield test_client


@pytest.fixture()
def db():
    session = SessionLocal()
    session.query(Payment).delete()
    session.commit()
    try:
        yield session
    finally:
        session.close()


@pytest.fixture()
def valid_payload():
    return {
        "source_event_id": "focused-1",
        "merchant_id": "M-FOCUS",
        "amount": "12.50",
        "currency": "EUR",
        "status": "CAPTURED",
        "created_at": "2026-02-01T12:00:00Z",
    }
