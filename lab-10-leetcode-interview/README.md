# Lab 10 — LeetCode Interview Simulation

[Español](README.es.md)

Docker-first portable interview: Easy → Intermediate → Advanced. Every phase ends in explicit **PASS** or **FAIL**. Failed phases use another ticket; random selection records and avoids the immediately previous ticket.

## Modes and honor system

In **Interview Mode**, narrate continuously and do not consult `INTERVIEWER_GUIDE.md`, `interviewer/`, `reference/`, evaluator tests or evidence until unlocked. The interviewer asks adaptive questions, logs hints/evidence and scores each phase. In **Practice / Review Mode**, all open-source material is available. Nothing is technically hidden.

Explain understanding, investigation, hypotheses, evidence, the proposal before coding, alternatives, trade-offs, what each test proves, risks and self-review. Silent coding is interrupted with a Level 0 question.

## Docker-first runner

Only Git, Docker and Docker Compose are required:

```powershell
.\lab.ps1 doctor
.\lab.ps1 start easy -Random
.\lab.ps1 test easy -Ticket E3
.\lab.ps1 start intermediate -Random
.\lab.ps1 test intermediate -Ticket I5
.\lab.ps1 test intermediate -Ticket I6
.\lab.ps1 test intermediate -Ticket I7
.\lab.ps1 start advanced -Scenario A1
.\lab.ps1 test advanced -Scenario A2
.\lab.ps1 reset advanced -Scenario A1
```

Linux/macOS: `./lab doctor|start|test|reset level ticket`. Direct equivalents:

```bash
docker compose --profile tools run --rm algorithms-java mvn test -Dgroups="E1 & public"
docker compose --profile tools run --rm python pytest -q -m public tests/test_easy_e1_pair_transactions.py
docker compose up --build backend frontend
docker compose run --rm backend mvn test -Dtest=DuplicateTransferTest
docker compose run --rm frontend npm test
docker compose run --rm frontend npm run build
```

UI: <http://localhost:18102>; API: <http://localhost:18101>. Optional native execution uses the same projects: `mvn test` in `algorithms-java/` or `track-java/`, `python -m pytest` in `track-python/`, and `npm install && npm test && npm run build` in `frontend/`.

### VS Code without local toolchains

Open `lab-10-leetcode-interview/` in VS Code and run **Dev Containers: Reopen in Container**. The TypeScript Server, Vitest and frontend run in the Compose `frontend` service; the Java/Maven backend starts as a companion service. The `algorithms-java` and `python` runners remain available with `docker compose --profile tools run --rm ...`. The host does not need Node, npm, JDK, Maven or Python installed.

## Rotation pools

| Phase | Ticket | Family | Target |
|---|---|---|---|
| Easy | E1 Pair transactions | hash map / Two Sum | O(n) time |
| Easy | E2 Transaction summary | parsing / exact decimal | O(lines) |
| Easy | E3 Balanced events | stack / string | O(n) |
| Intermediate | I1 Growth streak | DP / LIS | O(n²), O(n log n) stretch |
| Intermediate | I4 Fraud clusters | BFS/DFS | O(rows × cols) |
| Intermediate | I5 Scheduling | greedy / intervals | O(n log n) |
| Intermediate | I6 Content Recommendation | maps / grouping / multi-criteria sorting | O(w + v + c log c + v log v) |
| Intermediate | I7 Shuffle Playlist | greedy / hash map / queues / scheduling | O(n log a) |
| Advanced | A1 Duplicate Transfer | idempotency / concurrency | full stack |
| Advanced | A2 Invalid State Transition | domain consistency | full stack |

I2 Bitonic DP and I3 Coin Change remain additional practice, outside the main three-family pool. Easy/Intermediate exhaust three variants before reuse; Advanced alternates two. `.interview-state.json` stores the latest choice.

### Ticket contracts

- E1: ascending indices for any pair summing to target, or null/None; duplicates and negatives are valid.
- E2: parse `timestamp|user|amount|STATUS`, skip malformed lines, include exact `APPROVED` only, and use `BigDecimal`/`Decimal`.
- E3: ignore ordinary text and validate nesting of `() [] {} <>`; empty is balanced.
- I1: length of the longest strictly increasing subsequence; empty returns 0.
- I4: count four-directional `'1'` components; it is a main-pool ticket.
- I5: maximum compatible half-open intervention windows; justify earliest-finish greedy.
- I6: group available titles by category; emit watched categories by descending relevance, then unwatched categories alphabetically, sorting titles inside every category.
- I7: greedily schedule songs by remaining artist count, avoid the previous artist when an alternative exists, break ties by first appearance and preserve each artist's song order.

### I6 — Content Recommendation

Implement this Java 21 entry point in `algorithms-java/src/main/java/com/stackquest/lab10/Solutions.java`:

```java
public static List<String> getRecommendations(
    List<String> watchedCategories,
    List<Integer> watchedCategoryRelevance,
    List<String> availableVideoTitles,
    List<String> availableVideoCategories)
```

The two watched lists are parallel: each category has the relevance at the same index. The two available-video lists are parallel in the same way. Group the available titles by category. Categories present in the watched history come first, ordered by decreasing relevance. Remaining categories follow in alphabetical order. Within every category, order titles alphabetically, then return one flat list containing titles only.

Example: watched categories `[Backend, Music]`, relevances `[300, 120]`, titles `[Docker, Jazz, API, Yoga, Piano, Running]`, and title categories `[Backend, Music, Backend, Fitness, Music, Health]` produce `[API, Docker, Jazz, Piano, Yoga, Running]`.

Constraints: 1–200,000 watched categories and available titles; relevance is 1–1,000,000,000 and unique; category names and titles are alphanumeric with at most 10 characters. Avoid repeated linear searches through the watched categories.

Run this exercise independently, without AI or any external service:

```powershell
.\lab.ps1 test intermediate -Ticket I6
.\lab.ps1 test intermediate -Ticket I6 -Evaluator
```

Or, with local Java 21 and Maven, run `mvn test "-Dgroups=I6 & public"` from `algorithms-java/`.

After solving, discuss: time and space complexity; why a map is useful; behavior at 200,000 titles; a single-comparator alternative and its trade-offs; and how repeated relevance values or repeated titles would affect the design.

## Hints and scoring

**Level 0:** questions only. **Level 1:** conceptual nudge. **Level 2:** explicit technique/place, never complete code. All hints are logged; Level 1 is not an automatic fail, Level 2 materially limits the rating.

Easy/Intermediate receive PASS/GOOD/EXCELLENT per ticket plus mandatory `PHASE RESULT: PASS|FAIL`. PASS requires public behavior and a defensible approach; GOOD adds evaluator cases and target complexity without Level 2; EXCELLENT adds proactive edge cases, proof or meaningful alternatives.

I6 hints: **Hint 1:** decide what information each category needs before sorting titles. **Hint 2:** maps can provide both watched-category relevance and title grouping. **Hint 3:** sort categories by their rules, sort each group's titles, then flatten; a complex global title comparison is unnecessary.

### I7 — Shuffle Playlist

The complete candidate-facing statement and starter code are commented directly in `algorithms-java/src/main/java/com/stackquest/lab10/ShufflePlaylist.java`. Implement `Result.getShuffledPlaylist(List<List<String>> playlist)`. The file includes a local `Solution.main`: input is `n` followed by `n` lines containing `title artist`; output uses the same two-column format. It writes to stdout locally and also supports `OUTPUT_PATH` when set.

Run only this exercise with `.\lab.ps1 test intermediate -Ticket I7`; add `-Evaluator` for the full evaluator set or `-Reference` for the separated reference source. I7 is entirely local and does not use AI, a database, the network or another service.

I7 hints: **Hint 1:** identify the state needed for each artist. **Hint 2:** a queue per artist preserves relative song order. **Hint 3:** artist priority combines remaining count and first appearance, while the previous artist may need to be held out temporarily.

Follow-up topics: greedy correctness, tie handling, complexity, 200,000 songs, priority-queue updates, why one initial sort is insufficient, and how the problem changes if repeats are always allowed or must be minimized globally.

## Independent SQL practice

| Ticket | Exercise | Database | Focus |
| --- | --- | --- | --- |
| S1 | [Wishlist Products Summary for E-commerce Dashboard](exercises/sql/S1-wishlist-products-summary/exercise.md) | MySQL | `INNER JOIN`, `CASE`, `GROUP BY`, `COUNT`, `COUNT(DISTINCT ...)` |
| S2 | [Identifying High-Impact Delays in Order Fulfillment](exercises/sql/S2-high-impact-delivery-delays/exercise.md) | MySQL | CTE, `DATEDIFF`, monthly aggregation, dynamic filtering |

SQL tickets are standalone query exercises and are not part of the randomized algorithm rotation. Lab 10 has no reusable MySQL exercise runner, so they require no new database infrastructure; use each statement independently and consult its separate solution only in Practice / Review Mode.

## Advanced

Java is the canonical complete track. The flow is React → typed `transferApi.ts` → Spring controller → service/domain → repository → H2. The UI creates transfers, shows loading/error/success, lists history and changes status. Python is an alternative algorithms/API practice track; React is intentionally not duplicated.

**A1:** retries with one idempotency key create duplicate rows. Reproduce sequentially and reason about concurrent requests and durable uniqueness. **A2:** `COMPLETED → PENDING` is accepted because the transition rule is absent. Reproduce via history controls or `PATCH /api/transfers/{id}/status` and place the rule at the proper boundary. Reset removes the Advanced volume and rebuilds the common app, avoiding divergent copies.

The exact phases are: (1) Exploration, (2) Architecture/flow, (3) Reproduction, (4) Root Cause, (5) Design/alternatives, (6) Implementation, (7) Testing, (8) New Evidence, (9) Self Review, (10) System Design, (11) Product/Ownership. Each gets PASS/FAIL. Overall PASS requires 70/100, at least 8/11 phase passes and no FAIL in Root Cause, Implementation or Testing.

Weights: Understanding 10, Investigation 15, Reasoning 15, Design 15, Implementation 15, Testing 10, Debugging 5, Product/UX 5, Trade-offs 5, Ownership 5. A well-investigated incomplete solution may outscore an unjustified working diff.

## Layout

- `algorithms-java/`: pure Java 21/JUnit candidate and reference sources—no Spring, DB or frontend.
- `track-python/`: alternative Python candidate, tests and reference.
- `track-java/` + `frontend/`: Advanced Spring/React app.
- `exercises/`: importable ticket metadata.
- `interviewer/`: logs and evidence unlocked only after Advanced Testing.

Evaluator tests are “hidden during interview by convention,” not secret. Candidate baselines fail only the selected intentional exercise/scenario; reference runs must pass public and evaluator evidence. Docker/build/database/port failures are infrastructure failures, not candidate failures.
