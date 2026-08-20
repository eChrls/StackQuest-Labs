import json
from pathlib import Path
from app.extractor import ResumeExtractor
from app.provider import DeterministicFakeProvider
from app.evaluator import score, ResumeLike

CASES=json.loads(Path('data/eval.json').read_text())

def test_e1_structured_output_has_typed_lists():
    class Malformed:
        def complete(self, text): return {"name":"Ada", "skills":"Python", "experience":[], "education":[]}
    result=ResumeExtractor(Malformed()).extract(CASES[0]['text'])
    assert isinstance(result.skills,list)

def test_e2_extraction_matches_relevant_skills():
    result=ResumeExtractor(DeterministicFakeProvider()).extract(CASES[1]['text'])
    assert result.skills == ["FastAPI"]

def test_e3_recall_uses_expected_denominator():
    metrics=score(ResumeLike(["Python"]),ResumeLike(["Python","SQL"]))
    assert metrics["recall"] == 0.5
