from decimal import Decimal

from sqlalchemy import inspect

from app.db import engine
from app.models import Payment


def test_migration_creates_payments_table():
    assert "payments" in inspect(engine).get_table_names()


def test_payment_table_has_event_and_amount_columns():
    columns = {column["name"] for column in inspect(engine).get_columns("payments")}
    assert {"source_event_id", "merchant_id", "amount", "status"}.issubset(columns)


def test_ingestion_preserves_decimal_amount(client, db, valid_payload):
    response = client.post("/api/ingest", json=[valid_payload])
    assert response.status_code == 200
    assert db.query(Payment).first().amount == Decimal("12.50")
