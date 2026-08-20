from datetime import datetime
from decimal import Decimal
from typing import Literal

from pydantic import BaseModel, ConfigDict, Field


class PaymentRecord(BaseModel):
    model_config = ConfigDict(extra="forbid")

    source_event_id: str = Field(min_length=1)
    merchant_id: str = Field(min_length=1)
    amount: Decimal | None = None
    currency: str = Field(min_length=3, max_length=3)
    status: Literal["CAPTURED", "FAILED", "PENDING", "REFUNDED"]
    created_at: datetime


class IngestionResponse(BaseModel):
    accepted: int
    rejected: int
    inserted: int


class MerchantReport(BaseModel):
    merchant_id: str
    status: str
    count: int
    total: Decimal
