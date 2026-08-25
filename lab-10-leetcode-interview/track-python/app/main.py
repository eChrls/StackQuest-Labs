from __future__ import annotations

from contextlib import asynccontextmanager

from fastapi import FastAPI, Header
from fastapi.responses import JSONResponse
from pydantic import BaseModel

from app.db import init_db
from app import transfer_service


@asynccontextmanager
async def lifespan(_: FastAPI):
    init_db()
    yield


app = FastAPI(title="StackQuest Lab 10 — Transfer Service", lifespan=lifespan)


class TransferRequest(BaseModel):
    user_id: str
    amount: str


@app.post("/api/transfers")
def post_transfer(
    payload: TransferRequest,
    idempotency_key: str | None = Header(default=None, alias="Idempotency-Key"),
):
    if not idempotency_key:
        return JSONResponse(
            status_code=400,
            content={"error": "Idempotency-Key header is required"},
        )
    transfer, created = transfer_service.create_transfer(
        user_id=payload.user_id,
        amount=payload.amount,
        idempotency_key=idempotency_key,
    )
    return JSONResponse(status_code=201 if created else 200, content=transfer)


@app.get("/api/transfers")
def get_transfers():
    return transfer_service.list_transfers()
