# A2 evidence — unlock after phase 7 Testing

Attempt: record in `interviewer/session-log-template.md`.

The audit export contains `PENDING → COMPLETED → PENDING` for one transfer. A downstream settlement job treats every `PENDING` row as actionable, so reopening a terminal transfer can cause it to be processed again.
