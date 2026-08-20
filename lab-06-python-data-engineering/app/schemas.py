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


class IncrementalIngestionResponse(BaseModel):
    accepted: int
    rejected: int
    duplicated: int
    inserted: int


class ReconciliationReport(BaseModel):
    expected_count: int
    persisted_count: int
    expected_total: Decimal
    persisted_total: Decimal
    expected_statuses: dict[str, dict[str, int | Decimal]]
    persisted_statuses: dict[str, dict[str, int | Decimal]]
    discrepancies: list[str]


class ElasticsearchSyncResponse(BaseModel):
    synced: int


class MerchantReport(BaseModel):
    merchant_id: str
    status: str
    count: int
    total: Decimal


class MerchantLeaderboardEntry(BaseModel):
    merchant_id: str
    count: int
    total: Decimal


class MerchantLeaderboardReport(BaseModel):
    status: str
    entries: list[MerchantLeaderboardEntry]


class RecoveryReconcileReport(BaseModel):
    checked: int
    missing: list[str]
    stale: list[str]


class RecoveryRepairReport(BaseModel):
    repaired: int
    skipped: int
