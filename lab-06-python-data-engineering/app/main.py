import json
import os
from pathlib import Path
from decimal import Decimal

from fastapi import Depends, FastAPI, HTTPException, Query
from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.db import SessionLocal
from app.models import Payment
from app.schemas import IngestionResponse, MerchantReport, PaymentRecord

app = FastAPI(title="StackQuest Lab-06")
DATASET_PATH = Path(__file__).parent / "data" / "payments.json"


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
