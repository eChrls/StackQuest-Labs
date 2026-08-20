from fastapi import FastAPI
from .models import ExtractRequest, Resume
from .extractor import ResumeExtractor
from .provider import DeterministicFakeProvider

app = FastAPI(title="Applied AI Resume Extractor")
extractor = ResumeExtractor(DeterministicFakeProvider())

@app.get("/health")
def health(): return {"status": "ok", "provider": "deterministic-fake"}

@app.post("/extract", response_model=Resume)
def extract(request: ExtractRequest): return extractor.extract(request.text)
