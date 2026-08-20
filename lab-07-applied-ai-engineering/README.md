# Lab 07 — Applied AI Engineering

## Purpose

Offline, deterministic foundation for structured extraction from CV/document text. Python 3.12, FastAPI, Pydantic, pytest and a provider abstraction run in Docker without GPU, Ollama, internet at test time or paid APIs. The deterministic fake provider is the reference test double; no RAG or agent framework is part of this phase.

## Run and architecture

From the repository root: `docker compose -f lab-07-applied-ai-engineering/compose.yml config`, `docker compose -f lab-07-applied-ai-engineering/compose.yml up --build api`; API is `http://localhost:18087`. Tests: `docker compose -f lab-07-applied-ai-engineering/compose.yml --profile test run --rm tests`. Flow: request DTO → `ResumeExtractor` → `Provider` (deterministic fake) → Pydantic `Resume`; eval fixtures live in `data/eval.json`.

## Baseline and Easy track

Starting Point: call `/health`, POST text to `/extract`, run pytest and inspect the raw provider dictionary before changing extraction logic. Baseline has exactly three independent red challenge tests plus one green health test.

### E1 — Structured output

Observed: a provider can return a scalar where the typed schema requires a list. Expected: every response is a valid `Resume` with typed `name`, `skills`, `experience` and `education`. Hints: (1) print the raw provider result, (2) compare it to the Pydantic model, (3) normalize or reject malformed fields at the provider boundary. Do not remove schema validation.

### E2 — Prompt/extraction quality

Observed: contextual words can become false-positive skills, while relevant skills must remain. Expected: the fixture’s skills match ground truth exactly. Hints: (1) compare extracted and expected sets, (2) inspect evidence boundaries, (3) change the extraction rule rather than hard-coding one CV.

### E3 — Evaluation metric

Observed: recall looks correct for some balanced examples but is wrong when expected and predicted sets differ in size. Expected: precision = TP/predicted and recall = TP/expected, with deterministic empty-set policy. Hints: (1) write the formula first, (2) use a two-item expected/one-item predicted fixture, (3) test the metric independently of the provider.

Guided debugging: reproduce one red test, capture raw structured output, state a hypothesis, make the smallest boundary change, rerun focused then full pytest, and explain why the metric/eval evidence supports the change.

## Learning / Interview / Review

Learning introduces structured outputs as runtime schema contracts and evals as repeatable comparisons against ground truth. Interview mode asks about provider portability, validation failure modes, precision/recall trade-offs and deterministic fixtures. Review mode checks typed boundaries, no hidden network calls, reproducibility, metric definitions and minimal changes.

### Mentor / AI spoilers

Verified roots: E1 trusts malformed provider shape without normalization; E2 treats a context word as a skill signal; E3 divides recall by predicted size. Valid alternatives include strict rejection, explicit coercion, evidence-based extraction and documented empty-set semantics. Wrong fixes: disable Pydantic, hard-code fixture names, hide false positives, or alter ground truth to make metrics green.

## Validation matrix

| Area | Evidence |
|---|---|
| Docker | Compose config, build and FastAPI startup |
| API | `/health` and `/extract` work with fake provider |
| Baseline | 3 deliberate red eval tests, 1 infrastructure/health test green |
| Green proof | temporary E1/E2/E3 corrections yield full pytest green |
| Offline | no external provider, GPU, API key or network in tests |

## Agent Continuity

Work only in this directory. Preserve the three Easy defects and their independent tests. Dataset ground truth is versioned and original. The next agent can reproduce symptoms, provide Hint 1/2/3, explain structured outputs/evals from zero, review alternatives and separate infrastructure from challenge failures without inspecting unrelated Labs.

## Intermediate track

### I1 — Retrieval, grounding and citations

Documents are split into deterministic chunks and ranked by token overlap (an offline embedding/retrieval stand-in). A grounded answer must cite `source#chunk` identifiers; when no chunk meets the evidence threshold the answer is explicitly `I don't know`. Starting point: inspect `app/retrieval.py` and query `/search`. Hints: (1) print chunk scores, (2) distinguish “top result” from “supported result”, (3) test both citation and no-answer paths. Wrong fix: always return the first chunk.

### I2 — Safe tool calling / Text-to-SQL

The tool exposes a deliberately tiny SQLite schema (`candidates(name, skills)`) and only read operations. Natural language is not SQL authority: validate an allow-listed SELECT shape and reject DELETE/UPDATE/DDL before execution. Hints: (1) log generated SQL, (2) check the verb and selected columns, (3) assert the database is unchanged after a rejected request. Wrong fix: sanitize one forbidden word or trust the model’s SQL.

### I3 — Prompt injection and permissions

Document text is untrusted data, not policy. Tool permissions are explicit (`analyst` plus an allow-list); an embedded “ignore previous instructions” must never grant access. Hints: (1) separate user identity from document content, (2) make authorization a precondition, (3) test adversarial text with a guest and an allowed analyst. Wrong fix: block one exact phrase or hide the dangerous button.

Guided debugging for all Intermediate tickets: reproduce a focused red eval, capture raw chunks/SQL/authorization decision, state the invariant, make the smallest boundary fix, run focused and full pytest, then review false positives and denial behavior.

Intermediate validation evidence: the complete offline suite reached **7/7 PASS** with temporary corrections for E1–E3 and I1–I3. After restoration, the baseline is **1 PASS + 6 deliberate FAIL**, with no accidental failures. The API exposes `/search` and `/tool/check` in addition to `/extract`.
