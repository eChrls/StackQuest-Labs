from fastapi import FastAPI
from .models import ExtractRequest, Resume
from .extractor import ResumeExtractor
from .provider import DeterministicFakeProvider
from .retrieval import DeterministicRetriever, answer
from .safety import authorize_tool

app = FastAPI(title="Applied AI Resume Extractor")
extractor = ResumeExtractor(DeterministicFakeProvider())
retriever = DeterministicRetriever({"handbook.txt":"Candidates must provide Python experience.","policy.txt":"Remote work is allowed."})

@app.get("/health")
def health(): return {"status": "ok", "provider": "deterministic-fake"}

@app.post("/extract", response_model=Resume)
def extract(request: ExtractRequest): return extractor.extract(request.text)

@app.get("/search")
def search(q: str): return answer(q, retriever)

@app.post("/tool/check")
def tool_check(user: str, tool: str, text: str): return {"allowed": authorize_tool(user, tool, text)}
