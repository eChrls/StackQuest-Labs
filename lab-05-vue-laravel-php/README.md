# Lab 05 — Vue + Laravel/PHP Full‑Stack

## Docker-first setup

Prerequisites: Git, Docker with Compose, VS Code and Dev Containers. Open this Lab and choose **Dev Containers: Reopen in Container**. PHP/Composer, Node/npm and MySQL remain in Docker. Run `docker compose --profile test run --rm backend-test`, `docker compose run --rm frontend npm run build` and `docker compose run --rm frontend npm test` for the baseline.

## Visual debugging

Select **Lab 05 — Listen for Xdebug**, put a breakpoint under `backend/`, then reproduce the API request through port 18085; path mapping connects the backend container to the opened source. Select **Lab 05 — Debug Vue** for TypeScript breakpoints and inspect Network plus reactive state. Confirm Variables, Call Stack and Step Over/Into/Out/Continue. Xdebug, PHP and Node are not host dependencies.

## Purpose

Docker-first Vue 3/TypeScript and Laravel 12/PHP 8.2 with MySQL 8.4. The focus is reactive state, forms and validation, async/API errors, Laravel controllers/services, Eloquent-style persistence, authorization, business rules, transactions and full-stack debugging. It is intentionally different from Lab 03/04: the domain is a task desk and the candidate must follow state and authorization across Vue → HTTP → PHP → MySQL.

## Run

From the repository root: `docker compose -f lab-05-vue-laravel-php/compose.yml config`, then `docker compose -f lab-05-vue-laravel-php/compose.yml up --build mysql backend frontend`. UI is `http://localhost:14205`; API is `http://localhost:18085/health` and MySQL is internal only. Tests: `docker compose -f lab-05-vue-laravel-php/compose.yml --profile test run --rm backend-test`; frontend `docker compose -f lab-05-vue-laravel-php/compose.yml run --rm frontend npm run build` and `docker compose -f lab-05-vue-laravel-php/compose.yml run --rm frontend npm test`. No PHP, Composer, Node or MySQL is required on the host.

## Domain and baseline

Customers own tasks. An active customer has OPEN/DONE tasks; notes are nullable. MySQL schema and deterministic seed live in `backend/database/schema.sql`. Baseline has four independent deliberate failures across the four tickets: two frontend checks and two backend checks. Infrastructure, dependency downloads and MySQL health are not challenge failures.

## Easy

Starting Point: load the task desk, inspect the Vue `ref`/computed flow and reproduce one focused frontend test.

**E1 — completion does not update the visible list.** Expected: completing a task removes it from the open list without a page reload. Hints: (1) inspect the reactive collection, (2) observe the request/response timing, (3) assert the rendered state after the async action. Guided debugging: browser network → handler → ref/computed value → DOM.

**E2 — whitespace title passes validation.** Expected: a title containing only whitespace is rejected consistently by Vue and the backend boundary. Hints: (1) inspect the exact value sent, (2) distinguish `required` from meaningful content, (3) test trim/normalization without changing user-facing copy.

## Intermediate

**I1 — open-task query includes DONE rows.** Expected: server filtering returns only OPEN tasks for a customer. Hints: (1) compare SQL predicate with domain vocabulary, (2) inspect the database result rather than the Vue filter, (3) preserve index-friendly querying.

**I2 — inactive customer can complete a task.** Expected: authorization/business rules are enforced in Laravel service code, not only in the UI. Hints: (1) replay the POST directly, (2) inspect customer state inside the transaction, (3) reject before mutation and verify rollback.

Guided debugging: reproduce through the UI, replay the same request with the API, inspect Vue state and the Laravel service, query MySQL before and after the transaction, then run the focused test and the complete profile.

## Learning / Interview / Review

Learning follows network traces, PHP breakpoints/logs and focused tests. Interview mode requires explaining Vue reactivity, API error states, Eloquent/SQL choices, authorization and transaction boundaries. Review mode checks strict TypeScript, nullable DTOs, idempotency, least privilege, rollback and small diffs.

### Mentor / AI context

Candidate-facing symptoms hide root causes. Verified mentor roots: E1 stale/non-reactive UI after async completion; E2 client/server whitespace contract mismatch; I1 incorrect ORM/query predicate; I2 authorization checked only at presentation layer and mutation not guarded transactionally. Equivalent fixes are valid if the HTTP contract and business rule remain stable. Common wrong fixes: reload the page, hide a button only, filter only in Vue, or weaken tests.

## Validation matrix

| Area | Evidence |
|---|---|
| Compose | config parses; MySQL healthy |
| Backend | Composer install, PHP syntax, Laravel service tests, schema/seed |
| Frontend | Vite build/typecheck; Vue challenge tests |
| Baseline | exactly 4 independent deliberate failures |
| Green proof | temporary E1/E2/I1/I2 fixes pass; then defects restored |
| App | Vue request reaches PHP endpoint and MySQL seed |

Troubleshooting: recreate only the Lab‑05 MySQL volume if schema state is stale; inspect `docker compose logs mysql backend`; distinguish Composer/npm network failures from test failures; never commit `vendor/`, `node_modules/`, secrets or dumps.

## Agent Continuity

Work only inside this directory. Preserve the four-ticket baseline, run backend and frontend independently, and verify a temporary green state before restoring defects. Future community Advanced work may explore authorization policy caching or transaction isolation, but it is not required here. A new agent should be able to reproduce every ticket, give Hint 1/2/3, explain root cause in mentor mode, review alternatives and identify infrastructure versus challenge failures without rediscovering the design.

## Acceptance and continuity

Acceptance requires four independent red checks, a reproducible Docker-first path, focused and full validation, and a solution that preserves the HTTP contract, authorization rule and transaction boundary. Compose config, PHP syntax, Laravel tests, Vite build and Vue tests form the validation matrix.

Agent Continuity: work only in this directory, run backend and frontend independently, preserve the baseline, and use the documented hints and mentor roots. Contributors may add further Easy, Intermediate, Advanced or specialised task-desk challenges.
