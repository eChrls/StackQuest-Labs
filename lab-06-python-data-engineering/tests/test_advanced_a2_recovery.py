import os
from datetime import datetime, timezone
from decimal import Decimal

import httpx

from app.models import Payment

ELASTICSEARCH_INDEX = "lab6-payments"


def _elasticsearch_url() -> str:
    return os.getenv("ELASTICSEARCH_URL", "http://localhost:9200")


def _index_document(source_event_id: str, document: dict) -> None:
    response = httpx.put(
        f"{_elasticsearch_url()}/{ELASTICSEARCH_INDEX}/_doc/{source_event_id}",
        json=document,
        params={"refresh": "wait_for"},
        timeout=5,
    )
    response.raise_for_status()


def _delete_document(source_event_id: str) -> None:
    httpx.delete(
        f"{_elasticsearch_url()}/{ELASTICSEARCH_INDEX}/_doc/{source_event_id}",
        params={"refresh": "wait_for"},
        timeout=5,
    )


def test_a2_partial_sync_failure_recovers_without_duplication_or_stale_data(client, db):
    # Elasticsearch is not wiped between test runs like PostgreSQL is (see the `db`
    # fixture), so a prior run's repair could leave these ids already indexed.
    # Clear them first so the scenario below is reproducible on a warm environment.
    for source_event_id in ("a2-synced-ok", "a2-missing", "a2-stale"):
        _delete_document(source_event_id)

    now = datetime.now(timezone.utc)

    synced_ok = Payment(
        source_event_id="a2-synced-ok",
        merchant_id="M-A2",
        amount=Decimal("40.00"),
        currency="EUR",
        status="CAPTURED",
        created_at=now,
    )
    never_synced = Payment(
        source_event_id="a2-missing",
        merchant_id="M-A2",
        amount=Decimal("25.00"),
        currency="EUR",
        status="CAPTURED",
        created_at=now,
    )
    corrected_after_sync = Payment(
        source_event_id="a2-stale",
        merchant_id="M-A2",
        amount=Decimal("130.00"),
        currency="EUR",
        status="REFUNDED",
        created_at=now,
    )
    db.add_all([synced_ok, never_synced, corrected_after_sync])
    db.commit()

    # simulate a sync that ran to completion for two payments before the worker crashed:
    # "a2-synced-ok" is indexed correctly, "a2-stale" was indexed before a later correction
    # updated its amount/status in PostgreSQL, and "a2-missing" never got indexed at all.
    _index_document("a2-synced-ok", {
        "source_event_id": "a2-synced-ok",
        "merchant_id": "M-A2",
        "amount": "40.00",
        "currency": "EUR",
        "status": "CAPTURED",
        "created_at": now.isoformat(),
    })
    _index_document("a2-stale", {
        "source_event_id": "a2-stale",
        "merchant_id": "M-A2",
        "amount": "100.00",
        "currency": "EUR",
        "status": "PENDING",
        "created_at": now.isoformat(),
    })

    reconcile = client.post("/api/elasticsearch/reconcile").json()
    assert reconcile["checked"] == 3
    assert set(reconcile["missing"]) == {"a2-missing"}
    assert set(reconcile["stale"]) == {"a2-stale"}

    repair_first = client.post("/api/elasticsearch/repair").json()
    assert repair_first == {"repaired": 2, "skipped": 1}

    reconcile_after_repair = client.post("/api/elasticsearch/reconcile").json()
    assert reconcile_after_repair == {"checked": 3, "missing": [], "stale": []}

    # replaying the repair must be a safe no-op: nothing left to fix, nothing duplicated
    repair_second = client.post("/api/elasticsearch/repair").json()
    assert repair_second == {"repaired": 0, "skipped": 3}

    search = client.get("/api/search", params={"source_event_id": "a2-stale"}).json()
    assert search["count"] == 1
    assert search["payments"][0]["amount"] == "130.00"
    assert search["payments"][0]["status"] == "REFUNDED"
