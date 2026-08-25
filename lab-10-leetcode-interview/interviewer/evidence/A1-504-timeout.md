# A1 evidence — unlock after phase 7 Testing

Attempt: record in `interviewer/session-log-template.md`.

A client timed out after 504 and retried the same transfer with the same idempotency key. Both requests reached the service and two rows were created. The first request may have committed even though the client never received its response.
