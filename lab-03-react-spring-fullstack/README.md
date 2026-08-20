# Lab 03 — React + Spring Full-Stack

Lab 03 is a full-stack debugging and feature lab that combines a React dashboard with a Spring Boot payments API and PostgreSQL. The candidate starts from a runnable but imperfect application, reproduces symptoms using test and browser evidence, tracks the cause across the stack, and then explains the smallest justified fix.

The normal material is candidate-first. Root causes and the complete resolution guidance are in the collapsed Mentor / AI Support section.

## Why this Lab exists

This Lab teaches the real full-stack workflow:

    run → understand → reproduce → inspect evidence → hypothesize
    → change the smallest justified cause → verify → explain

Learning outcomes:

- navigate an unfamiliar React + Spring codebase;
- use Docker, browser DevTools, tests and logs as evidence;
- debug frontend → API → Controller → Service → Repository → PostgreSQL;
- reason about HTTP status codes, nullable data, state updates and page contracts;
- apply small but correct fixes without masking unrelated behaviour;
- add a small feature that crosses the full stack without breaking the baseline;
- review correctness, maintainability and production risk in an end-to-end system.

## Stack and architecture

- Frontend: React 19, TypeScript, Vite, Node.js 22 LTS, Vitest and React Testing Library
- Backend: Java 21, Spring Boot 3.5.4, Spring Web, Spring Data JPA, Bean Validation, Flyway, PostgreSQL 17-alpine, JUnit 5 and Mockito
- Infrastructure: Docker and Docker Compose

    Browser
      │ HTTP / fetch
      ↓
    React + TypeScript
      │
      ↓
    Spring Boot REST
      │
      ↓
    Controller → Service → Repository → PostgreSQL

### Frontend architecture

```text
Page
 ↓
Component / Hook
 ↓
API module
 ↓
HTTP
```

### Backend architecture

```text
Controller
 ↓
Service
 ↓
Repository
 ↓
PostgreSQL
```

## Docker services

```text
Host
│
├── localhost:13003
│        ↓
│     frontend
│        │ browser fetch
│        ↓
├── localhost:18083
│        ↓
│     backend
│        ↓
│     postgres
│
└── localhost:15007
         ↓
    backend-debug JDWP

test profile:

backend-test → postgres-test
frontend-test
```

The browser calls `http://localhost:18083`; it never calls the Docker hostname `backend`.

## Docker services reference

- `postgres`: development PostgreSQL plus Flyway migrations.
- `backend`: Spring Boot app in development mode.
- `frontend`: Vite + React + TypeScript app.
- `postgres-test`: isolated PostgreSQL for the test profile.
- `backend-test`: Maven suite against the ephemeral test database.
- `frontend-test`: isolated Vitest suite against the app logic.
- `backend-debug`: Spring Boot with JDWP enabled for remote debugging.

## Volumes

`postgres_data`, `maven_cache`, `frontend_node_modules`.

## Ports

- Frontend: `127.0.0.1:13003` → `http://localhost:13003`
- Backend: `127.0.0.1:18083` → `http://localhost:18083`
- JDWP: `127.0.0.1:15007` → `localhost:15007`
- PostgreSQL is not exposed on the host

## Quick start

From the repository root, or from inside the lab folder:

```bash
docker compose up --build
docker compose down
docker compose ps
docker compose config
docker compose logs -f backend
docker compose logs -f frontend
docker compose --profile test run --rm backend-test
docker compose --profile test run --rm frontend-test
docker compose --profile test run --rm backend-test mvn test -Dspring.profiles.active=test --no-transfer-progress
docker compose --profile test run --rm frontend-test npm test -- --run
docker compose --profile debug up --build backend-debug postgres
```

For psql without exposing PostgreSQL to the host:

```bash
docker compose exec postgres psql -U lab3 -d lab3
```

No local Java, Maven, Node, npm or PostgreSQL installation is required.

## Project structure

```text
backend/src/main/java/com/lab3/
├── config  controller  domain  dto  exception
├── mapper  repository  service
└── Lab3Application.java
backend/src/main/resources/db/migration/
frontend/src/
├── api  components  hooks  pages  test  types
├── App.tsx  main.tsx  style.css
```

## Domain

- `Merchant`: `id`, `name`, `active`
- `Payment`: UUID, merchant, amount, status, `createdAt`, nullable description
- `PaymentStatus`: `PENDING`, `CAPTURED`, `FAILED`, `REFUNDED`
- `PaymentAudit`: payment UUID, previous status, new status and createdAt

## API contracts

- `GET /api/merchants`
- `GET /api/merchants/{merchantId}/payments?status&page&size` with zero-based `page` and default `size=5`
- `GET /api/payments/{paymentId}`
- `PATCH /api/payments/{paymentId}/status` with `{ "status": "CAPTURED" }`; responds `204 No Content`
- `GET /api/payments/{paymentId}/audit`
- `GET /api/merchants/{merchantId}/summary` for the later feature track

The paginated response shape includes `content`, `page`, `size`, `totalElements` and `totalPages`. CORS is restricted to `http://localhost:13003`.

## Seed data

Flyway creates three merchants and 16 payments. Useful IDs include:

- M1 (`Northstar Market`): 12 payments, including `00000000-0000-0000-0000-000000000003`, `...0007` and `...0010` in `PENDING`
- M2 (`Cedar & Co`): payments `...0013`, `...0014`, `...0015`
- M3 (`Atlas Goods`): payment `...0016`
- Payment `...0001` is used for the captured-detail reproduction; `...0005` is a failed payment

## Expected initial state

The baseline is intentionally imperfect and intentionally reproducible.

```text
Backend:
18 tests
15 passing
3 failing

Frontend:
14 tests
11 passing
3 failing

Total:
32 tests
26 passing
6 failing
```

Those six failures are deliberate evidence and are the starting point of the challenge.

## Choose a track

Easy tickets focus on a visible user-facing defect or contract mismatch. Intermediate tickets require cross-layer reasoning and service-level correctness. Advanced tickets require production-oriented reasoning, database behaviour, or concurrency guarantees.

Docker health is infrastructure, not difficulty.

## Easy track

### T1 — Pagination mismatch

Difficulty: Easy
Type: Frontend / HTTP contract
Suggested interview time: 20–40 min
Skills: page semantics, API contracts, fetch, state and browser evidence

Context: The payment list uses a page index that does not match the API contract.

Observed behaviour: Moving to the next page shows the wrong set or repeats data.

Expected behaviour: The next page shows the next five payments and respects the zero-based API contract.

Reproduction: run the frontend tests or exercise the app manually and observe the page navigation behaviour.

Constraints: Preserve the API response contract; do not hard-code page values.

Acceptance criteria:

- the visual page and backend page remain aligned;
- no duplicate or skipped payments appear across page transitions;
- the focused test passes and the app remains coherent when navigating pages.

Starting point: inspect the caller that sets the page state and the request builder that serializes the query string.

Hint 1: Compare the visual page value with the zero-based API page contract.

Hint 2: Inspect the query string that is sent for the next page, not only the UI label.

Hint 3: Check whether the page number is being incremented before or after the API contract is respected.

Guided debugging: reproduce in a browser or test, inspect the fetch URL, compare the visual page with the API parameter, and verify the server-side page number that is actually requested.

Follow-up: What happens when a backend contract changes from zero-based to one-based? Where should the conversion live?

### T2 — Successful update still shows an error

Difficulty: Easy
Type: Frontend / HTTP response handling
Suggested interview time: 20–35 min
Skills: fetch, status codes, no-content responses, error handling, browser Network/Console

Context: The backend accepts an update and returns `204 No Content`.

Observed behaviour: The UI reports an error even though the HTTP request succeeded.

Expected behaviour: A successful HTTP response is treated as success; the UI should not expect a JSON body that does not exist.

Reproduction: trigger a capture or failed-status action and inspect the response in DevTools or the test harness.

Constraints: Preserve the API contract and handle the successful empty-body response correctly.

Acceptance criteria:

- the client does not call `.json()` on a no-content response;
- the action is shown as successful without an error toast;
- the error path still handles genuinely failed HTTP requests.

Starting point: inspect the `updatePaymentStatus` API function and the status-change handler in the React app.

Hint 1: Check the response code before trying to parse JSON.

Hint 2: A `204 No Content` response has no response body; treat it as a success, not as a parse failure.

Hint 3: Compare the request path and the UI behaviour when the backend rejects an invalid transition.

Guided debugging: reproduce the successful update, inspect the fetch response in the browser or test output, verify whether a status is present, and ensure the UI handles the success path separately from the error path.

Follow-up: Should the client treat all empty-body successful responses the same way, or should the API return a more explicit payload?

### T3 — Status change does not reflect immediately in the detail panel

Difficulty: Easy
Type: Frontend / state management
Suggested interview time: 20–45 min
Skills: state, immutability, React render flow and user-visible stale state

Context: The detail panel shows stale data after a valid action.

Observed behaviour: The payment detail keeps the previous status until a manual refresh or second fetch.

Expected behaviour: Once a valid transition succeeds, the panel reflects the new status immediately.

Reproduction: select a pending payment, trigger a capture or fail action, and inspect the detail pane immediately.

Constraints: Ensure the UI reflects the new source of truth without needing a forced reload.

Acceptance criteria:

- the UI updates immediately after a successful action;
- the detail panel stays synchronized with the server state;
- no stale status is displayed after the action.

Starting point: inspect the `changeStatus` function and the selected-payment state used in the detail view.

Hint 1: Do not mutate the object currently being rendered in place without re-deriving the state.

Hint 2: Check whether the component is relying on a stale object reference after an update.

Hint 3: Compare the success path with the data-loading path for the detail panel.

Guided debugging: reproduce the UI change, inspect the selected item before and after the action, compare the object reference or rendered value, and verify whether the state has been re-derived.

Follow-up: When should the UI fetch fresh state after mutation, and when is local state sufficient?

### T4 — Merchant name is wrong in payment detail

Difficulty: Easy
Type: Backend / mapping contract
Suggested interview time: 20–45 min
Skills: DTO design, mapper correctness, boundary contracts and domain-to-response translation

Context: The payment detail exposes the wrong merchant name.

Observed behaviour: `merchantId` and `merchantName` do not represent the correct merchant.

Expected behaviour: The response should expose the actual merchant identity rather than duplicating the ID or using the wrong source of truth.

Reproduction: inspect the detail response or run the relevant frontend/backend assertions against the payment DTO.

Constraints: Keep the API contract intact while correcting the mapping source.

Acceptance criteria:

- the correct merchant name appears in the payment detail;
- the mapped identifier reflects the actual merchant;
- no unrelated field is changed in the payload.

Starting point: inspect the mapping layer that builds the payment DTO.

Hint 1: Check whether the mapper populates `merchantId` and `merchantName` from the expected source object.

Hint 2: Look for a duplicated field assignment rather than a real merchant lookup.

Hint 3: Compare the mapped object with the domain relationship and the merchant projection used elsewhere.

Guided debugging: inspect the payment object before mapping, follow the DTO construction, compare the merchant names and IDs, and verify the contract in the response JSON.

Follow-up: Where should the merchant identity resolution live: in the domain, the mapper or the service?

## Intermediate track

### T5 — Payment and audit updates are not atomic

Difficulty: Intermediate
Type: Transactional integrity / backend consistency
Suggested interview time: 45–90 min
Skills: transaction boundaries, rollback, repository contracts, service orchestration and PostgreSQL consistency

Context: Payment status and related audit rows are updated in separate operations.

Observed behaviour: If the audit write fails after the payment write, the payment may be left in a partially updated state.

Expected behaviour: The payment update and the audit insert should behave as one transactional unit.

Reproduction: use the backend test that simulates an audit failure during status update and verifies that the payment remains unchanged.

Constraints: Preserve the public API and keep the business operation atomic.

Acceptance criteria:

- a failing audit write leaves the payment state unchanged;
- the service contract is consistent with the database state;
- the business operation is explainable in review without relying on implicit best effort.

Starting point: inspect the status-update service method and the repository transaction boundary.

Hint 1: Compare the update and audit writes; they happen in separate steps and must not diverge.

Hint 2: Trace the transaction boundary to verify whether then-consequence and rollback are aligned.

Hint 3: Check whether the audit failure can leave a payment row mutated without a compensating action.

Guided debugging: reproduce the failing audit scenario, inspect the service order, set a breakpoint before and after each repository write, and confirm whether a rollback or transaction boundary is missing.

Follow-up: What would a production-grade audit trail look like when the database and the external system are both involved?

### T6 — Invalid status transitions are accepted

Difficulty: Intermediate
Type: Business rule validation / HTTP contract
Suggested interview time: 45–90 min
Skills: domain rules, HTTP status codes, request validation and service guardrails

Context: Some transitions should be rejected even though the payload is syntactically valid.

Observed behaviour: Status changes that should fail are accepted and stored.

Expected behaviour: Only `PENDING -> CAPTURED` and `PENDING -> FAILED` are valid. Other transitions return `409 Conflict`.

Reproduction: call the status-update endpoint with an invalid transition and observe the backend behaviour.

Constraints: Keep the rule in the business logic and return an explicit conflict for invalid state changes.

Acceptance criteria:

- invalid transitions are rejected with `409 Conflict`;
- valid transitions remain accepted;
- the rule is enforced before persistence.

Starting point: inspect the status-transition guard and the endpoint contract for `PATCH /api/payments/{id}/status`.

Hint 1: Check the exact rules currently implemented against the business requirement.

Hint 2: Verify whether the code allows a transition that should be rejected.

Hint 3: Confirm that the operation returns the right HTTP status for a state conflict.

Guided debugging: reproduce the invalid transition, inspect the current payment status and requested status, confirm the decision point in the service, and verify the HTTP contract on failure.

Follow-up: Should the server also reject invalid transitions at the API input-validation layer, or only in the domain service?

### T7 — Merchant Summary

Difficulty: Intermediate
Type: Feature / end-to-end reporting
Suggested interview time: 60–120 min
Skills: domain modelling, DTO design, service/repository orchestration, frontend state and full-stack integration tests

Requirement: add `GET /api/merchants/{merchantId}/summary` and show a merchant summary card that includes the selected merchant identity and correct counts/total amounts.

Context: The dashboard needs a compact view of merchant reconciliation data for a selected merchant.

Observed behaviour: The feature is not implemented in the delivered baseline.

Expected behaviour: A merchant summary loads correctly, handles loading and error states, and shows the right values for the selected merchant.

Reproduction: the feature is introduced once the six defect tickets are resolved; it is validated as an end-to-end addition.

Constraints: Keep the feature small, preserve existing contracts and add focused evidence.

Acceptance criteria:

- the feature is implemented in backend and frontend;
- at least one backend unit and one backend integration test cover it;
- at least one frontend API/hook test and one component test cover it;
- the UI handles loading, error and success states correctly.

Starting point: map the existing merchant and payment data, then implement the smallest summary contract that satisfies the feature and its tests.

Hint 1: Decide which fields are derived from status and which fields belong in the response DTO.

Hint 2: Compare a small service implementation against repository-side aggregation and pick the simplest correct design.

Hint 3: Treat the summary as a stable DTO contract rather than a serialization shortcut from the entity layer.

Guided debugging: reproduce the missing summary feature, inspect the merchant and payment data, follow the response-building flow, and verify the frontend loading and rendering path.

Follow-up: Should the summary be computed in application code or directly in PostgreSQL for larger datasets?

## Advanced track

### A1 — PostgreSQL reporting performance

Dependency: T7 Merchant Summary

Difficulty: Advanced
Type: SQL / performance investigation
Suggested interview time: 90–150 min
Skills: SQL aggregation, PostgreSQL plans, `EXPLAIN`, index justification and exact money handling

Requirement: evolve the summary feature so that PostgreSQL performs the data aggregation instead of repeatedly loading all matching payment entities into application memory.

Context: A correct summary is necessary before a performance-oriented redesign can be justified.

Observed behaviour: The repository does not currently include a technically validated A1 implementation.

Expected behaviour: The summary remains exact, and the query plan is evaluated with evidence rather than with a local stopwatch or guesswork.

Reproduction: this track is a follow-on once T7 is complete; it is not a baseline implementation and is not treated as done in this repository.

Constraints: do not invent result evidence; no millisecond threshold is required; no index is added without plan evidence.

Acceptance criteria:

- the summary remains correct and exact;
- the database side performs the aggregation;
- `EXPLAIN` or `EXPLAIN ANALYZE` is used as evidence;
- performance discussion distinguishes correctness from structural suitability.

Starting point: inspect the T7 query path, identify how the summary is currently derived, and compare the application-side loop with an aggregate query.

Hint 1: Inspect the rows loaded by the current implementation and compare them with a single aggregate query.

Hint 2: Check whether `COUNT` and `SUM` can be composed with conditional filters in the database.

Hint 3: Review the query plan nodes, row counts and index effects before adding a structural optimization.

Guided debugging: reproduce the summary path, inspect query execution, compare the application model to the database aggregate plan, and validate only with evidence from the database layer.

Follow-up: When does a composite index help, and how would a production system monitor this report over time?

### A2 — Idempotent concurrent capture callbacks

Dependency: T5 and T6

Difficulty: Advanced
Type: Concurrency / consistency
Suggested interview time: 120–180 min
Skills: retry safety, transaction model, unique constraints, PostgreSQL consistency and concurrent proof

Requirement: provide a durable contract for a provider callback or retried event so that the same event cannot apply more than once even under concurrency.

Context: A capture callback may be retried or delivered concurrently. The implementation must preserve one business effect.

Observed behaviour: There is no technically validated A2 implementation in the current repository, and it should not be described as done.

Expected behaviour: The first valid callback succeeds, retries are idempotent, concurrent duplicates do not create two business effects, and the state remains consistent.

Reproduction: this is an advanced follow-on track that requires a durable unique event record, transaction boundaries and a real concurrency test.

Constraints: do not build a distributed system; do not rely on in-memory idempotency; do not simulate concurrent delivery with sequential calls only.

Acceptance criteria:

- first callback succeeds;
- a retry is harmless;
- duplicate concurrent delivery is reproducible in a test;
- one durable idempotency record exists;
- payment state and provider reference remain internally consistent.

Starting point: inspect the status transition path and the audit domain, then define the durable unique fact that establishes idempotency.

Hint 1: Identify the durable unique business fact that distinguishes a retry from a new event.

Hint 2: Use a transaction boundary that covers both the business state and the idempotency record.

Hint 3: Run a real concurrency test with multiple PostgreSQL workers, not a loop of sequential calls.

Guided debugging: define the event record, create the transaction boundary, run two coordinated workers against the same payment, inspect the resulting rows, and validate that one event row and one business state mutation remain.

Follow-up: What should a retry response look like? What if the event belongs to another payment? What if the client times out after the commit?

## Standard solving workflow

1. Confirm Docker and PostgreSQL health.
2. Run the focused failing test or reproduce the browser symptom.
3. Record expected versus actual behaviour.
4. Follow the earliest divergence across the stack.
5. Form a falsifiable hypothesis at the boundary where the data first changes.
6. Make the smallest justified change.
7. Add or improve regression evidence.
8. Run focused and full validation.
9. Explain cause, trade-off and one production concern.

## Debugger from zero

Use the debug profile and attach to `localhost:15007`.

- Breakpoint: pauses before a selected line.
- Resume/continue: runs to the next breakpoint or exception.
- Step over: executes the line without entering called code.
- Step into: enters called code across layers.
- Step out: returns from the current method.
- Variables: inspect current state.
- Watches: evaluate a useful expression.
- Call stack: shows how request or test arrived.
- Exception breakpoint: stops at the useful exception frame.
- JUnit debugging: run one test in debug mode with a breakpoint in the code under test.

The practical loop is reproduce → breakpoint → inspect → step → compare → follow stack → hypothesize → verify.

## Tests as evidence

Tests are reproduction, acceptance criteria, debugging evidence and regression protection. A documented red test is intentional; it is not an infrastructure failure. Do not change an expected value just to make the build green, hard-code data, or bypass the relevant layer.

The baseline is intentionally `26 PASS / 6 FAIL` in a deliberate challenge state. A temporary corrected state can reach `32/32 PASS`, and the T7 feature has been validated in the local challenge flow with `36/36 PASS`. Those values are part of the challenge evidence, but they must not be confused with the delivered baseline state of the challenge repository.

## Modes

Learning Mode: hints, documentation and mentor/AI teaching are available, with no mandatory timebox. The objective is understanding.

Interview Mode: use the suggested timebox, start with the public brief, ask for hints explicitly, and explain the reasoning. Documentation is allowed unless a ticket says otherwise.

Review Mode: explain the root cause, walk through the diff, justify the design, discuss an alternative, identify a production risk, and answer follow-up questions.

## Definition of Done

A ticket is complete only when the applicable criteria are met: relevant tests pass, existing behaviour does not regress, startup remains valid, contracts are compatible, edge cases are covered, data remains consistent, and the solution is explainable. A single visible assertion must not be passed with a shortcut.

## Infrastructure versus challenge

| Symptom | First check | Interpretation |
| --- | --- | --- |
| No containers | `docker compose ps` | Infrastructure until healthy |
| PostgreSQL unavailable | health status and logs | Infrastructure until healthy |
| Flyway failure | migration and application logs | Fix environment before ticket debugging |
| Port occupied | 13003 / 18083 / 15007 ownership | Stop conflict or use a documented alternative |
| Vite or Maven error | image build and runtime output | Separate dependency/build from challenge failure |
| Browser cannot reach backend | frontend URL and backend logs | Likely environment or network issue |
| Healthy environment plus focused assertion/error | test and stack trace | Likely challenge evidence |

## Mentor / AI Support — SPOILERS

<details>
<summary>Open only for mentoring, review or an explicitly requested solution.</summary>

### Verified base root causes

T1: the frontend page counter does not match the API contract. The backend uses zero-based pages while the UI increments a visual page number and sends the wrong value.

T2: the update endpoint returns `204 No Content`, but the frontend tries to parse JSON. The fix is to treat empty-body success as a success path and not as a failure to decode.

T3: the detail panel mutates the current selected object in place instead of re-deriving the state from the latest payload. This leaves the UI rendered with stale data.

T4: the mapper duplicates the `merchantId` field and does not resolve the actual `merchantName` from the domain relationship. The DTO is built from the wrong source.

T5: `PaymentService.updateStatus` updates the payment row and then writes the audit in an unconstrained sequence. If the audit write fails, the payment may already be changed.

T6: the transition guard is too permissive and fails to restrict the allowed state machine. The domain should only permit `PENDING -> CAPTURED` and `PENDING -> FAILED`, returning `409 Conflict` otherwise.

### Validated T7 resolution summary

The T7 feature has been validated as a full-stack flow with a backend summary endpoint, frontend summary card, and end-to-end tests. The key pattern is a small DTO contract plus focused backend and frontend evidence.

### Common wrong fixes

- changing the API page contract to match the UI instead of fixing the caller;
- calling `.json()` on a no-content success response;
- mutating a selected object in place to mask stale render state;
- returning the merchant ID where the merchant name is expected;
- writing the audit after a payment mutation without a transaction boundary;
- allowing invalid transitions by checking only one direction of the state machine;
- hard-coding the summary response instead of deriving it from the domain data.

### Full-resolution outline

1. Fix page semantics and align frontend state with the API contract.
2. Handle 204 success responses correctly in the client.
3. Re-derive selected state instead of mutating stale objects.
4. Repair the payment DTO mapping for merchant identity.
5. Enforce atomic payment + audit updates inside a transaction.
6. Validate only the allowed status transitions and return conflict for invalid ones.
7. Add the T7 summary DTO, endpoint, UI and regression tests.
8. Review alternatives and production concerns.

### Validation matrix

| Ticket | Baseline evidence | Temporary corrected state | Accepted result |
| --- | --- | --- | --- |
| T1 | frontend pagination bug | 32/32 pass | page contract aligned |
| T2 | fetch JSON on 204 | 32/32 pass | success path handled correctly |
| T3 | stale detail state | 32/32 pass | immediate UI reflection |
| T4 | mapper mismatch | 32/32 pass | correct merchant identity |
| T5 | audit failure scenario | 32/32 pass | atomic payment + audit |
| T6 | invalid transition | 32/32 pass | `409 Conflict` on invalid moves |
| T7 | feature specification | 36/36 pass | summary contract and UI proven |

### A1 / A2 status

A1 and A2 are advanced follow-on tracks that are documented here as pending technical validation. They are not treated as done in the repository baseline, and no claim of verified implementation is made in this README.

### Agent continuity context

A new agent can immediately identify the baseline, the six deliberate defect tickets, the T7 feature addition, the difference between challenge baseline and temporary corrected state, the infrastructure checks, public-facing hints and guided debugging instructions, and the exact review questions to ask without rediscovering the codebase.

</details>

## Final baseline contract

The committed challenge state contains six deliberate defects and no implemented T7 feature. A temporary corrected state can reach `32/32 PASS`, and the T7 feature can be validated at `36/36 PASS`, but those states are not the same as the delivered baseline.

This README is the Lab-specific continuity source. It is intentionally candidate-first in the public section and contains the real root-cause and resolution guidance only in the collapsed Mentor / AI Support section.

This lab is intentionally independent from the Java reference lab and should be read as a full-stack debugging challenge with a later feature addition, not as a direct copy of the backend-only ticket structure.
