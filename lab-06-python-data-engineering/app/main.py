import json
import os
from pathlib import Path
from decimal import Decimal

import httpx
from fastapi import Depends, FastAPI, HTTPException, Query
from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.db import SessionLocal
from app.models import Payment
from app.schemas import (
    ElasticsearchSyncResponse,
    IncrementalIngestionResponse,
    IngestionResponse,
    MerchantReport,
    PaymentRecord,
    ReconciliationReport,
)

app = FastAPI(title="StackQuest Lab-06")
DATASET_PATH = Path(__file__).parent / "data" / "payments.json"
ELASTICSEARCH_INDEX = "lab6-payments"


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.get("/health")
def health():
    return {"status": "ok", "elasticsearch_url": os.getenv("ELASTICSEARCH_URL", "http://localhost:9200")}


def load_dataset() -> list[dict]:
    return json.loads(DATASET_PATH.read_text())


def elasticsearch_url() -> str:
    return os.getenv("ELASTICSEARCH_URL", "http://localhost:9200")


def payment_document(payment: Payment) -> dict:
    return {
        "source_event_id": payment.source_event_id,
        "merchant_id": payment.merchant_id,
        "amount": str(payment.amount),
        "currency": payment.currency,
        "status": payment.status,
        "created_at": payment.created_at.isoformat(),
    }


@app.post("/api/ingest", response_model=IngestionResponse)
def ingest(records: list[dict] | None = None, db: Session = Depends(get_db)):
    raw_records = load_dataset() if records is None else records
    accepted = 0
    rejected = 0
    inserted = 0
    for raw_record in raw_records:
        try:
            record = PaymentRecord.model_validate(raw_record)
        except Exception:
            rejected += 1
            continue
        accepted += 1
        payment = Payment(
            source_event_id=record.source_event_id,
            merchant_id=record.merchant_id,
            amount=record.amount if record.amount is not None else Decimal("0.00"),
            currency=record.currency,
            status=record.status,
            created_at=record.created_at,
        )
        db.add(payment)
        inserted += 1
    db.commit()
    return IngestionResponse(accepted=accepted, rejected=rejected, inserted=inserted)


@app.post("/api/ingest/incremental", response_model=IncrementalIngestionResponse)
def ingest_incremental(records: list[dict], db: Session = Depends(get_db)):
    accepted = 0
    rejected = 0
    duplicated = 0
    inserted = 0
    valid_records = []
    for raw_record in records:
        try:
            record = PaymentRecord.model_validate(raw_record)
        except Exception:
            rejected += 1
            continue
        valid_records.append(record)
        if db.scalar(select(Payment.id).where(Payment.source_event_id == record.source_event_id)) is not None:
            duplicated += 1
            continue
        accepted += 1
        db.add(Payment(
            source_event_id=record.source_event_id,
            merchant_id=record.merchant_id,
            amount=record.amount if record.amount is not None else Decimal("0.00"),
            currency=record.currency,
            status=record.status,
            created_at=record.created_at,
        ))
        inserted += 1
    db.commit()
    return IncrementalIngestionResponse(
        accepted=accepted,
        rejected=rejected,
        duplicated=duplicated,
        inserted=len(valid_records),
    )


@app.post("/api/reconcile", response_model=ReconciliationReport)
def reconcile(records: list[dict], db: Session = Depends(get_db)):
    valid_records = []
    for raw_record in records:
        try:
            valid_records.append(PaymentRecord.model_validate(raw_record))
        except Exception:
            continue

    expected_statuses: dict[str, dict[str, int | Decimal]] = {}
    for record in valid_records:
        summary = expected_statuses.setdefault(record.status, {"count": 0, "total": Decimal("0.00")})
        summary["count"] += 1
        summary["total"] += record.amount if record.amount is not None else Decimal("0.00")

    persisted_statuses: dict[str, dict[str, int | Decimal]] = {}
    for status, count, total in db.execute(
        select(Payment.status, func.count(Payment.id), func.sum(Payment.amount))
        .where(Payment.status == "CAPTURED")
        .group_by(Payment.status)
    ).all():
        persisted_statuses[status] = {"count": count, "total": total}

    expected_count = len(valid_records)
    expected_total = sum((record.amount or Decimal("0.00") for record in valid_records), Decimal("0.00"))
    persisted_count = sum(summary["count"] for summary in persisted_statuses.values())
    persisted_total = sum((summary["total"] for summary in persisted_statuses.values()), Decimal("0.00"))
    discrepancies = []
    if expected_count != persisted_count:
        discrepancies.append("count mismatch")
    if expected_total != persisted_total:
        discrepancies.append("total mismatch")
    if expected_statuses != persisted_statuses:
        discrepancies.append("status mismatch")
    return ReconciliationReport(
        expected_count=expected_count,
        persisted_count=persisted_count,
        expected_total=expected_total,
        persisted_total=persisted_total,
        expected_statuses=expected_statuses,
        persisted_statuses=persisted_statuses,
        discrepancies=discrepancies,
    )


@app.post("/api/sync", response_model=ElasticsearchSyncResponse)
def sync_to_elasticsearch(db: Session = Depends(get_db)):
    payments = db.scalars(select(Payment).where(Payment.status == "CAPTURED")).all()
    synced = 0
    for payment in payments:
        response = httpx.put(
            f"{elasticsearch_url()}/{ELASTICSEARCH_INDEX}/_doc/{payment.source_event_id}",
            json=payment_document(payment),
            timeout=5,
        )
        response.raise_for_status()
        synced += 1
    return ElasticsearchSyncResponse(synced=synced)


@app.get("/api/search")
def search_elasticsearch(source_event_id: str,):
    response = httpx.post(
        f"{elasticsearch_url()}/{ELASTICSEARCH_INDEX}/_search",
        json={"query": {"term": {"source_event_id.keyword": source_event_id}}},
        timeout=5,
    )
    if response.status_code == 404:
        return {"count": 0, "payments": []}
    response.raise_for_status()
    hits = response.json().get("hits", {}).get("hits", [])
    return {"count": len(hits), "payments": [hit["_source"] for hit in hits]}


@app.get("/api/merchants/{merchant_id}/report", response_model=MerchantReport)
def merchant_report(
    merchant_id: str,
    status: str = Query("CAPTURED"),
    db: Session = Depends(get_db),
):
    count, total = db.execute(
        select(func.count(Payment.id), func.coalesce(func.sum(Payment.amount), 0))
        .where(Payment.merchant_id == merchant_id)
    ).one()
    if count == 0:
        raise HTTPException(status_code=404, detail="merchant has no payments")
    return MerchantReport(merchant_id=merchant_id, status=status, count=count, total=total)
