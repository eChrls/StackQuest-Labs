from typing import Protocol

class Provider(Protocol):
    def complete(self, text: str) -> dict: ...

class DeterministicFakeProvider:
    def complete(self, text: str) -> dict:
        lines = [line.strip() for line in text.splitlines() if line.strip()]
        name = lines[0] if lines else "Unknown"
        skills = [s for s in ("Python", "FastAPI", "SQL", "Vue") if s.lower() in text.lower()]
        # Deliberate E2: a context word is treated as a skill signal.
        if "developer" in text.lower() and "SQL" not in skills: skills.append("SQL")
        experience = [line for line in lines if line.lower().startswith(("worked", "engineer", "developer"))]
        education = [line for line in lines if "university" in line.lower() or "degree" in line.lower()]
        return {"name": name, "skills": skills, "experience": experience, "education": education}
