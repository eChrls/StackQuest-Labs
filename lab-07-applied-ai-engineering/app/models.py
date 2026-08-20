from pydantic import BaseModel, Field

class Resume(BaseModel):
    name: str
    skills: list[str] = Field(default_factory=list)
    experience: list[str] = Field(default_factory=list)
    education: list[str] = Field(default_factory=list)

class ExtractRequest(BaseModel):
    text: str = Field(min_length=1)
