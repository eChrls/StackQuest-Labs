import pytest
from fastapi.testclient import TestClient

from app.db import reset_db
from app.main import app


@pytest.fixture()
def client(tmp_path, monkeypatch):
    db_file = tmp_path / "transfers-test.db"
    monkeypatch.setenv("TRANSFER_DB_PATH", str(db_file))
    reset_db()
    with TestClient(app) as test_client:
        yield test_client
