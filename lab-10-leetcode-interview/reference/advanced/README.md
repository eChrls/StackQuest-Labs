# REFERENCE / SPOILER — Advanced

Executable reference behavior is enabled only for review with `LAB_REFERENCE_MODE=true` (the PowerShell runner exposes `-Reference`). The committed candidate baseline remains faulty.

- A1 reference serializes creation and returns the existing transfer for a repeated key. In a production multi-instance service, prefer a database `UNIQUE(idempotency_key)` constraint plus conflict-readback; application synchronization alone is not the scalable design answer.
- A2 reference rejects any transition away from a terminal state at the service/domain boundary. A production model should encode the full allowed-transition graph and use optimistic locking where concurrent state writers exist.
