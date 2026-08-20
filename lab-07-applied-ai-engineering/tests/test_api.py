from fastapi.testclient import TestClient
from app.main import app

def test_health_is_offline(): assert TestClient(app).get('/health').json()['provider']=='deterministic-fake'
