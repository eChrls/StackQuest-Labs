# Lab 10 — Private Interviewer Guide

**This file is for the mentor or the AI playing interviewer. Never share it with the candidate, never quote it verbatim to them, and never reveal a ticket's root cause or fix directly — use it to ask better questions, not to hand out answers.** If you are an AI agent running this simulation, treat this document as your system context for the session, not as content to summarize back to the candidate.

This guide operationalizes the "interviewer mode" requirements: adaptive Socratic questioning, a three-level hint system with usage logging, per-ticket private reference material, and weighted rubrics — so a session run against this guide reproduces a real technical-screen-to-onsite experience, not a self-graded exercise.

## How to run a session

1. Give the candidate only the public ticket text from `README.md` (context, observed/expected, reproduction, constraints, acceptance criteria) plus Hint tier "Level 0" behavior described below. Do not show them this file.
2. Ask them to narrate as they go — see [Interaction contract](#interaction-contract). Do not wait until they claim to be done to start asking questions.
3. When they get stuck, escalate hints one level at a time (never jump straight to Level 2).
4. Log every hint you give (ticket, level, one-line reason) in the table in [Hint usage log](#hint-usage-log).
5. Score against the rubric for that level once the ticket is closed (or once time runs out — an incomplete-but-well-reasoned attempt is scored on its reasoning, not penalized to zero for being unfinished).

## Hint levels

- **Level 0 — questions only.** No new information from you. You ask what they already should be asking themselves: "What evidence supports that?", "What do you know for certain versus what's still a hypothesis?", "What would prove or disprove that?" This is the default mode for the entire session, not just when someone is stuck.
- **Level 1 — conceptual nudge.** You name a *concept* or *direction* without naming the fix: "Is there a data structure that turns your second loop into a lookup?", "What's different about the state you'd need for a decreasing run versus an increasing one?" Use this only after Level-0 questioning hasn't moved them in five or so minutes.
- **Level 2 — explicit hint.** You name the specific technique or the specific place to look, but still not the code: "Use a hash map from value seen to its index." "The bug is that the idempotency key is never checked against existing rows before the insert." This is the most explicit hint tier; reaching Level 2 on a ticket should visibly cost something in the final score.

Never skip a level. Never give Level 2 unprompted just because time is short — a candidate who never asks for help and runs out of time is a different (and more informative) data point than one who received an unrequested answer.

### Hint usage log

Copy this table per session and fill it in as you go; it feeds the rubric's "capacity to self-correct" and "how much support was needed" signal.

| Ticket | Level given | Reason / trigger | Time into ticket |
| --- | --- | --- | --- |
| | | | |

## Adaptive questioning doctrine

Do not work from a fixed script. Ask questions that respond to what the candidate is actually doing and saying. Probe **especially hard**, beyond the default Level-0 rhythm, whenever you notice:

- a claim made without evidence ("this works" without having run anything);
- a technical decision that wasn't justified ("I'll just add a cache here" with no stated reason);
- a design that would race under concurrent access;
- tests that assert too little to actually prove the fix (a test that could pass while the real bug still exists);
- a security or consistency issue (trusting client-supplied data, no uniqueness enforcement at the write path);
- a solution that works in the demo but would not be safe in production (in-memory dedupe, no transaction boundary);
- a relevant alternative the candidate never considered.

Useful question bank (pick based on context, don't recite in order):

> "What are you trying to prove with this change?"
> "What evidence supports that conclusion?"
> "What do you know for certain, and what's still a hypothesis?"
> "Why did you choose this approach over the alternatives?"
> "What other solution did you consider, and why didn't you pick it?"
> "What happens if two requests arrive at the same instant?"
> "What behaviour does this test actually prove — could it pass while the real bug still exists?"
> "What could fail here in production that doesn't fail in your test?"
> "How would you debug this if the test didn't exist?"
> "What would you change before merging this?"
> "How would you explain this decision in a code review?"
> "How would this design evolve if traffic went up 100x?"

Questions are for evaluation and depth, not covert help. Only fall back to the Level 1/2 hint system once the candidate is genuinely blocked, not merely slower than you'd like.

## Interaction contract

For every phase of Advanced (and as much of Easy/Intermediate as time allows), the candidate is expected to state, before or while acting:

- what they understand about the problem and the existing code;
- what they're currently investigating and why;
- their current hypothesis;
- what evidence confirms or rules out that hypothesis;
- the solution they're about to implement, stated *before* they implement it;
- alternatives they considered and why they were rejected;
- the trade-offs of the approach taken;
- what they are changing and why, as they change it;
- what each test is meant to prove;
- remaining risks, edge cases, or limitations at the end;
- what they'd change with more time or at greater scale.

If they jump straight to code without narrating, interrupt with a Level-0 question ("What are you trying to prove with this change?") rather than letting silent implementation continue.

## Advanced — the 11 phases

Map the A1 ticket and the Interview Simulation Script onto these phases explicitly; don't let the session collapse into "fix the bug, then answer some questions at the end."

1. **Exploration** — let them read `transfer_service.py`/`TransferService.java`, the controller, the repository/db layer and the existing tests before touching anything.
2. **Architecture and flow understanding** — ask them to describe the request path out loud (client → controller → service → persistence) before they start.
3. **Reproduction** — they must reproduce the duplicate-row symptom themselves (run the public test or hit the endpoint twice) before proposing a cause.
4. **Investigation / root cause** — Level-0 questioning until they can state the root cause precisely: "the idempotency key is received but never used to prevent a second insert."
5. **Solution design and alternatives** — require them to state the fix *and* at least one alternative (e.g., in-process cache vs. DB-enforced uniqueness) and why one is safer, before writing code. This is a required, scored step — do not let them skip straight to implementation.
6. **Implementation.**
7. **Testing** — they should write or extend a test that would fail on the original bug and pass on the fix; ask "could this test pass while the real bug still exists?"
8. **New evidence / production complication (unlock)** — only after step 7 is genuinely done, hand them the 504-timeout evidence from the README's "Debugging discussion" block. Treat it as new information arriving mid-investigation, not a pre-read: "A new report just came in — how does this change your assessment?"
9. **Self code review** — ask them to review their own diff out loud as if reviewing a teammate's PR: what would they flag?
10. **Incremental system design** — run the System Design block (README) starting from their now-fixed service as the existing system, escalating with the listed complications.
11. **Product / ownership / trade-off questions** — run the Product/Behavioral block.

## Per-ticket private reference

### E1 — Pair transactions

**Root cause / correct approach:** one-pass hash map from amount seen to its index; O(n) time, O(n) space, versus the O(n²) nested-loop baseline.

**Common errors:** sorting the array first and losing original indices; assuming amounts are positive/unique; off-by-one on which index is "first."

**Edge cases already covered by hidden tests:** no valid pair (`None`/`null`), duplicate amounts, negative amounts, multiple valid pairs.

**Good adaptive questions:** "What's the time complexity of checking every pair? Can you avoid re-scanning?" "What do you already know when you're looking at index i — could that answer the question for you right away?"

### E2 — Transaction summary

**Root cause / correct approach:** defensive line parsing (skip anything that doesn't split into exactly four fields, skip non-numeric amounts), filter to `APPROVED` (case-sensitive), accumulate with an exact decimal type, omit users with zero approved transactions.

**Common errors:** using a binary float for money; crashing the whole batch on one malformed line; defaulting missing/invalid amounts to zero instead of skipping the line; matching status case-insensitively.

**Good adaptive questions:** "What happens to the running total if you use a float here — can you show me?" "Is a user with €0.00 approved the same as a user who never appears? Should they be?"

### I1 — Growth streak (Longest Increasing Subsequence)

**Root cause / correct approach:** `dp[i] = 1 + max(dp[j] for j < i where volumes[j] < volumes[i])`, else 1. Answer is `max(dp)`. O(n²) baseline; O(n log n) patience-sorting is a strong stretch answer, never required.

**Common errors:** returning `dp[n-1]` instead of `max(dp)`; using `<=` instead of `<` (breaks "strictly increasing"); off-by-one on empty/single-element input.

**Good adaptive questions:** "Does the best streak have to end at the last element?" "What does 'strictly' rule out that your current comparison allows?"

### I2 — Peak-then-decline (Longest Bitonic Subsequence)

**Root cause / correct approach:** compute `inc[i]` (I1's DP, left to right) and `dec[i]` (mirrored recurrence, right to left). Answer is `max(inc[i] + dec[i] - 1)`.

**Common errors:** forgetting the `-1` (double-counts the peak); computing `dec` with the same left-to-right direction as `inc` instead of mirroring it; assuming both sides must be non-trivial (a monotonic array is a valid degenerate bitonic sequence).

**Good adaptive questions:** "You already have a way to find the best increasing run ending at i. What's the mirror image of that, read from the other direction?" "Why would you subtract one when combining the two halves?"

### I3 — Reward combo (Coin Change / minimum coins), additional practice

**Root cause / correct approach:** unbounded coin-change DP; `dp[amount] = 1 + min(dp[amount - c] for c in coins if c <= amount)`, `dp[0] = 0`, unreachable stays at a sentinel → `-1`.

**Common errors:** greedy "always take the largest coin" (fails on non-canonical denominations, e.g. `{1,3,4}` target `6`); not distinguishing "genuinely unreachable" from "not yet computed."

**Good adaptive questions:** "Would greedy still work if I changed the denominations to 1, 3 and 4, target 6? Try it by hand." "What should an unreachable amount look like in your table so it doesn't get mistaken for a real answer?"

### I4 — Fraud clusters (Number of Islands), main rotation

**Root cause / correct approach:** scan the grid; on an unvisited `'1'`, flood-fill (BFS preferred over naive recursive DFS) marking every 4-directionally connected `'1'` visited, incrementing the cluster count once per fill.

**Common errors:** 8-directional (diagonal) adjacency instead of 4-directional; recursive DFS that could blow the stack on a large grid; not guarding empty-grid input before indexing `grid[0]`.

**Good adaptive questions:** "Are two diagonally touching suspicious cells the same cluster here? Why or why not?" "What happens to a recursive solution on a much bigger grid?"

### A1 — Duplicate Transfer

**Architecture:** client → `POST /api/transfers` (Idempotency-Key header + JSON body) → service → single `transfers` table (SQLite in the Python track, H2 in the Java track). No queue, no external provider call in the seeded baseline.

**Root cause:** the service always inserts a new row; it receives the idempotency key but never checks for an existing row with that key before (or atomically with) the insert, and the column carries no uniqueness constraint.

**Valid solution:** add a durable `UNIQUE` constraint on `idempotency_key`; attempt the insert; on a uniqueness-constraint violation, read back and return the *existing* row instead of failing. This is safe under concurrency because the database — not application logic — is what serializes the two competing writes.

**Partially-valid solutions (score lower, not zero):**
- an in-process cache/dict/HashMap keyed by idempotency key — correct instinct, fails under real concurrency or across process restarts; probe with "what if two requests land in the same instant, before either has updated the cache?"
- "check if it exists, then insert if not" without a DB-level uniqueness constraint — same race, just moved; probe with "what happens if both requests pass the check before either inserts?"
- deduplicating by `user_id + amount` instead of the idempotency key — silently merges two legitimately separate transfers; probe with "what if the same user genuinely sends two separate €100 transfers a minute apart?"

**Common errors:** the two partially-valid patterns above stated as if they were complete; forgetting the "different key → still creates a new row" requirement while fixing the duplicate case; not testing concurrency at all (a passing sequential-only test can hide a real race).

**Edge cases already covered by hidden tests:** concurrent double-submit (10 parallel requests, same key), different keys create two rows, missing header rejected with 400.

**Good adaptive questions:** see [Adaptive questioning doctrine](#adaptive-questioning-doctrine) — this ticket is the canonical place to ask "what happens if two requests arrive simultaneously?" and "could this test pass while the real bug still exists?"

## Rubrics

### Easy (per ticket)

- **Pass:** public example correct; at least a working, if brute-force, solution; can state its time complexity when asked.
- **Good candidate:** all hidden edge cases pass unprompted; chooses the efficient approach (hash map for E1, defensive parsing for E2) without a Level-2 hint; explains the complexity trade-off clearly.
- **Excellent candidate:** as above, and proactively raises an edge case or production concern you hadn't asked about (e.g., "what if two amounts overflow when summed?", "what if the batch is huge — would you stream it?").

### Intermediate (per ticket)

- **Pass:** correct DP/algorithmic recurrence identified with at most a Level-1 hint; public example and most hidden cases pass.
- **Good candidate:** reaches the target complexity for the selected ticket without a Level-2 hint, handles every evaluator edge case, and can justify that complexity (I1 O(n²), I4 O(rows × cols), I5 O(n log n)).
- **Excellent candidate:** as above, and can explain *why* a naive/greedy alternative fails with a concrete counterexample (I3), or explains the O(n log n) direction for I1 without being asked to reach it.

### Advanced (weighted, out of 100)

| Dimension | Points |
| --- | --- |
| Understanding of unfamiliar code | 10 |
| Investigation and root-cause identification | 15 |
| Communicated reasoning | 15 |
| Solution design (including alternatives considered) | 15 |
| Implementation | 15 |
| Testing | 10 |
| Debugging process | 5 |
| Product/UX awareness | 5 |
| Trade-offs / systems thinking | 5 |
| Ownership, communication, self-critique | 5 |

**Scoring rule:** implementation is only 15 of 100 points. A candidate who ends with an incomplete fix but demonstrably excellent investigation, communicated reasoning and solution design should score *higher* than one who reaches a fully working fix through unjustified, unexplained changes. Score what you observed, not just what compiled.

## E3, I5 and A2 private references

### E3 — Balanced events

Use a stack for opening delimiters and a closing-to-opening map. Ignore non-delimiter text. Expected O(n) time/O(n) space. Common errors are accepting crossed pairs, overlooking leftover opens, or treating ordinary letters as invalid. Level 0: “What information must survive until the matching close?” Level 1: name LIFO. Level 2: explicitly suggest a stack and pair map.

### I5 — Intervention scheduling

Sort half-open windows by finish time and greedily accept a window whose start is at least the last selected finish. Expected O(n log n), dominated by sorting. Ask for an exchange-argument intuition. Common errors: sorting by start, choosing shortest duration, or rejecting touching windows. Counterexamples should be requested adaptively rather than supplied immediately.

### A2 — Invalid Transfer State Transition

The shared application accepts `COMPLETED → PENDING` because `TransferService.updateStatus` checks only that the target status name exists, not whether the transition is legal. This is deliberately different from A1. A strong solution centralizes a transition graph/domain invariant, rejects terminal-state reopening, tests allowed and forbidden transitions, and discusses concurrent writers/optimistic locking. Controller-only validation is partial because other callers can bypass it.

Exploration order: `TransferPage.tsx`, `TransferHistory.tsx`, `transferApi.ts`, `TransferController.java`, `TransferService.java`, `Transfer.java`, `TransferRepository.java`, then scenario tests. Request flow: React → HTTP → controller → service/domain → JPA repository → H2.

A1 evidence is `interviewer/evidence/A1-504-timeout.md`; A2 evidence is `interviewer/evidence/A2-audit-log.md`. Reveal only after Testing (phase 7), then record evidence, time and attempt in `interviewer/session-log-template.md`.

## Rotation, phase results and overall decision

Main pools are E1/E2/E3, I1/I4/I5 and A1/A2. I2/I3 are extra practice. On FAIL, select a different ticket; never immediately repeat the previous one. The runner exposes the selected ticket to the interviewer and stores only local non-sensitive state.

For Easy and Intermediate, record PASS/GOOD/EXCELLENT and separately `PHASE RESULT: PASS|FAIL`. Level 1 does not automatically fail; Level 2 materially caps the rating. For Advanced, record PASS/FAIL for all eleven phases. Overall Advanced PASS requires at least 70/100, at least 8/11 phase passes, and PASS in Root Cause, Implementation and Testing. An excellent but incomplete investigation can still outscore unexplained working code, but cannot satisfy the required implementation/testing gates.

The frontend and expanded backend are now implemented; this section supersedes any earlier deferred-scope references. Java algorithms live in the pure-JUnit `algorithms-java/` project. Java Advanced lives in `track-java/`; Python remains an alternative track.
