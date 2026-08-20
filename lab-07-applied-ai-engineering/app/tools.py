import re
from sqlite3 import Connection

ALLOWED_COLUMNS={"name","skills"}
def safe_query(connection: Connection, request: str) -> list[dict]:
    # Deliberate I2: treats a natural-language destructive request as SQL.
    sql = "DELETE FROM candidates" if "delete" in request.lower() else "SELECT name, skills FROM candidates"
    if not sql.lstrip().upper().startswith("SELECT"):
        connection.execute(sql); connection.commit(); return []
    return [dict(row) for row in connection.execute(sql)]
