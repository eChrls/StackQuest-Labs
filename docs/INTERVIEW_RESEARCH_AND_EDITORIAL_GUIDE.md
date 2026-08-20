# Interview Research & Editorial Guide

**Project:** Real-World Technical Interview Labs
**Status:** Editorial baseline
**Research date:** 2026-08-20
**Primary scope:** Spain and Europe, with a small number of international public examples when they reinforce a recurring interview pattern.

---

## 1. Purpose

This document defines the research basis and editorial direction for the `Labs` repository.

The project should simulate **realistic technical interview work**, not algorithm-drill platforms. A Lab should feel like receiving an unfamiliar repository from a company and being asked to understand it, run it, investigate a symptom, use tests and debugging evidence, make a safe change, and explain the decision.

The editorial goal is:

> Real projects, realistic symptoms, progressive hints, production-minded acceptance criteria, and tasks inspired by public hiring patterns without copying proprietary or confidential interview material.

---

## 2. Research method and evidence levels

The research prioritizes:

1. **Official public hiring challenge repositories** published by companies.
2. **Official company hiring/interview documentation.**
3. **Public company engineering repositories** that expose realistic development practices.
4. **Community-curated hiring process collections** only as secondary evidence.
5. **Candidate-published challenge copies** only as secondary evidence and never as a source to reproduce verbatim.

### Evidence labels

- **A — Primary:** official company challenge or official hiring documentation.
- **B — Strong secondary:** public industry/community source showing a recurring pattern.
- **C — Context only:** candidate-published or third-party material; useful for triangulation, not for direct reproduction.

This is not a statistical survey of all European employers. “High frequency” below means the pattern appears repeatedly across the researched public sample.

---

## 3. Main conclusion

The strongest recurring pattern is **not** “solve a clever algorithm from a blank editor”.

Public European hiring material repeatedly emphasizes one or more of:

- working in an existing repository;
- getting the application running locally;
- using the debugger;
- implementing a product/domain feature;
- reading and extending existing code;
- refactoring;
- unit and integration tests;
- pair programming;
- reasoning aloud and documenting assumptions;
- production-quality code;
- explaining trade-offs;
- quality over feature completeness;
- follow-up code review or extension of the submitted work.

This directly supports the core philosophy of Real-World Technical Interview Labs.

---

## 4. Strongest sources and what they teach us

### 4.1 GetYourGuide — Backend Coding Interview

**Evidence:** A — official public repository
**URL:** https://github.com/getyourguide/swe-be-coding-interview

Relevant signals:

- candidate receives an existing Java 21 project;
- must run the application locally;
- IntelliJ is explicitly recommended to leverage debugger tooling;
- candidates may use another IDE provided they can run endpoints and use a debugger;
- the repository itself is preparation for challenges implemented during the interview.

**Editorial consequence:**
Java Labs should include unfamiliar-project orientation, real debugging, endpoints, tests and navigation across code rather than isolated methods only.

**Important restriction:**
The repository contains a confidentiality notice. Do not reproduce undisclosed interview tasks or attempt to reconstruct them.

---

### 4.2 GetYourGuide — Frontend Coding Interview

**Evidence:** A — official public repository
**URL:** https://github.com/getyourguide/swe-fe-coding-interview

Relevant signals:

- candidate must make the full application run locally;
- challenges are implemented on top of an existing codebase;
- some apparently odd application behaviour is intentional;
- the company describes the target as a generalist software engineer rather than a narrow framework specialist.

**Editorial consequence:**
Frontend Labs should contain existing behaviour, ambiguous symptoms and debugging across state/API/UI boundaries. Some problems should require identifying whether the bug is in the UI, network contract or backend rather than being told the location.

---

### 4.3 Personio — Backend Coding Challenge

**Evidence:** A — official public repository
**URL:** https://github.com/personio/backend-coding-challenge

Relevant signals:

- scenario is framed as joining an existing team and becoming familiar with an existing reminder service;
- Kotlin/Spring Boot, Flyway, PostgreSQL and SQL tooling appear in a realistic service;
- domain rules include scheduling, time zones, recurrence and “send exactly once” behaviour;
- API and infrastructure structure are documented;
- architecture boundaries matter.

**Editorial consequence:**
Realistic backend challenges should include domain rules with time, persistence, idempotency and cross-layer reasoning. Advanced Java/PostgreSQL tracks should include time zones, scheduled behaviour, atomicity and “exactly once from the application’s point of view” problems.

---

### 4.4 Crewmeister — Java Coding Challenge

**Evidence:** A — official public repository
**URL:** https://github.com/crewmeister/java-coding-challenge

Relevant signals:

- Spring Boot service;
- requirements are presented as user stories;
- external public data source;
- REST endpoints and date-based exchange-rate logic;
- candidate decides whether persistence is needed.

**Editorial consequence:**
Not every challenge should be “find the bug”. Some should be feature-oriented and require translating user stories into a small maintainable service while making reasonable design choices.

---

### 4.5 George Backend Chapter — Coding Challenge

**Evidence:** A — official public repository
**URL:** https://github.com/george-labs/george-backend-hiring

Relevant signals:

- explicitly evaluates OOP, refactoring and automated unit/integration testing;
- performed through multiple pair-programming sessions;
- requirements are delivered incrementally;
- quality is valued over quantity;
- candidates should design only as much as needed and avoid over-engineering;
- assumptions should be documented;
- code must compile and tests must pass for an iteration to count as done;
- web research is allowed; exact-solution searching is not;
- candidates are asked to think before coding.

**Editorial consequence:**
Labs should contain iterative tickets, explicit Definition of Done, progressive changes and follow-up requirements. Refactoring Labs should reward adaptability to the next requirement rather than a “perfect final architecture”.

---

### 4.6 Flipdish — Engineering interview process

**Evidence:** A — official company careers documentation
**URL:** https://www.flipdish.com/es/carreras/engineering-careers

Relevant signals:

- technical assessment can be a take-home project or live pair-programming;
- their challenge is designed internally;
- they explicitly value people who enjoy taking something messy and making it better.

**Editorial consequence:**
Legacy, debugging and “make this safer/better without rewriting it” are realistic interview signals, not artificial training exercises.

---

### 4.7 Wise — Engineering Interviews

**Evidence:** A — official company careers documentation
**URL:** https://wise.jobs/engineering-interviews

Relevant signals:

- technical tests can include case studies, take-home challenges or technical tests;
- software engineering candidates may complete paired coding exercises;
- candidates receive a detailed brief.

**Editorial consequence:**
Each Lab ticket should look like a concise engineering brief and should support both self-paced take-home mode and pair/live mode.

---

### 4.8 FACEIT — Frontend Coding Challenge

**Evidence:** A — official public repository, archived
**URL:** https://github.com/faceit/frontend-coding-challenge

Relevant signals:

- existing TypeScript frontend with prebuilt components and fake REST API;
- tasks include loading, errors, retry, empty states, editing, deletion, creation and server-side search;
- optimistic updates and rollback;
- performance considerations;
- responsive behaviour;
- approximately three-hour expected duration;
- repository structure and professionalism matter.

**Editorial consequence:**
Frontend Labs should not reduce React/Angular/Vue to rendering a list. Realistic tracks should include loading/error/empty states, async writes, rollback, validation, search, pagination and API failures.

---

### 4.9 Mimo — Frontend Take-home Assignment

**Evidence:** A — official public repository
**URL:** https://github.com/getmimo/coding-challenge

Relevant signals:

- open-ended React challenge;
- architecture, state management, UX and code structure are evaluation dimensions;
- candidate designs a lightweight API;
- explicit time budget;
- README should explain trade-offs and what would be done next;
- current version also evaluates AI-tool collaboration.

**Editorial consequence:**
Some Labs should include open-ended feature tickets where multiple solutions are acceptable and the candidate must justify trade-offs. AI use may be allowed in a dedicated mode, but the Lab must still make reasoning observable.

---

### 4.10 Equal Experts — Data Engineering Exercise

**Evidence:** A — official public repository, archived
**URL:** https://github.com/EqualExperts/data-engineering-exercise-python

Relevant signals:

- exercise runs inside Docker;
- ingestion process over supplied data;
- ingestion runs multiple times as new data arrives;
- tests and test-first thinking are explicitly valued;
- simplicity is valued over unnecessary abstraction;
- candidate is asked about data-quality measures;
- candidate is asked how the design would scale to a much larger dataset;
- expected exercise time is around 90 minutes;
- follow-up interview includes walking through the solution.

**Editorial consequence:**
The Data Lab should strongly emphasize incremental ingestion, repeatability/idempotency, data quality, simple architecture, tests, and a follow-up scaling discussion rather than building a huge data platform.

---

### 4.11 Primer — DX Backend Challenge

**Evidence:** A — official public repository
**URL:** https://github.com/primer-io/dx-backend-challenge

Relevant signals:

- starter/skeleton FastAPI project;
- candidate extends an existing repository;
- Docker Compose is an accepted execution path;
- simple application bootstrap is already provided.

**Editorial consequence:**
Python/FastAPI challenges should often begin from a working skeleton and ask the candidate to extend or debug it rather than build framework boilerplate.

---

### 4.12 seQura — Backend coding challenge copy

**Evidence:** C — candidate/public copy, not treated as authoritative company repository
**URL:** https://github.com/itSQualL/sequra

Useful pattern signals:

- payment/merchant domain;
- realistic business fee calculation;
- persistence;
- background processing;
- REST reporting endpoint;
- care with money;
- production-quality expectation;
- tests/documentation/trade-offs;
- quality over completeness;
- approximately three-hour timebox.

**Editorial consequence:**
Payments are a strong domain for realistic backend exercises involving `BigDecimal`, jobs, idempotency, transactions and reporting.

**Restriction:**
Use only the general pattern. Do not copy its wording, dataset or exact rules into Labs.

---

### 4.13 Community hiring-process evidence

**Evidence:** B — community curated
**URL:** https://github.com/poteto/hiring-without-whiteboards

Relevant European/Spanish examples listed include patterns such as:

- Ableton: take-home followed by pair programming/debugging;
- Wallapop: take-home project or existing repository, then practical interview;
- CARTO: take-home project followed by code review/interview;
- Badi and Caravelo: take-home followed by discussion;
- several European companies: real-world tasks, pairing, code review or extension.

**Editorial consequence:**
A useful Lab should support a second phase where the candidate discusses or extends the solution. “Fix it and stop” is less realistic than “fix it, then explain and adapt it”.

**Caution:**
Community lists can become outdated. Use them to identify patterns, not to claim a company’s current hiring process.

---

### 4.14 Applied AI Engineering assessment patterns

**Evidence:** public AI Engineering assessment and field-guide patterns

Sources already reviewed for this direction include the Inato AI Engineer test, Hex AI Engineering take-home, and AI Engineering Field Guide. Their recurring high-level signals include:

- establish a non-LLM baseline before adding complexity;
- use explicit evaluation data, including held-out cases where appropriate;
- extract structured information from documents;
- build and assess retrieval-augmented generation;
- use constrained tools and safe Text-to-SQL;
- explain experiments, measurements, failures, and trade-offs.

**Editorial consequence:**
Lab-7 may use these general patterns for original self-learning challenges and interview-inspired practice. It must not copy their exercises, wording, datasets, hidden cases, or business rules, and must not present itself as a challenge used by Inato, Hex, or any other named organization.

---

## 5. Observed interview patterns

| Pattern                                      | Strength in researched sample | Labs                              |
| -------------------------------------------- | ----------------------------: | --------------------------------- |
| Existing repository / starter project        |                     Very high | All                               |
| Run and understand project before coding     |                     Very high | All                               |
| Tests as evaluation evidence                 |                     Very high | All                               |
| Add/extend a product feature                 |                     Very high | All                               |
| Explain design decisions / trade-offs        |                     Very high | All                               |
| Follow-up discussion / code review / pairing |                     Very high | All                               |
| Refactoring / improve messy code             |                          High | Lab-1, Lab-2, Lab-3               |
| Debugging with IDE/logs                      |                          High | Lab-1, Lab-2, Lab-3               |
| REST/API behaviour                           |                          High | Lab-1, Lab-3, Lab-4, Lab-5, Lab-6 |
| Persistence and data modelling               |                          High | Lab-1, Lab-2, Lab-3, Lab-5, Lab-6 |
| Loading/error/empty/retry frontend states    |       High in frontend sample | Lab-3, Lab-4, Lab-5               |
| Async UI mutation / rollback                 |                   Medium-high | Lab-3, Lab-4, Lab-5               |
| Incremental ingestion / repeated jobs        |           High in data sample | Lab-6                             |
| Data quality / scale discussion              |           High in data sample | Lab-6                             |
| Transactions / atomicity                     |                   Medium-high | Lab-1, Lab-2, Lab-3, Lab-6        |
| Time zones / scheduling                      |                        Medium | Lab-1, Lab-6                      |
| Production-readiness notes                   |                          High | All                               |
| Algorithm puzzles as main assessment         |   Not dominant in this sample | Optional only                     |
| Baseline plus measurable AI evaluation       | High in AI Engineering sample | Lab-7                             |
| Document extraction / RAG / tool use         | High in AI Engineering sample | Lab-7                             |

---

## 6. Editorial identity

### The repository is

- real-world technical interview practice;
- project-based;
- debugging-first where appropriate;
- test-backed;
- Docker-reproducible;
- multi-stack;
- progressive in difficulty;
- designed for discussion, not just “green tests”.

### The repository is not

- LeetCode;
- a syntax tutorial;
- a framework tutorial;
- a collection of isolated toy functions;
- a repository of copied company interview tests;
- a repository of leaked/confidential assessment material;
- a place where every problem has one “magic” solution.

---

## 7. Difficulty model

Every Lab must contain **Easy** and **Intermediate** challenges.

### Easy

Purpose: make the Lab approachable without turning it into a tutorial.

Characteristics:

- one primary defect or requirement;
- narrow scope;
- clear expected/actual behaviour;
- visible starting point;
- directly relevant test or reproduction path;
- usually 20–60 minutes in Interview Mode;
- foundational language/framework concept.

Examples:

- incorrect monetary total;
- null handling;
- wrong DTO field;
- frontend loading/error handling;
- simple SQL aggregate;
- duplicated ETL row.

### Intermediate

Purpose: simulate the most common realistic early-career / mid-level technical exercise.

Characteristics:

- requires navigation across multiple files/layers;
- failing test may identify symptom but not cause;
- debugging expected;
- interaction with DB/API;
- small design decision;
- regression risk;
- usually 45–120 minutes per ticket in Interview Mode.

Examples:

- Controller → Service → Repository bug;
- transaction rollback;
- legacy side effects;
- pagination contract mismatch;
- incremental load/idempotency;
- partial refund rule.

### Advanced

Mandatory in the most strategically important areas:

- **Lab-1: Java/Spring Boot**
- **Lab-2: Java legacy/backend**
- **Lab-3: Java/Spring + PostgreSQL/full stack**
- **Lab-6: Python/Data + PostgreSQL/Elasticsearch**

Optional/community expansion for Lab-4 and Lab-5.

Characteristics:

- symptom-first;
- minimal visible guidance;
- concurrency/performance/consistency/production reasoning;
- multiple valid solutions;
- explicit trade-offs;
- tests may need to be designed by the candidate;
- often includes code review or follow-up discussion;
- usually 60–180 minutes per ticket in Interview Mode.

Examples:

- concurrent updates / race condition;
- idempotent payment operation;
- deadlock/locking scenario;
- N+1/query plan/index issue;
- keyset vs OFFSET pagination;
- timezone/scheduling defect;
- ETL replay/reconciliation;
- PostgreSQL/Elasticsearch desynchronization.

**Advanced must mean advanced. Docker usage alone never makes a challenge advanced.**

---

## 8. Hint policy

Hints should normally live in the README/ticket, not as comments such as `// BUG HERE`.

Code comments should remain plausible production comments.

### Easy

Visible:

- `Starting point`
- exact test or endpoint to run
- first subsystem/layer to inspect

Optional progressive hints:

```markdown
<details>
<summary>Hint 1</summary>
Look at the values before and after the calculation.
</details>
```

### Intermediate

Visible:

- reproduction instructions;
- failing test or affected feature.

Hidden/collapsible:

- Hint 1: evidence to inspect;
- Hint 2: subsystem/layer;
- Hint 3: relevant concept.

### Advanced

Visible:

- business context;
- symptom;
- expected behaviour;
- acceptance criteria.

Hints should be optional and progressively reveal only:

1. observation strategy;
2. subsystem;
3. concept.

Never reveal the exact faulty line or fix.

---

## 9. Standard ticket format

Every challenge ticket should read like a small company assignment.

```markdown
## Ticket X — Title

**Difficulty:** Easy | Intermediate | Advanced
**Type:** Debugging | Feature | Refactoring | SQL | Data | Code Review | Production incident
**Suggested interview time:** 45 min

### Context

Why the business/system cares.

### Observed behaviour

What currently happens.

### Expected behaviour

What should happen.

### Reproduction

Endpoint, UI steps, failing test, dataset or scenario.

### Constraints

What must not be rewritten/changed.

### Acceptance criteria

Observable Definition of Done.

### Starting point

Present for Easy; optional for Intermediate; usually absent for Advanced.

### Hints

Progressive `<details>` sections.

### Follow-up discussion

Two or three questions about trade-offs, testing, production readiness or scalability.
```

---

## 10. Definition of Done policy

A realistic ticket is not complete merely because the happy path appears to work.

Depending on the task, completion should require:

- relevant tests pass;
- existing tests do not regress;
- application still builds/runs;
- contract remains compatible;
- data remains consistent;
- edge case is covered;
- code is explainable;
- candidate can state the cause;
- candidate can justify the fix;
- candidate can identify one reasonable production concern.

This follows the recurring “quality over quantity / production-minded” pattern observed in public challenges.

---

## 11. Lab-specific editorial plan

### Lab-1 — Java/Spring Boot Debugging

**Tracks:** Easy + Intermediate + Advanced

Keep existing material. Extend over time with original challenges inspired by patterns, not exact tasks.

Easy candidates:

- `BigDecimal` immutability/precision;
- null handling;
- `equals/hashCode`;
- exception/HTTP mapping;
- simple repository/filter defect.

Intermediate:

- follow Controller → Service → Repository;
- failing integration test;
- transaction rollback;
- JPA relationship/N+1 diagnosis;
- date/time bug;
- add small REST feature with tests.

Advanced:

- race condition on balance/status update;
- idempotency;
- optimistic/pessimistic locking trade-off;
- query performance and index;
- JVM/memory incident as diagnosis exercise;
- scheduler/time-zone issue.

Primary inspiration:
GetYourGuide backend, Personio, Crewmeister, George, Flipdish.

---

### Lab-2 — Java Legacy + Refactoring + TDD

**Tracks:** Easy + Intermediate + Advanced

Easy:

- strings/equality;
- duplicated side effect;
- swallowed exception;
- extract small responsibility after characterization test.

Intermediate:

- characterization tests;
- large method refactor;
- incremental new rule;
- TDD feature;
- reduce coupling without a rewrite.

Advanced:

- legacy transaction boundary;
- concurrency/idempotency in old code;
- refactor while preserving externally observable behaviour;
- code review of a tempting but unsafe rewrite;
- incremental strangler-style seam at conceptual level.

Primary inspiration:
George, Flipdish, European take-home + code-review patterns.

---

### Lab-3 — React/TypeScript + Java/Spring + PostgreSQL

**Tracks:** Easy + Intermediate + Advanced

Easy:

- wrong HTTP handling;
- page indexing mismatch;
- stale UI;
- mapper/DTO mismatch;
- loading/error/empty state.

Intermediate:

- trace browser → API → service → DB;
- optimistic update/rollback;
- validation contract;
- transaction consistency;
- full-stack feature with tests.

Advanced:

- pagination performance;
- N+1;
- concurrent edits;
- retry/idempotency;
- partial failure;
- SQL index/query-plan investigation.

Primary inspiration:
GetYourGuide frontend/backend, FACEIT, Personio, Mimo.

---

### Lab-4 — Angular + NestJS

**Tracks:** Easy + Intermediate
**Advanced:** optional later/community-driven.

Easy:

- DI/service wiring;
- observable/async data;
- validation;
- HTTP error/loading state;
- route/component bug.

Intermediate:

- existing app feature;
- server-side filtering/pagination;
- async race;
- frontend/backend contract;
- tests and code organization.

Potential advanced extension:

- caching/stale data;
- concurrent writes;
- performance.

Primary inspiration:
European frontend take-home patterns, FACEIT-style API tasks, GetYourGuide generalist approach.

---

### Lab-5 — Vue 3 + TypeScript + Laravel

**Tracks:** Easy + Intermediate
**Advanced:** optional later/community-driven.

Easy:

- form validation;
- API status/error;
- ORM query/filter;
- Vue state update;
- nullable data.

Intermediate:

- existing Laravel/Vue feature;
- permissions/business rule;
- transaction;
- query count/performance;
- frontend/backend integration.

Potential advanced extension:

- locking;
- queue/retry;
- larger SQL diagnosis.

Primary inspiration:
general European take-home + code-review patterns and realistic product-feature assessments.

---

### Lab-6 — Python/FastAPI + PostgreSQL + ETL + Elasticsearch

**Tracks:** Easy + Intermediate + Advanced

Easy:

- parse/validate input;
- missing fields;
- duplicate rows;
- simple aggregation;
- FastAPI endpoint bug;
- basic SQL reporting.

Intermediate:

- repeated incremental ingestion;
- idempotency;
- malformed data;
- reconciliation/count mismatch;
- schema change;
- batch retry;
- PostgreSQL query;
- search index stale vs DB.

Advanced:

- 10TB/large-scale design discussion;
- incremental watermark design;
- replay/backfill;
- time-zone boundary;
- deduplication strategy;
- PostgreSQL indexes/EXPLAIN;
- window functions;
- keyset pagination;
- Elasticsearch reindex/synchronization;
- partial pipeline failure and recovery.

Primary inspiration:
Equal Experts, Primer, production data-quality patterns, realistic PostgreSQL case studies.

---

### Lab-7 — Applied AI Engineering

**Tracks:** Easy + Intermediate

**Advanced:** `🚫 NOT PLANNED` for the initial scope.

Its primary objective is self-learning; realistic AI Engineer interview practice is secondary. Challenges must be original, progressive, Docker-reproducible, portable to a standard CPU-only development computer, and measurable through deterministic tests/evals.

Easy progression:

- prompt engineering and structured output;
- original fictional-CV information extraction;
- baseline search, regex/keywords, or deterministic fake before an LLM;
- visible datasets, expected outputs, precision, false positives, and false negatives.

Intermediate progression:

- RAG, chunking, embeddings, retrieval, grounding, citations, and no-answer cases;
- debugging across input, parsing, chunking, embedding, retrieval, context, prompt, generation, and validation;
- regression, retrieval, answer, and citation evals;
- controlled tools, read-only Text-to-SQL, and output validation;
- prompt injection, permissions, and sensitive-data boundaries;
- a final integrated applied challenge.

The provider abstraction must support deterministic fake/mock, optional external API, and optional local model paths. Baseline verification cannot require Internet, paid APIs, nondeterministic behavior, Ollama, GPU/CUDA, large local models, or specialized hardware. External providers use environment configuration and no committed secrets.

Primary inspiration:
High-level public patterns from the Inato AI Engineer test, Hex AI Engineering take-home, and AI Engineering Field Guide. Never copy exercises or datasets.

---

## 12. SQL/PostgreSQL editorial track

SQL must not be confined to one standalone quiz.

It should appear inside business scenarios.

### Easy SQL

- SELECT/filter;
- JOIN;
- COUNT/SUM;
- GROUP BY;
- HAVING;
- date range.

### Intermediate SQL

- CTE;
- multiple joins;
- subquery;
- window function;
- deduplication;
- reconciliation query;
- UPSERT;
- JSONB basics.

### Advanced SQL/PostgreSQL

- `EXPLAIN` / `EXPLAIN ANALYZE`;
- index choice;
- composite index ordering;
- N+1 recognition from application behaviour;
- locking;
- deadlock investigation;
- transaction isolation;
- keyset pagination;
- slow aggregate/reporting query;
- window functions in realistic reporting.

For additional non-company practice inspiration, realistic SQL case-study repositories such as Data With Danny may be used to identify problem shapes, but company-style Lab tickets should remain original.

Reference:
https://github.com/datawithdanny/postgresql-for-data-analytics

---

## 13. Debugging editorial rule

Debugging is a cross-cutting skill in every Lab.

The challenge should reward:

1. reproduce;
2. collect evidence;
3. identify the earliest divergence from expected behaviour;
4. form a hypothesis;
5. test the hypothesis;
6. correct the smallest justified cause;
7. verify;
8. add/prevent regression.

Tools vary by stack:

- Java: debugger, breakpoints, call stack, variables, exceptions, JUnit, Spring logs.
- TypeScript frontend: DevTools, Network, Console, Sources, component tests.
- Node: debugger, stack traces, async flow, tests.
- PHP: logs, stack traces, tests, debugger where configured.
- Python: debugger, exceptions, logging, pytest.
- PostgreSQL: diagnostic queries, locks, `EXPLAIN`, data comparison.
- Elasticsearch: mappings, queries, documents, refresh/index state, synchronization evidence.

A Lab should not teach “sprinkle print statements everywhere” as the primary method, although temporary logging may be a legitimate targeted tool.

---

## 14. Interview modes

Every Lab should eventually support three modes.

### Learning Mode

- no hard time limit;
- progressive hints available;
- documentation allowed;
- instructor guidance;
- objective is understanding.

### Interview Mode

- suggested ticket timebox;
- only the normal README brief and starting hint appropriate to difficulty;
- documentation/web allowed unless ticket says otherwise;
- candidate must explain reasoning.

### Review Mode

After solution:

- explain cause;
- code review;
- discuss alternative;
- “what would you do in production?”;
- introduce one small follow-up requirement.

This mirrors the repeated public pattern of take-home → review/pairing/extension.

---

## 15. AI-use policy

Because real hiring policies vary, Labs should not assume one universal AI rule.

Each challenge may declare one of:

- `AI: Allowed`
- `AI: Allowed for documentation/research, not exact solution`
- `AI: Disabled for simulation`

In all modes, the candidate must be able to explain and modify their solution.

The repository should never reward code the candidate cannot defend.

---

## 16. Copyright, confidentiality and originality

This rule is mandatory.

### We may

- cite public company challenge repositories;
- describe high-level patterns;
- create original scenarios inspired by common engineering problems;
- reuse general technical concepts;
- link to public sources in the research bibliography.

### We must not

- copy a confidential assessment;
- reproduce tasks a company asks candidates not to publish;
- reconstruct hidden GetYourGuide questions;
- copy candidate submissions as official solutions;
- copy substantial wording, datasets or exact business rules from a company challenge;
- imply a Lab is an actual test used by a named company unless it is explicitly licensed and intentionally reproduced.

Public sources are **research references**, not templates to clone.

Preferred wording:

> “Inspired by patterns observed in public European technical assessments.”

Avoid:

> “This is the test used by Company X.”

---

## 17. How to use company names

Company names belong primarily in this research document/bibliography.

Individual Lab READMEs should generally describe the pattern without branding the exercise after a company.

Good:

> “This challenge reflects common take-home and pair-programming patterns found in public European backend assessments.”

Avoid:

> “GetYourGuide Challenge Clone.”

This keeps the project original and reduces confusion about endorsement or affiliation.

---

## 18. Editorial checklist for every new ticket

Before accepting a new challenge into a Lab:

- [ ] Is the scenario plausible in day-to-day software engineering?
- [ ] Is there a clear business/system reason?
- [ ] Is the symptom observable?
- [ ] Does the candidate have a reproducible starting state?
- [ ] Does difficulty match the real complexity?
- [ ] Is there at least one objective acceptance criterion?
- [ ] Can the bug/feature be validated by tests or observable behaviour?
- [ ] Does Easy have an appropriate starting point?
- [ ] Are Intermediate hints progressive rather than revealing?
- [ ] Does Advanced avoid hand-holding?
- [ ] Is there a follow-up discussion question?
- [ ] Was the correct solution verified before the defect was seeded?
- [ ] Is the challenge original rather than copied?
- [ ] Does the README avoid revealing the root cause?
- [ ] Does it remain Docker-reproducible?
- [ ] Could a reviewer discuss trade-offs after completion?

---

## 19. Agent Continuity standard

A Lab is 100% complete only when a new agent, using the global sources and the Lab-specific README, can:

- understand the challenge;
- guide the learner;
- provide progressive hints;
- teach the related concept;
- distinguish challenge defects from infrastructure failures;
- validate a solution;
- explain the root cause and review alternatives; and
- provide a complete resolution when the learner is blocked.

Lab-1 is the first Reference Lab for this standard. Lab-2 and Lab-3 will be adapted differentially after that reference is established.

## 20. Bibliography

### Primary / official company sources

1. GetYourGuide — Software Engineer Backend Coding Interview
   https://github.com/getyourguide/swe-be-coding-interview

2. GetYourGuide — Software Engineer Frontend Coding Interview
   https://github.com/getyourguide/swe-fe-coding-interview

3. Personio — Backend Coding Challenge
   https://github.com/personio/backend-coding-challenge

4. Crewmeister — Java Coding Challenge
   https://github.com/crewmeister/java-coding-challenge

5. George Backend Chapter — Coding Challenge
   https://github.com/george-labs/george-backend-hiring

6. FACEIT — Frontend Coding Challenge
   https://github.com/faceit/frontend-coding-challenge

7. Mimo — Coding Challenge / Frontend Take-home
   https://github.com/getmimo/coding-challenge

8. Equal Experts — Data Engineering Exercise (Python)
   https://github.com/EqualExperts/data-engineering-exercise-python

9. Primer — DX Backend Challenge
   https://github.com/primer-io/dx-backend-challenge

10. Flipdish — Engineering Careers / Interview Process
    https://www.flipdish.com/es/carreras/engineering-careers

11. Wise — Engineering Interviews
    https://wise.jobs/engineering-interviews

### Secondary sources / aggregators

12. Backend Challenges — public list of open-source job challenges
    https://github.com/CollabCodeTech/backend-challenges

13. Hiring Without Whiteboards — community-curated interview process list
    https://github.com/poteto/hiring-without-whiteboards

14. Data With Danny — PostgreSQL / realistic SQL case studies
    https://github.com/datawithdanny/postgresql-for-data-analytics

### Context-only source

15. Public candidate copy of a seQura backend challenge
    https://github.com/itSQualL/sequra

Use source 15 only for general pattern triangulation. Do not reproduce the task/dataset/rules.

---

## 20. Canonical editorial rule for future agents

When creating or modifying any `Lab-*`, agents should be instructed to read this document first.

Recommended prompt prefix:

> Read `docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md` before designing or changing interview challenges. Preserve the existing Lab requirements, but make all new tickets conform to its originality, difficulty, hints, realism, Definition of Done and follow-up discussion rules.

This document is the editorial source of truth for realism across all Labs.
