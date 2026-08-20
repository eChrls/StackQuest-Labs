# Real-World Technical Interview Labs

**Debug • Test • Refactor • Learn**

[English](README.md) | [Español](README.es.md)

[![License: MIT](https://img.shields.io/github/license/eChrls/Labs)](LICENSE) [![Stars](https://img.shields.io/github/stars/eChrls/Labs)](https://github.com/eChrls/Labs/stargazers) [![Forks](https://img.shields.io/github/forks/eChrls/Labs)](https://github.com/eChrls/Labs/forks) [![Workspace integrity](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml/badge.svg?branch=main)](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml)

![Real-World Technical Interview Labs social preview](docs/assets/social-preview.png)

An open-source collection of deliberately imperfect, reproducible projects that simulate real work and realistic technical assessments.

This is not LeetCode, isolated algorithms, a syntax tutorial, or a kata collection. It is practice with unfamiliar codebases, debugging, failing tests, legacy code, refactoring, REST, databases, frontend/backend integration, data work, production-minded reasoning, and technical interviews.

## The work behind the interview

Many real assessments do not give you an empty editor. They give you this:

```text
an unfamiliar project
        ↓
run it
        ↓
understand it
        ↓
reproduce the problem
        ↓
inspect tests/logs
        ↓
debug
        ↓
fix/refactor
        ↓
verify
        ↓
explain the decision
```

That workflow is the identity of this project.

## Canonical status system

| Status | Meaning |
| --- | --- |
| `✅ DONE` | Exists, is validated, and is synchronized/published when applicable. |
| `🟡 IN PROGRESS` | Active work exists but does not meet its Definition of Done yet. |
| `🧪 VALIDATION` | Implementation appears complete, but mandatory verification is missing. |
| `⏳ PENDING` | Planned but not started or not yet available. |
| `🚫 NOT PLANNED` | Consciously not planned at present. |

Creating files or writing code is not enough to mark an item `✅ DONE`. A Lab needs evidence of a reproducible project, the expected challenge test state, a temporarily demonstrated solution, complete documentation, validated Docker, and commit/push when applicable. Open-source infrastructure must be applied, checked, and synchronized. When evidence is incomplete, use `🧪 VALIDATION` or `🟡 IN PROGRESS`.

## Lab catalog

The seven Labs remain visible regardless of implementation status. A Lab being available does not mean every planned difficulty track exists.

| Lab | Stack | Focus | Easy | Intermediate | Advanced | Status |
| --- | --- | --- | --- | --- | --- | --- |
| [Lab-1](Lab-1/README.md) | Java 21, Spring Boot, Maven, PostgreSQL, JUnit | Unfamiliar backend, Java/Spring, REST, PostgreSQL, tests, debugging | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `✅ DONE` |
| [Lab-2](Lab-2/README.md) | Java 21, Spring Boot, PostgreSQL, JUnit/Mockito | Legacy, characterization tests, refactoring, side effects, TDD | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `✅ DONE` |
| [Lab-3](Lab-3/README.md) | React, TypeScript, Java, Spring Boot, PostgreSQL | HTTP, DTOs, async state, JPA, transactions, cross-layer debugging | `⏳ PENDING` | `🟡 IN PROGRESS` | `⏳ PENDING` | `🟡 IN PROGRESS` |
| Lab-4 | Angular, TypeScript, Node.js, NestJS | Frontend/backend contracts and async behavior | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` (optional/community) | `⏳ PENDING` |
| Lab-5 | Vue 3, TypeScript, PHP, Laravel, MySQL | Product features, validation, persistence and integration | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` (optional/community) | `⏳ PENDING` |
| Lab-6 | Python, FastAPI, PostgreSQL, Elasticsearch | Backend, ETL, reporting, data quality, SQL, synchronization, search | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING` |
| Lab-7 | Python, FastAPI, Pydantic, PostgreSQL/pgvector, AI provider abstraction | Applied AI Engineering: prompting, extraction, RAG, evals, tools, debugging | `⏳ PENDING` | `⏳ PENDING` | `🚫 NOT PLANNED` | `⏳ PENDING` |

Lab-1 and Lab-2 are validated base Labs, not claims that their full three-track expansions exist. Lab-3 has a published challenge baseline, but its planned track model and global experience requirements are incomplete. See the detailed [roadmap](docs/ROADMAP.md).

## Real-world assessment patterns

The challenges are original but shaped by recurring patterns in publicly available technical assessments. Research includes GetYourGuide, Personio, Crewmeister, George, FACEIT, Mimo, Equal Experts, Primer, Flipdish, and Wise. Lab-7 may also draw on public patterns documented from the Inato AI Engineer test, Hex AI Engineering take-home, and AI Engineering Field Guide: baseline plus evaluation, document extraction, RAG, tool use, Text-to-SQL, held-out evals, and explaining experiments and trade-offs.

> Inspired by recurring patterns observed in publicly available European technical assessments.

These are research references, not exercises to copy or claims of affiliation. The authoritative research, originality, copyright, difficulty, hint, and editorial rules live in the [Interview Research & Editorial Guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md).

## Difficulty tracks

### Easy

Eventually available in every Lab: reduced scope, visible starting point, a known failing test or endpoint, progressive hints, and foundational skills.

### Intermediate

Eventually available in every Lab: multiple files/layers, debugging, DB/API interaction, decisions, and regression risk.

### Advanced

Mandatory eventually for Lab-1, Lab-2, Lab-3, and Lab-6; optional future/community expansion for Lab-4 and Lab-5. Lab-7 Advanced is `🚫 NOT PLANNED` for its initial scope. Advanced work elsewhere must involve genuinely advanced concerns: concurrency, locking, transaction boundaries, performance, SQL/query plans, idempotency, data consistency, production incidents, Elasticsearch synchronization, or scalability trade-offs.

Docker does not increase challenge difficulty; it provides infrastructure.

## Standard challenge format and hints

```text
Context
Observed behaviour
Expected behaviour
Reproduction
Constraints
Acceptance criteria
Starting point / progressive hints
Follow-up discussion
```

Easy has an explicit starting point. Intermediate provides reproduction plus progressive hints. Advanced exposes the symptom and acceptance criteria with minimal help. Never use artificial comments such as `// BUG HERE`; code comments must look plausible in production. Full rules are in the [editorial guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md).

## Usage modes

| Mode | Intended experience | Status |
| --- | --- | --- |
| Learning Mode | Progressive hints, documentation, no time pressure. | `⏳ PENDING` |
| Interview Mode | Suggested timebox, difficulty-appropriate hints, observable reasoning. | `⏳ PENDING` |
| Review Mode | Root cause, code review, alternatives, production concerns, small follow-up. | `⏳ PENDING` |

These modes are the global target; they are not yet formalized consistently in every Lab.

## Debugging is a core skill

| Stack | Debugging |
| --- | --- |
| Java/Spring | JVM debugger, breakpoints, call stack, variables, JUnit, logs |
| React/Angular/Vue | Browser DevTools, Network, Console, Sources, tests |
| Node | Debugger, async stack, logs, tests |
| PHP | Stack traces, logs, tests/debugger |
| Python | Debugger, exceptions, logging, pytest |
| PostgreSQL | Diagnostic SQL, `EXPLAIN`, locks, data comparison |
| Elasticsearch | Queries, mappings, documents, synchronization state |

Docker is infrastructure, not the learning objective. An agent may perform Docker operations so the learner can focus on investigation and reasoning.

## Tests are evidence

Tests are acceptance criteria, debugging evidence, and regression protection—not merely a final check. Labs may deliberately begin with red tests; this documents the challenge baseline and does not mean the repository is broken.

## Docker-first and portable

```text
clone repo
   ↓
Docker + Compose
   ↓
choose Lab
   ↓
build isolated environment
```

Java/Maven, Node/npm, PHP/Composer, Python, PostgreSQL, MySQL, and Elasticsearch do not need global installation. Each Lab defines its environment. Every Lab must rebuild on another computer using essentially Git, Docker, and Docker Compose. Initial databases come from versioned migrations, seeds, or fixtures—not copied personal volumes. Current portability: `✅ DONE` for existing Lab baselines, supported by versioned setup and validated Compose.

## Lab-7 — Applied AI Engineering

**Prompting • RAG • Evals • Tools • Debugging**

Status: `⏳ PENDING` · Primary objective: **Self-learning** · Secondary objective: **Realistic AI Engineer interview practice**.

Unlike Labs 1–6, its main purpose is not exclusively to simulate technical assessments. It will teach practical AI Engineering through progressive, original, measurable problems while incorporating patterns from public assessments when useful. It remains Docker-first, reproducible, portable, realistic, debugging-oriented, and evidence-driven: tests and evals must demonstrate behavior.

### Planned stack and provider policy

The provisional stack is Python, FastAPI, Pydantic, PostgreSQL, pgvector when RAG is introduced, pytest, Docker, Docker Compose, embeddings, RAG, evals, and tool calling. Exact versions will be selected during implementation.

```text
AI Provider
├── deterministic fake/mock
├── external API
└── optional local model
```

Fundamental tests and evals must run without a paid API, Internet access, or nondeterministic LLM behavior. A real provider may be configured through `.env`; a small local model may be offered optionally. Ollama is optional and never the baseline.

The Lab must run reasonably on Linux and Windows through Docker Desktop/WSL2 or equivalent, on a modern standard CPU and reasonable consumer RAM. A dedicated GPU, NVIDIA hardware, CUDA, Apple Silicon, high-end hardware, large local models, or specialized hardware must never be required. Lack of a GPU cannot prevent completion.

### Planned progression

| Track | Stage | Learning outcomes | Status |
| --- | --- | --- | --- |
| Easy | E1 — Prompt engineering | System/user instructions, precision, constraints, structured output, valid JSON, iteration | `⏳ PENDING` |
| Easy | E2 — CV information extraction | Original fictional CVs; technology, periods, experience, terms, missing data; baseline search/regex/fake first | `⏳ PENDING` |
| Easy | E3 — Evaluation basics | Dataset, expected output, precision, false positives/negatives; “looks good” is not reliable evidence | `⏳ PENDING` |
| Intermediate | RAG | Documents, chunking, embeddings, retrieval, top-k, citations, grounding, no-answer cases | `⏳ PENDING` |
| Intermediate | AI debugging | Trace input → parsing → chunking → embedding → retrieval → context → prompt → generation → validation | `⏳ PENDING` |
| Intermediate | Evals | Visible dataset, regressions, retrieval quality, answer and citation correctness | `⏳ PENDING` |
| Intermediate | Tool calling | Controlled document search, read-only SQL, calculator, structured lookup | `⏳ PENDING` |
| Intermediate | Text-to-SQL | Natural language → safe SQL → PostgreSQL → answer, with destructive SQL blocked | `⏳ PENDING` |
| Intermediate | Basic AI security | Prompt injection, tool permissions, sensitive-data boundaries, output validation | `⏳ PENDING` |
| Integrated | Final applied challenge | Combine foundations, retrieval, evals, tools, debugging, and justified trade-offs | `⏳ PENDING` |
| Advanced | Initial Lab-7 scope | No large-model training/fine-tuning, CUDA, distributed ML, heavy serving, deep transformer internals, large local models, or complex multi-agent architectures | `🚫 NOT PLANNED` |

The governing principle is: establish a baseline, measure it, and introduce AI only when it demonstrably improves the result. Future community extensions may revisit excluded areas if there is demand.

### Lab-6 and Lab-7 are different

- **Lab-6 — Data Engineering/backend:** ETL, PostgreSQL, Elasticsearch, data quality, pipelines, and reporting.
- **Lab-7 — AI Engineering:** prompts, extraction, embeddings, RAG, evals, tools, and AI debugging.

They may share technologies, but they do not share learning objectives.

## Documentation hierarchy

- **[README.md](README.md):** global identity, vision, catalog, status, summary roadmap, objectives, tracks, and milestones.
- **[docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md):** authoritative source for realism, research, difficulty, challenge design, hints, editorial criteria, originality, and follow-up discussion.
- **[docs/LAB_SPEC.md](docs/LAB_SPEC.md):** mandatory technical contract for creating a Lab.
- **`Lab-N/README.md`:** Lab-specific context, operation, baseline, and tickets.
- **[docs/ROADMAP.md](docs/ROADMAP.md):** detailed roadmap and future evolution.

## Open-source contributions

Contributions are welcome: a New Lab, new or advanced challenge, bug report, tests, documentation, portability, accessibility, an alternative stack, or a realistic production scenario. Read [CONTRIBUTING.md](CONTRIBUTING.md) before proposing work.

## Community status board

| Capability | Status |
| --- | --- |
| Public repository | `✅ DONE` |
| MIT License | `✅ DONE` |
| CONTRIBUTING | `✅ DONE` |
| Code of Conduct | `✅ DONE` |
| Security policy | `✅ DONE` |
| Issue Forms | `✅ DONE` |
| PR template | `✅ DONE` |
| Discussions | `✅ DONE` |
| Topics | `✅ DONE` |
| Labels | `✅ DONE` |
| CODEOWNERS | `✅ DONE` |
| Community Profile 100% | `✅ DONE` |
| Workspace integrity CI | `✅ DONE` |
| Social Preview | `✅ DONE` (configured manually) |
| GitHub Pages | `⏳ PENDING` |
| Custom domain | `🚫 NOT PLANNED` |
| First external contributor | `⏳ PENDING` |
| First community Lab | `⏳ PENDING` |
| First release | `⏳ PENDING` |

The [social preview asset](docs/assets/social-preview.png) is retained for GitHub, LinkedIn, and community sharing. Pages will be evaluated only when core Labs are stable, community documentation is mature, and a clear use exists. GitHub remains the primary platform; a separate website/domain is not needed for usability today.

## Discovery and community milestones

| Milestone | Status |
| --- | --- |
| First external star | `⏳ PENDING` |
| First external fork | `⏳ PENDING` |
| First external contributor | `⏳ PENDING` |
| First external PR | `⏳ PENDING` |
| First community-created Lab | `⏳ PENDING` |
| First release | `⏳ PENDING` |

No metric is inferred or manufactured; a milestone moves only with public evidence.

## Master roadmap

| Area | Capability | Status |
| --- | --- | --- |
| Foundation | Open-source foundation | `✅ DONE` |
| Foundation | Global documentation | `✅ DONE` |
| Foundation | Community infrastructure | `✅ DONE` |
| Foundation | Editorial research | `✅ DONE` |
| Core Labs | Lab-1 | `✅ DONE` |
| Core Labs | Lab-2 | `✅ DONE` |
| Core Labs | Lab-3 | `🟡 IN PROGRESS` |
| Core Labs | Lab-4 | `⏳ PENDING` |
| Core Labs | Lab-5 | `⏳ PENDING` |
| Core Labs | Lab-6 | `⏳ PENDING` |
| Core Labs | Lab-7 | `⏳ PENDING` |
| Difficulty expansion | Easy in all seven Labs | `⏳ PENDING` |
| Difficulty expansion | Intermediate in all seven Labs | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-1 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-2 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-3 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-6 | `⏳ PENDING` |
| Difficulty expansion | Advanced Lab-7 | `🚫 NOT PLANNED` |
| Learning experience | Progressive hints | `⏳ PENDING` |
| Learning experience | Learning Mode | `⏳ PENDING` |
| Learning experience | Interview Mode | `⏳ PENDING` |
| Learning experience | Review Mode | `⏳ PENDING` |
| Learning experience | Time estimates | `⏳ PENDING` |
| Learning experience | Follow-up questions | `⏳ PENDING` |
| Community | Good first issues | `⏳ PENDING` |
| Community | First external contribution | `⏳ PENDING` |
| Community | First community Lab | `⏳ PENDING` |
| Community | Release/versioning | `⏳ PENDING` |
| Future | GitHub Pages | `⏳ PENDING` |
| Future | Additional stacks/community Labs | `⏳ PENDING` |
| Future | Custom domain | `🚫 NOT PLANNED` |

## Maintaining this roadmap

> Planned capabilities remain visible even while pending. When a milestone is completed, update its status instead of removing it.

Any agent that completes a milestone must update this README in the same PR/commit when appropriate. Status moves to `✅ DONE` only with evidence of its completion criteria.

## Project continuity context

This is an open-source, Docker-first collection of original, realistic Labs. The [editorial guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md) governs research and challenge quality; [LAB_SPEC](docs/LAB_SPEC.md) governs technical construction. Seven Labs are planned: Labs 1–6 focus on technical interview practice, while Lab-7 prioritizes Applied AI self-learning with interview-inspired practice. Easy and Intermediate must eventually exist in all seven; Advanced is mandatory for Labs 1, 2, 3, and 6, optional/community-driven for Labs 4 and 5, and `🚫 NOT PLANNED` initially for Lab-7. Lab-1 and Lab-2 base Labs are done; Lab-3 is in progress; Labs 4–7, track expansion, usage modes, community milestones, releases, and Pages remain pending. Preserve every planned item and use only the canonical statuses above.

Released under the [MIT License](LICENSE).
