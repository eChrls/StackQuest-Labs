from app.models import Payment


def test_e1_invalid_dataset_row_is_rejected(client, db):
    response = client.post("/api/ingest")
    assert response.status_code == 200
    assert response.json() == {"accepted": 5, "rejected": 1, "inserted": 5}
    assert db.query(Payment).count() == 5
