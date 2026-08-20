# Detailed Roadmap

The root [README](../README.md) is the public master status board. This document adds detail without replacing it. Editorial decisions follow the [Interview Research & Editorial Guide](INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md); technical construction follows [LAB_SPEC](LAB_SPEC.md).

## Status policy

Only `✅ DONE`, `🟡 IN PROGRESS`, `🧪 VALIDATION`, `⏳ PENDING`, and `🚫 NOT PLANNED` are canonical. `✅ DONE` requires evidence of the applicable Definition of Done, not merely files or code. Planned capabilities remain visible while pending.

## Core Labs

| Lab | Base | Easy | Intermediate | Advanced | Direction |
| --- | --- | --- | --- | --- | --- |
| Lab-1 — Java/Spring Boot Debugging | `✅ DONE` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | Expand the validated base; Advanced covers concurrency, locking, transactions, performance, and incidents. |
| Lab-2 — Java Legacy Challenge | `✅ DONE` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | Formalize characterization/refactoring/TDD tracks; Advanced covers safe legacy change and idempotency. |
| Lab-3 — Full Stack Integration | `🟡 IN PROGRESS` | `⏳ PENDING` | `🟡 IN PROGRESS` | `⏳ PENDING` | Complete tracks across React/TypeScript, Spring, HTTP, JPA, PostgreSQL, async state, and transactions. |
| Lab-4 — Angular + NestJS | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` (optional/community) | Existing-app integration with DI, async behavior, validation, errors, contracts, and tests. |
| Lab-5 — Vue + Laravel | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` (optional/community) | Product features, validation, permissions, persistence, transactions, and performance. |
| Lab-6 — Python/Data | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | FastAPI, PostgreSQL, ETL/reporting, quality, idempotency, SQL, Elasticsearch sync, and scale. |

Base availability and track completion are separate. Lab-1 and Lab-2 being `✅ DONE` does not make their track expansions complete.

## Difficulty expansion

| Milestone | Status |
| --- | --- |
| Easy in all six Labs | `⏳ PENDING` |
| Intermediate in all six Labs | `⏳ PENDING` |
| Advanced Lab-1 | `⏳ PENDING` |
| Advanced Lab-2 | `⏳ PENDING` |
| Advanced Lab-3 | `⏳ PENDING` |
| Advanced Lab-6 | `⏳ PENDING` |
| Optional Advanced Lab-4/Lab-5 | `⏳ PENDING` |

Advanced means difficult engineering reasoning—concurrency, locking, transaction boundaries, performance/query plans, idempotency, consistency, incidents, synchronization, or scale—not Docker usage.

## Learning experience

| Capability | Status |
| --- | --- |
| Standard ticket format in every challenge | `⏳ PENDING` |
| Progressive hints by difficulty | `⏳ PENDING` |
| Learning Mode | `⏳ PENDING` |
| Interview Mode and timeboxes | `⏳ PENDING` |
| Review Mode | `⏳ PENDING` |
| Root-cause, trade-off, and production follow-ups | `⏳ PENDING` |

## Foundation and community

| Capability | Status |
| --- | --- |
| Open-source foundation/community files | `✅ DONE` |
| Global bilingual documentation | `✅ DONE` |
| Editorial research source of truth | `✅ DONE` |
| Technical Lab contract | `✅ DONE` |
| Workspace integrity CI | `✅ DONE` |
| Social Preview | `✅ DONE` (configured manually) |
| Good first issues | `⏳ PENDING` |
| First external star/fork/PR/contributor | `⏳ PENDING` |
| First community-created Lab | `⏳ PENDING` |
| Release/versioning | `⏳ PENDING` |

## Portability

Existing Lab baselines have `✅ DONE` portability: versioned Docker/Compose configuration and deterministic migrations/seeds reconstruct them without copied personal volumes. Future Labs must preserve and validate this contract before becoming done.

## Future platform

| Capability | Status | Decision |
| --- | --- | --- |
| GitHub Pages | `⏳ PENDING` | Re-evaluate when core Labs are stable, documentation is mature, and a clear use exists. Do not create it now. |
| Custom domain | `🚫 NOT PLANNED` | GitHub is primary and the repository is usable without another website. |
| Additional stacks/community Labs | `⏳ PENDING` | Consider after the six-Lab core and contribution process are stable. |

## Maintenance rule

> Planned capabilities remain visible even while pending. When a milestone is completed, update its status instead of removing it.

Any agent completing a milestone updates the root README and this roadmap in the same PR/commit when appropriate, citing evidence required for `✅ DONE`.
