# Detailed Roadmap

The root [README](../README.md) is the public master status board. This document adds detail without replacing it. Editorial decisions follow the [Interview Research & Editorial Guide](INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md); technical construction follows [LAB_SPEC](LAB_SPEC.md).

## Status policy

Only `✅ DONE`, `🟡 IN PROGRESS`, `🧪 VALIDATION`, `⏳ PENDING`, and `🚫 NOT PLANNED` are canonical. `✅ DONE` requires evidence of the applicable Definition of Done, not merely files or code. Planned capabilities remain visible while pending.

## Core Labs

| Lab                                | Base         | Easy         | Intermediate    | Advanced                          | Direction                                                                                                                 |
| ---------------------------------- | ------------ | ------------ | --------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| Lab-1 — Java/Spring Boot Debugging | `✅ DONE`    | `⏳ PENDING` | `⏳ PENDING`    | `⏳ PENDING`                      | Expand the validated base; Advanced covers concurrency, locking, transactions, performance, and incidents.                |
| Lab-2 — Java Legacy Challenge      | `✅ DONE`    | `⏳ PENDING` | `⏳ PENDING`    | `⏳ PENDING`                      | Formalize characterization/refactoring/TDD tracks; Advanced covers safe legacy change and idempotency.                    |
| Lab-3 — Full Stack Integration     | `✅ DONE`    | `⏳ PENDING` | `🧪 VALIDATION` | `⏳ PENDING`                      | Base challenge is complete; validate Intermediate against the Reference Lab standard before claiming full Lab completion. |
| Lab-4 — Angular + NestJS           | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING`    | `⏳ PENDING` (optional/community) | Existing-app integration with DI, async behavior, validation, errors, contracts, and tests.                               |
| Lab-5 — Vue + Laravel              | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING`    | `⏳ PENDING` (optional/community) | Product features, validation, permissions, persistence, transactions, and performance.                                    |
| Lab-6 — Python/Data                | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING`    | `⏳ PENDING`                      | FastAPI, PostgreSQL, ETL/reporting, quality, idempotency, SQL, Elasticsearch sync, and scale.                             |
| Lab-7 — Applied AI Engineering     | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING`    | `🚫 NOT PLANNED`                  | Self-learning first; interview-inspired prompting, extraction, RAG, evals, tools, and AI debugging.                       |

Base availability and track completion are separate. Lab-1 and Lab-2 being `✅ DONE` does not make their track expansions complete.

Lab-3 status is deliberately split: base challenge `✅ DONE`; Easy expansion `⏳ PENDING`; Intermediate conformance to the Reference Lab standard `🧪 VALIDATION`; Advanced expansion `⏳ PENDING`; Agent Continuity Test `⏳ PENDING`; full Lab completion `⏳ PENDING`.

## Difficulty expansion

| Milestone                      | Status           |
| ------------------------------ | ---------------- |
| Easy in all seven Labs         | `⏳ PENDING`     |
| Intermediate in all seven Labs | `⏳ PENDING`     |
| Advanced Lab-1                 | `⏳ PENDING`     |
| Advanced Lab-2                 | `⏳ PENDING`     |
| Advanced Lab-3                 | `⏳ PENDING`     |
| Advanced Lab-6                 | `⏳ PENDING`     |
| Optional Advanced Lab-4/Lab-5  | `⏳ PENDING`     |
| Advanced Lab-7 initial scope   | `🚫 NOT PLANNED` |

Advanced means difficult engineering reasoning—concurrency, locking, transaction boundaries, performance/query plans, idempotency, consistency, incidents, synchronization, or scale—not Docker usage.

## Lab-7 — Applied AI Engineering plan

**Prompting • RAG • Evals • Tools • Debugging**

Lab-7 is `⏳ PENDING`. Its primary objective is self-learning; realistic AI Engineer interview practice is secondary. It shares the repository's Docker-first, reproducible, portable, evidence-driven philosophy, but it is not exclusively an interview simulation.

| Milestone                                                  | Track               | Status       |
| ---------------------------------------------------------- | ------------------- | ------------ |
| AI foundations and prompt engineering                      | Easy                | `⏳ PENDING` |
| Original fictional CV extraction challenge                 | Easy                | `⏳ PENDING` |
| Deterministic evaluation baseline                          | Easy                | `⏳ PENDING` |
| RAG: chunking, embeddings, retrieval, grounding, citations | Intermediate        | `⏳ PENDING` |
| AI pipeline debugging                                      | Intermediate        | `⏳ PENDING` |
| Regression, retrieval, answer, and citation eval suite     | Intermediate        | `⏳ PENDING` |
| Controlled tool calling                                    | Intermediate        | `⏳ PENDING` |
| Safe read-only Text-to-SQL                                 | Intermediate        | `⏳ PENDING` |
| Basic AI security                                          | Intermediate        | `⏳ PENDING` |
| Final integrated applied challenge                         | Easy + Intermediate | `⏳ PENDING` |

The future stack is Python, FastAPI, Pydantic, PostgreSQL, pgvector when RAG arrives, pytest, Docker/Compose, a provider abstraction, embeddings, RAG, evals, and tool calling. Versions remain undecided until implementation.

The provider boundary must support a deterministic fake/mock, an optional external API configured through environment variables, and an optional small local model. Fundamental tests/evals cannot require Internet, paid APIs, secrets, or nondeterministic model behavior. Ollama is optional, never baseline.

The Lab must run on a standard development computer under Linux or Windows with Docker Desktop/WSL2 or equivalent, modern CPU, and reasonable consumer RAM. GPU, NVIDIA/CUDA, Apple Silicon, high-end or specialized hardware, and large local models are not required. No GPU may be a completion blocker.

`Advanced track — 🚫 NOT PLANNED for the initial Lab-7 scope.` Large transformer training/fine-tuning, CUDA, distributed ML, heavy model serving, deep transformer internals, large local models, and complex multi-agent architectures are excluded initially; community extensions may revisit them if demand exists.

Lab-6 remains Data Engineering/backend—ETL, data quality, pipelines, reporting, PostgreSQL, and Elasticsearch. Lab-7 is AI Engineering—prompts, extraction, embeddings, RAG, evals, tools, and AI debugging. Shared technology does not imply shared objectives.

## Learning experience

| Capability                                       | Status       |
| ------------------------------------------------ | ------------ |
| Standard ticket format in every challenge        | `⏳ PENDING` |
| Progressive hints by difficulty                  | `⏳ PENDING` |
| Learning Mode                                    | `⏳ PENDING` |
| Interview Mode and timeboxes                     | `⏳ PENDING` |
| Review Mode                                      | `⏳ PENDING` |
| Root-cause, trade-off, and production follow-ups | `⏳ PENDING` |
| Agent Continuity Test                            | `⏳ PENDING` |

## Foundation and community

| Capability                              | Status                          |
| --------------------------------------- | ------------------------------- |
| Open-source foundation/community files  | `✅ DONE`                       |
| Global bilingual documentation          | `✅ DONE`                       |
| Editorial research source of truth      | `✅ DONE`                       |
| Technical Lab contract                  | `✅ DONE`                       |
| Workspace integrity CI                  | `✅ DONE`                       |
| Social Preview                          | `✅ DONE` (configured manually) |
| Good first issues                       | `⏳ PENDING`                    |
| First external star/fork/PR/contributor | `⏳ PENDING`                    |
| First community-created Lab             | `⏳ PENDING`                    |
| Release/versioning                      | `⏳ PENDING`                    |

## Portability

Existing Lab baselines have `✅ DONE` portability: versioned Docker/Compose configuration and deterministic migrations/seeds reconstruct them without copied personal volumes. Future Labs must preserve and validate this contract before becoming done.

## Future platform

| Capability                       | Status           | Decision                                                                                                      |
| -------------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------- |
| GitHub Pages                     | `⏳ PENDING`     | Re-evaluate when core Labs are stable, documentation is mature, and a clear use exists. Do not create it now. |
| Custom domain                    | `🚫 NOT PLANNED` | GitHub is primary and the repository is usable without another website.                                       |
| Additional stacks/community Labs | `⏳ PENDING`     | Consider after the seven-Lab core and contribution process are stable.                                        |

## Maintenance rule

> Planned capabilities remain visible even while pending. When a milestone is completed, update its status instead of removing it.

Any agent completing a milestone updates the root README and this roadmap in the same PR/commit when appropriate, citing evidence required for `✅ DONE`.

## Temporary construction order

The current construction strategy is temporary: Lab-1 -> 100% Reference Lab, Lab-2 -> adapt to the Reference Lab standard, Lab-3 -> adapt to the Reference Lab standard, then Lab-6, Lab-7, Lab-4 and Lab-5. This sequencing can be simplified or removed once all Labs are complete.

## Agent Continuity standard

A Lab is 100% complete only when a new agent, using the global sources and the Lab-specific README, can understand the challenge, guide the learner, provide progressive hints, teach the related concept, distinguish challenge defects from infrastructure failures, validate a solution, explain root causes and alternatives, and provide a complete resolution when the learner is blocked. Lab-1 is the first Reference Lab for this standard; Lab-2 and Lab-3 will be adapted differentially afterward.
