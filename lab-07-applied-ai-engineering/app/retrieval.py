from dataclasses import dataclass
import re

@dataclass(frozen=True)
class Chunk:
    source: str
    index: int
    text: str

class DeterministicRetriever:
    def __init__(self, documents: dict[str,str], chunk_size: int = 40):
        self.chunks = [Chunk(name, i, part) for name, doc in documents.items() for i, part in enumerate(_chunks(doc, chunk_size))]
    def search(self, query: str, k: int = 2) -> list[Chunk]:
        terms=set(re.findall(r"[a-z0-9]+", query.lower()))
        ranked=sorted(self.chunks,key=lambda c: len(terms & set(re.findall(r"[a-z0-9]+",c.text.lower()))),reverse=True)
        # Deliberate I1: returns a context even when score is zero.
        return ranked[:k]

def _chunks(text: str, size: int): return [text[i:i+size] for i in range(0,len(text),size)]

def answer(query: str, retriever: DeterministicRetriever) -> dict:
    hits=retriever.search(query)
    return {"answer": hits[0].text if hits else "I don't know", "citations": [f"{h.source}#{h.index}" for h in hits]}
