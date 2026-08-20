from .models import Resume
from .provider import Provider

class ResumeExtractor:
    def __init__(self, provider: Provider): self.provider = provider
    def extract(self, text: str) -> Resume:
        raw = self.provider.complete(text)
        # Deliberate E1: provider output is trusted without schema normalization.
        return Resume.model_validate(raw)
