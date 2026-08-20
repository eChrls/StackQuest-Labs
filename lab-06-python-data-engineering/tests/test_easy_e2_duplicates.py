from app.models import Payment


def test_e2_reingesting_same_events_is_idempotent(client, db, valid_payload):
    second_payload = dict(valid_payload, source_event_id="focused-2")
    records = [valid_payload, second_payload]
    first = client.post("/api/ingest", json=records)
    second = client.post("/api/ingest", json=records)
    assert first.json()["inserted"] == 2
    assert second.json()["inserted"] == 0
    assert db.query(Payment).count() == 2
