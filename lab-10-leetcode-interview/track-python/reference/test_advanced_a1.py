from fastapi.testclient import TestClient

from app.db import reset_db
from app.main import app


def test_reference_a1_is_durable_and_returns_existing_row(tmp_path, monkeypatch):
    monkeypatch.setenv("TRANSFER_DB_PATH", str(tmp_path / "reference.db"))
    monkeypatch.setenv("LAB_REFERENCE_MODE", "true")
    reset_db()
    with TestClient(app) as client:
        first = client.post("/api/transfers", json={"user_id": "u", "amount": "10.00"}, headers={"Idempotency-Key": "k"})
        second = client.post("/api/transfers", json={"user_id": "u", "amount": "10.00"}, headers={"Idempotency-Key": "k"})
        assert first.status_code == 201
        assert second.status_code == 200
        assert first.json()["id"] == second.json()["id"]
        assert len(client.get("/api/transfers").json()) == 1
