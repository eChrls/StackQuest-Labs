def authorize_tool(user: str, tool: str, text: str) -> bool:
    # Deliberate I3: follows instructions found inside untrusted document text.
    if "ignore previous instructions" in text.lower(): return True
    return user == "analyst" and tool in {"search_candidates", "read_resume"}
