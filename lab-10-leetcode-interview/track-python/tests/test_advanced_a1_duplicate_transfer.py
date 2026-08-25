import threading

import pytest

PAYLOAD = {"user_id": "user-1", "amount": "100.00"}
KEY = "idem-key-1"


@pytest.mark.public
def test_a1_double_submit_creates_a_single_transfer(client):
    first = client.post(
        "/api/transfers", json=PAYLOAD, headers={"Idempotency-Key": KEY}
    )
    second = client.post(
        "/api/transfers", json=PAYLOAD, headers={"Idempotency-Key": KEY}
    )

    assert first.status_code == 201
    assert second.status_code == 200
    assert first.json()["id"] == second.json()["id"]

    listed = client.get("/api/transfers").json()
    assert len(listed) == 1


@pytest.mark.hidden
def test_a1_concurrent_double_submit_creates_a_single_transfer(client):
    results = []

    def submit():
        response = client.post(
            "/api/transfers", json=PAYLOAD, headers={"Idempotency-Key": "idem-race"}
        )
        results.append(response)

    threads = [threading.Thread(target=submit) for _ in range(10)]
    for thread in threads:
        thread.start()
    for thread in threads:
        thread.join()

    assert all(r.status_code in (200, 201) for r in results)
    transfer_ids = {r.json()["id"] for r in results}
    assert len(transfer_ids) == 1

    listed = client.get("/api/transfers").json()
    matching = [t for t in listed if t["idempotency_key"] == "idem-race"]
    assert len(matching) == 1


@pytest.mark.hidden
def test_a1_different_idempotency_keys_create_two_transfers(client):
    first = client.post(
        "/api/transfers", json=PAYLOAD, headers={"Idempotency-Key": "key-a"}
    )
    second = client.post(
        "/api/transfers", json=PAYLOAD, headers={"Idempotency-Key": "key-b"}
    )

    assert first.status_code == 201
    assert second.status_code == 201
    assert first.json()["id"] != second.json()["id"]

    listed = client.get("/api/transfers").json()
    assert len(listed) == 2


@pytest.mark.hidden
def test_a1_missing_idempotency_key_is_rejected(client):
    response = client.post("/api/transfers", json=PAYLOAD)
    assert response.status_code == 400
