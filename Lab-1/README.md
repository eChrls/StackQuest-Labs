# Lab-1 — Reference Lab: Java/Spring Payments

Lab-1 is the reference implementation for Real-World Technical Interview Labs. It is an intentionally imperfect payments backend. The candidate receives an unfamiliar but runnable service, reproduces observable symptoms, follows evidence through the layers, makes the smallest justified change, and explains the result.

The normal material is candidate-first. Root causes and complete resolution guidance are in the collapsed Mentor / AI Support section.

## Why this Lab exists

This Lab teaches the real backend workflow:

    run → understand → reproduce → inspect evidence → hypothesize
    → change the smallest justified cause → verify → explain

Learning outcomes:

- navigate an unfamiliar Java/Spring codebase;
- use JUnit, Mockito, MockMvc, PostgreSQL and Flyway as evidence;
- debug HTTP → Controller → Service → Repository → PostgreSQL;
- handle BigDecimal, nullability, validation and query parameters;
- add a small REST feature without breaking existing behaviour;
- reason about query plans, idempotency and concurrency;
- review correctness, maintainability and production risk.

## Stack and architecture

- Java 21
- Spring Boot 3.5.4
- Maven 3.9.9 inside Docker
- Spring Web, Spring Data JPA and Bean Validation
- PostgreSQL 17-alpine
- Flyway 10.x, JUnit 5, Mockito and Spring Boot Test

    HTTP
      ↓
    Controller → Service → Repository → PostgreSQL
       ↓          ↓            ↓
      DTO      business      JPA / SQL
               rules

The domain contains Merchant and Payment. Payment has a UUID id, merchant, BigDecimal amount, status, nullable providerReference, and UTC createdAt. Statuses are PENDING, CAPTURED, FAILED and REFUNDED.

## Docker services

| Service | Purpose | Profile | Host ports |
| --- | --- | --- | --- |
| postgres | Development PostgreSQL | default | none |
| app | Development API | default | 127.0.0.1:18081 |
| postgres-test | Isolated test PostgreSQL | test | none |
| test | Runs Maven tests | test | none |
| debug | API with remote JVM debugging | debug | HTTP 18081, JDWP 15005 |

The databases are recreated from versioned Flyway migrations and seed data. Java, Maven and PostgreSQL do not need to be installed on the host.

## Quick start

From the repository root:

    cd Lab-1
    docker compose config
    docker compose up --build postgres app

The API is available at http://localhost:18081.

Run the isolated test environment:

    docker compose --profile test up --build --abort-on-container-exit --exit-code-from test test

When repeating a test run after tests have written data, recreate the ephemeral service:

    docker compose --profile test down
    docker compose --profile test up --build --abort-on-container-exit --exit-code-from test test

Debug mode:

    docker compose --profile debug up --build postgres debug

Attach an IDE to localhost:15005. Useful commands:

    docker compose ps
    docker compose logs -f app
    docker compose --profile test logs -f test
    docker compose --profile debug logs -f debug
    docker compose exec postgres psql -U lab1 -d lab1

## Seed data and endpoints

Seeded merchants are M1 (Merchant One) and M2 (Merchant Two). M1 contains:

| Status | Amount | Provider reference |
| --- | ---: | --- |
| CAPTURED | 100.00 | PROVIDER-123 |
| CAPTURED | 50.00 | PROVIDER-456 |
| FAILED | 30.00 | PROVIDER-789 |
| PENDING | 25.00 | NULL |

Available endpoints:

- POST /api/payments
- GET /api/payments/{paymentId}
- GET /api/merchants/{merchantId}/payments
- GET /api/merchants/{merchantId}/captured-total

I2, A1 and A2 are specifications for the candidate; their implementations are not in the baseline.

## Expected initial state

The baseline contains exactly 16 tests:

- 12 PASS;
- 4 NON-GREEN, one independent reproduction for each base ticket;
- no accidental infrastructure failure.

| Ticket | Evidence | Initial result |
| --- | --- | --- |
| E1 | PaymentBugBigDecimalTest | assertion failure |
| E2 | PaymentControllerSmokeTest | nullable mapping error |
| E3 | PaymentBugNegativeAmountTest | assertion failure |
| I1 | PaymentBugStatusFilterTest | Service-flow assertion failure |

PaymentBugPendingNullProviderTest documents that null is valid in the domain. It is not the E2 reproduction. Repository-only tests are not sufficient evidence for I1.

## Choose a track

Easy tickets take approximately 20–60 minutes and expose a visible starting point. Intermediate tickets take 45–120 minutes and require cross-layer reasoning. Advanced tickets take 60–180 minutes and require production-oriented performance, consistency or concurrency reasoning. Docker is infrastructure, not difficulty.

## Easy track

### E1 — Captured total is incorrect

Difficulty: Easy
Type: Debugging / monetary calculation
Suggested interview time: 30–45 min
Skills: BigDecimal, immutability, JUnit, debugger variables

Context: Merchant operations uses the captured-total endpoint to reconcile money received from providers.

Observed behaviour: The captured total for a merchant with captured payments does not match the database.

Expected behaviour: M1 returns 150.00. Failed and pending payments are excluded.

Reproduction: Run PaymentBugBigDecimalTest in the Docker test profile.

Constraints: Keep monetary values as BigDecimal. Do not change seed data or hard-code a response.

Acceptance criteria:

- the focused test and full suite pass after the candidate solution;
- the endpoint returns the exact monetary result;
- the candidate explains the observed value.

Starting point: PaymentService.calculateCapturedTotal. Inspect the value before and after one captured payment.

Hint 1: Use a debugger or focused test and compare total before and after one payment.

Hint 2: Follow the loop in the Service and confirm whether the returned value is the value calculated.

Hint 3: BigDecimal operations return a value; they do not mutate the receiver.

Guided debugging: Reproduce, set a breakpoint, inspect variables, step over the calculation, follow the call stack, form a hypothesis, make the smallest change, and rerun focused plus full tests.

Follow-up: What if amounts arrive with more than two decimal places? Would SQL aggregation be preferable at larger volume?

### E2 — Nullable provider reference breaks an API response

Difficulty: Easy
Type: Debugging / DTO mapping
Suggested interview time: 30–45 min
Skills: nullability, mapping, HTTP errors, stack traces

Context: Provider references are unavailable while a payment is pending.

Observed behaviour: Listing M1 payments fails instead of returning the pending payment.

Expected behaviour: The response succeeds and preserves a missing provider reference as JSON null or another justified compatible representation.

Reproduction: Run PaymentControllerSmokeTest or GET /api/merchants/M1/payments.

Constraints: Preserve the nullable domain and response shape. Do not seed a fake reference.

Acceptance criteria:

- pending payments can be listed;
- the reference remains absent rather than invented;
- mapping and HTTP evidence cover the behaviour;
- other response fields do not regress.

Starting point: Read the exception stack trace, then inspect the response mapping path.

Hint 1: Read the first application frame, not the framework frames.

Hint 2: The repository has returned the payment; inspect the mapping path after that.

Hint 3: Check a nullable value before calling an instance method on it.

Guided debugging: Reproduce, enable a NullPointerException breakpoint, inspect a pending row, step into the mapper, compare it with a captured row, then verify the HTTP contract.

Follow-up: Would you represent missing data as null, omission or a typed status object? Where should API-wide null policy live?

### E3 — Payment amount must be positive

Difficulty: Easy
Type: Debugging / validation
Suggested interview time: 30–45 min
Skills: Bean Validation, boundary conditions, HTTP 400

Context: The API must not accept zero or negative payments.

Observed behaviour: A request with 0.00 or -10.00 is accepted.

Expected behaviour: Both values are rejected with HTTP 400 before persistence.

Reproduction: Run PaymentBugNegativeAmountTest. It includes both values and a non-null provider reference so E2 cannot mask E3.

Constraints: Keep the rule at the request boundary. Do not rely only on a database or later exception.

Acceptance criteria:

- zero is rejected;
- negative values are rejected;
- a positive control remains accepted;
- invalid input produces useful HTTP 400 validation evidence;
- no invalid payment is persisted.

Starting point: Inspect PaymentCreateRequest annotations and the Controller validation path.

Hint 1: Compare amount constraints with other required fields.

Hint 2: Confirm @Valid is present and identify what boundary rule is missing.

Hint 3: The requirement is strictly greater than zero, not greater-than-or-equal-to zero.

Guided debugging: Run the focused MockMvc test, inspect the deserialized DTO, follow validation, and check whether the Service is reached for invalid input.

Follow-up: Would you also enforce the rule in the domain or database? How should API clients discover it?

## Intermediate track

### I1 — Payment status filter is ignored

Difficulty: Intermediate
Type: Debugging / cross-layer filtering
Suggested interview time: 60–90 min
Skills: query parameters, Controller → Service → Repository, regression testing

Context: Operations needs to list only payments in a requested lifecycle status.

Observed behaviour: A request supplies a status, but payments in other statuses are returned.

Expected behaviour: GET /api/merchants/{merchantId}/payments?status=CAPTURED returns only captured payments. Without the parameter, existing unfiltered behaviour remains.

Reproduction: Run PaymentBugStatusFilterTest. It invokes PaymentService with captured and failed rows. Do not use the repository-only test as the ticket reproduction.

Constraints: Preserve ordering and the no-filter contract. Avoid filtering only after loading unrelated results when the repository can express it.

Acceptance criteria:

- the focused Service-flow test passes;
- an HTTP integration test or equivalent evidence covers the query parameter;
- no-filter listing still works;
- unknown-status behaviour is tested or explicitly documented.

Hint 1: Compare the status received by the Controller with the repository method ultimately called.

Hint 2: Trace the argument through getPaymentsByMerchant; the defect is between receiving and choosing the data access operation.

Hint 3: Filtering must be represented in the query path, not silently discarded by orchestration.

Guided debugging: Break at the Service entry, inspect status, step into the repository call, compare the selected method, and verify filtered and unfiltered paths.

Follow-up: How should unknown status map to HTTP? Would a typed enum parameter be preferable to a raw string?

### I2 — Merchant Summary

Difficulty: Intermediate
Type: Feature / REST reporting
Suggested interview time: 75–120 min
Skills: domain modelling, DTO design, persistence, BigDecimal, unit and integration tests

Requirement: Add GET /api/merchants/{id}/summary returning:

    {
      "merchantId": "M1",
      "capturedCount": 2,
      "capturedTotal": 150.00,
      "failedCount": 1
    }

Context: Merchant operations wants one small summary endpoint for reconciliation and triage.

Expected behaviour: The summary counts captured and failed payments and calculates captured money exactly. A missing merchant has deliberate, tested HTTP behaviour.

Constraints: Keep the feature small, preserve existing contracts, add at least one unit and one integration test, and do not build a reporting framework.

Acceptance criteria:

- M1 returns the documented values;
- BigDecimal exactness is covered;
- merchant-not-found behaviour is tested;
- the existing baseline contract is not silently changed;
- the design is explainable.

Starting point: Map the existing Merchant/Payment relationships and repository methods.

Hint 1: Write down which fields derive from status and which layer should own each decision.

Hint 2: Compare a small service solution with repository aggregation and justify the simplest correct design.

Hint 3: Treat the summary as a stable response DTO, not an entity serialization shortcut.

Follow-up: One query or several? What should an empty summary return?

## Advanced track

### A1 — PostgreSQL reporting performance

Dependency:

    I2 Merchant Summary
            ↓
    A1 PostgreSQL / Reporting Performance

A1 assumes I2 has been solved functionally. The intended progression is: first implement a correct small feature, then investigate how to make it suitable for significantly larger volume.

Difficulty: Advanced
Type: SQL / performance investigation
Suggested interview time: 90–150 min
Skills: SQL aggregation, PostgreSQL, EXPLAIN, query plans, exact money

Requirement: Evolve Merchant Summary so PostgreSQL performs the aggregation without unnecessarily loading every Payment entity into application memory.

Constraints: Do not use an absolute latency threshold. Do not change the summary contract. Add an index only when plan evidence justifies it.

Acceptance criteria:

- values remain exact and correct;
- evidence shows database-side aggregation;
- EXPLAIN or EXPLAIN ANALYZE is used;
- functional correctness is distinguished from structural suitability;
- result and query behaviour are tested.

Optional hints: Inspect rows/entities loaded by the current implementation; compare conditional aggregates with application iteration; inspect aggregate nodes, rows examined and index effects.

Follow-up: When does a composite index help? How would this report be monitored in production?

### A2 — Idempotent concurrent capture callbacks

Difficulty: Advanced
Type: Concurrency / payment consistency
Suggested interview time: 120–180 min
Skills: retries, transactions, unique constraints, locking, PostgreSQL consistency

Context: A local provider callback may be retried or delivered concurrently. It must apply one business capture for one event without a real PSP or Internet.

Requirement: Design a local callback contract with eventId and payment reference. The first event may transition a pending payment to captured; retries, including concurrent deliveries, are idempotent.

Constraints: Use PostgreSQL and Docker tests. Do not build a distributed system. Do not test concurrency only with sequential calls. Define incompatible reuse of an event ID.

Acceptance criteria:

- first callback succeeds;
- sequential retry has one business effect;
- duplicate concurrent delivery is reproducible;
- one durable idempotency record exists;
- payment state and provider reference are consistent;
- the test proves concurrency.

Optional hints: Identify the durable unique fact and transaction boundary; coordinate two PostgreSQL workers with a barrier; select and justify constraints, locks and isolation.

Follow-up: What response should a retry receive? What if an event belongs to another payment? What if commit succeeds but the caller times out?

## Standard solving workflow

1. Confirm Docker and PostgreSQL health.
2. Run the focused test or request.
3. Record expected versus actual.
4. Find the earliest divergence.
5. Trace the layer boundary.
6. Form a falsifiable hypothesis.
7. Make the smallest justified change.
8. Add or improve regression evidence.
9. Run focused and full tests.
10. Explain cause, trade-offs and one production concern.

## Debugger from zero

Use the debug profile and attach to localhost:15005.

- Breakpoint: pauses before a selected line.
- Resume/continue: runs to the next breakpoint or exception.
- Step over: executes the line without entering called code.
- Step into: enters called code across layers.
- Step out: returns from the current method.
- Variables: inspect current state.
- Watches: evaluate a useful expression.
- Call stack: shows how the request or test arrived.
- Exception breakpoint: stops at the useful exception frame.
- JUnit debugging: run one test in debug mode with a breakpoint in the code under test.

The practical loop is reproduce → breakpoint → inspect → step → compare → follow stack → hypothesize → verify.

## Tests as evidence

Tests are reproduction, acceptance criteria, debugging evidence and regression protection. A documented red test is intentional; it does not mean Docker is broken. Do not change an expected value just to make the build green, hard-code seed data, or bypass the relevant layer.

## Modes

Learning Mode: hints, documentation and mentor/AI teaching are available, with no mandatory timebox. The objective is understanding.

Interview Mode: use the suggested timebox, start with the public brief, ask for hints explicitly, and explain reasoning. Documentation is allowed unless a ticket says otherwise.

Review Mode: explain root cause, walk through the diff, justify the design, discuss an alternative, identify a production risk, and answer follow-up questions.

## Definition of Done

A ticket is complete only when applicable criteria are met: relevant tests pass; existing behaviour does not regress; Docker and application startup remain valid; contracts are compatible; edge cases are covered; data is consistent; the solution is explainable; and one production concern is identified. A single visible assertion must not be passed with a shortcut.

## Infrastructure versus challenge

| Symptom | First check | Interpretation |
| --- | --- | --- |
| No containers | docker compose ps | Start the documented profile. |
| PostgreSQL unavailable | health status and pg_isready logs | Infrastructure until healthy. |
| Flyway failure | migration and application logs | Fix environment before ticket debugging. |
| Port occupied | 18081 or 15005 ownership | Stop conflict or use a documented alternative. |
| Maven error | image build and Maven output | Separate dependency/build from challenge failure. |
| JDWP unavailable | debug logs and port 15005 | Confirm debug profile and JVM listener. |
| Healthy environment plus focused assertion/error | test and stack trace | Likely challenge evidence. |

## Mentor / AI Support — SPOILERS

<details>
<summary>Open only for mentoring, review or an explicitly requested solution.</summary>

### Verified base root causes

E1: PaymentService calls BigDecimal.add without assigning its returned value. BigDecimal is immutable, so the accumulator remains zero. Assign the result or use an equivalent exact reduction.

E2: PaymentMapper calls trim on a nullable providerReference. The pending seed row legitimately contains SQL NULL. Make mapping null-safe without inventing data.

E3: PaymentCreateRequest has NotNull but no strictly-positive constraint. Add a boundary rule equivalent to DecimalMin(0.00, exclusive) and test zero, negative and positive controls.

I1: PaymentService receives a status but calls the unfiltered repository method. Select the status-aware query or equivalent correct design. The permanent evidence crosses the Service boundary.

### Verified temporary solutions

I2 was temporarily validated with a summary DTO, endpoint, service/repository design, Mockito unit test and PostgreSQL MockMvc integration test. M1 returned 2, 150.00 and 1.

A1 was validated as the next step after I2 with PostgreSQL conditional COUNT/SUM aggregation, a projection, exact results and EXPLAIN evidence showing an aggregate plan node. No millisecond threshold or unjustified index was used.

A2 was temporarily validated with POST /api/payments/{paymentId}/capture-callback, eventId, providerReference, a unique durable event table, a transaction, a payment row lock, sequential retry and two coordinated concurrent PostgreSQL workers. The result was one event row and one captured business state.

### Common wrong fixes

- changing expected money to zero;
- converting BigDecimal to double;
- replacing null with fake text;
- discarding status in another layer;
- rejecting negative values but accepting zero;
- hard-coding M1 summary values;
- claiming performance from a local stopwatch;
- storing idempotency in process memory;
- simulating concurrency with sequential calls;
- recording idempotency outside the payment transaction.

### Full-resolution outline

1. Assign the BigDecimal result.
2. Make provider mapping null-safe.
3. Enforce amount strictly greater than zero.
4. Select the status-aware repository path.
5. Add I2 DTO, endpoint, service design and unit/integration tests.
6. Evolve I2 to database aggregation and inspect EXPLAIN for A1.
7. Add durable unique event recording, transaction and concurrency proof for A2.
8. Review alternatives, constraints and observability.

### Validation matrix

| Ticket | Baseline evidence | Temporary validation | Accepted result |
| --- | --- | --- | --- |
| E1 | PaymentBugBigDecimalTest | 16/16 after fix | exact 150.00 |
| E2 | HTTP listing test | 16/16 after fix | successful nullable response |
| E3 | zero/negative MockMvc test | 16/16 after fix | HTTP 400, no persistence |
| I1 | Service-flow test | 16/16 after fix | requested status only |
| I2 | feature specification | 18/18 | DTO plus unit/integration |
| A1 | feature specification | 20/20 with plan evidence | aggregation and exact result |
| A2 | feature specification | 20/20 with concurrent evidence | one business effect |

### Agent continuity context

A new agent can immediately identify the baseline, all seven tickets, their difficulty, reproduction, hints, the I2 → A1 dependency, A2's concurrency proof, infrastructure checks, four root causes, accepted behaviour, review questions and the full-resolution outline without rediscovering the codebase.

</details>

## Final baseline contract

The committed challenge state contains no I2, A1 or A2 implementation. It retains four production defects and four independent evidence tests. Temporary solutions, migrations, generated target files, logs and solution patches do not belong in the baseline.

This README is the Lab-specific continuity source. Global documentation and the master roadmap are maintained by their dedicated documentation work.
