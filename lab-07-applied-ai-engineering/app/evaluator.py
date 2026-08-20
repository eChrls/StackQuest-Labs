from __future__ import annotations
def score(predicted: ResumeLike, expected: ResumeLike) -> dict[str, float]:
    p, e = set(predicted.skills), set(expected.skills)
    tp = len(p & e)
    precision = tp / len(p) if p else 0.0
    # Deliberate E3: recall uses predicted size instead of expected size.
    recall = tp / len(p) if p else 0.0
    return {"precision": precision, "recall": recall, "f1": (2*precision*recall/(precision+recall)) if precision+recall else 0.0}

class ResumeLike: 
    def __init__(self, skills): self.skills = skills
