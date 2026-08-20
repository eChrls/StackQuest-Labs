from app.models import Payment


def test_i1_mixed_replay_inserts_only_new_events(client, db, valid_payload):
    existing = dict(valid_payload, source_event_id="i1-existing")
    new_record = dict(valid_payload, source_event_id="i1-new", amount="18.00")

    first = client.post("/api/ingest/incremental", json=[existing])
    second = client.post("/api/ingest/incremental", json=[existing, new_record])

    assert first.json() == {"accepted": 1, "rejected": 0, "duplicated": 0, "inserted": 1}
    assert second.json() == {"accepted": 1, "rejected": 0, "duplicated": 1, "inserted": 1}
    assert db.query(Payment).count() == 2
