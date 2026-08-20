# Lab 04 — Angular & Spring Enterprise

Docker-first Angular/TypeScript + Java 21/Spring Boot + PostgreSQL lab focused on Reactive Forms, RxJS, HttpClient, REST/DTO contracts, validation, JPA transactions, guards/interceptors and frontend/backend debugging. Distinct from Lab 03: the core exercise is contract and state-flow reasoning.

## Stack and commands

`frontend/` is Angular standalone with strict TypeScript, Reactive Forms and RxJS. `backend/` is Spring Boot 3.5.4, Maven 3.9.9, JPA, Flyway and PostgreSQL 17. Compose provides `postgres`, `backend`, `frontend` and isolated `postgres-test`/`backend-test`; HTTP uses ports 18084/14204 and PostgreSQL is never exposed. Run `docker compose config`, then `docker compose up --build postgres backend frontend`; tests use `docker compose --profile test run --rm backend-test` and `cd frontend && npm install && npm run build && npm test`.

V1 seeds active/inactive customers and one HIGH/OPEN support request. Baseline has four independent red tests: two backend contract/transaction tickets and two frontend form/HTTP-state tickets. Infrastructure failures are separate.

## Easy

Starting Point: run Compose, inspect `SupportRequestController` → `SupportRequestService`, open the Angular form and reproduce one focused test.

**E1 — HIGH request cannot close.** Observed: seeded HIGH request remains OPEN. Expected: valid request transitions to CLOSED atomically. Hints: (1) inspect the service branch, (2) step through DTO/entity mutation, (3) verify transaction and persisted status.

**E2 — Reactive Form validation.** Observed: empty subject reaches the submit path. Expected: invalid form blocks submission and exposes validation state. Hints: (1) inspect validators, (2) observe `form.invalid`, (3) test the boundary rather than only the template.

## Intermediate

**I1 — RxJS HTTP state.** A failed close can leave stale success messaging. Model loading/success/error as mutually consistent states; inspect `catchError`, retry and cancellation with HttpClient tests. Hints: (1) model stream states, (2) place error handling deliberately, (3) assert no duplicate side effects.

**I2 — validation and authorization boundary.** The endpoint must enforce DTO validation, inactive-customer rejection and stable 4xx errors while controllers stay thin. Hints: (1) separate transport/domain rules, (2) test inactive customers, (3) use a transaction and explicit exception handler.

## Learning / Interview / Review

Learning follows breakpoints and focused tests. Interview mode requires explaining evidence, DTO choices, RxJS lifecycle and transaction boundaries. Review mode checks strict typing, accessibility, test isolation, security defaults, rollback and minimal diffs.

### Mentor / AI context

Root causes are intentionally hidden from candidates: E1 service state transition; E2 form boundary; I1 stream/error handling; I2 validation/authorization layering. Equivalent designs are valid if REST contract, atomicity and UI states are preserved. Never weaken a red assertion.

## Validation matrix

| Area | Evidence |
|---|---|
| Compose | `docker compose config`; healthy PostgreSQL |
| Backend | Maven compile/testCompile, Flyway V1, Spring context |
| Frontend | strict TypeScript build and Angular server |
| Baseline | 4 independent deliberate failures, no accidental failures |
| Green proof | temporary fixes pass focused and full suites |

Validation evidence (2026-08-20): Maven dependencies downloaded normally in the official `maven:3.9.9-eclipse-temurin-21` image. Backend compile and Spring Boot startup passed; PostgreSQL 17.11 and Flyway V1 completed successfully; the API returned HTTP 200 with the seeded request. Baseline backend tests are 2 FAIL (E1/I contract assertions), and frontend build is green with 2 deliberate test failures. Temporary backend correction produced 2/2 PASS; temporary frontend correction produced 2/2 PASS. `mvn clean test` is used in the isolated test profile so stale compiled resources cannot mask configuration changes.

## Agent Continuity

Work only inside this directory. Start with Compose and the isolated test profile. Keep tickets independent and restore the red baseline after temporary green validation. Distinguish Angular/node dependency or PostgreSQL health failures from challenge failures. Advanced interceptor refresh/idempotency or isolation work is intentionally deferred.

Completion requires reproducible Easy/Intermediate tickets, Hint 1/2/3, Learning/Interview/Review guidance, mentor context and evidence separating infrastructure from challenge failures.
