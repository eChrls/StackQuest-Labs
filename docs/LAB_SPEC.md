# Lab Specification

> **Authoritative prerequisite:** Before designing or modifying a challenge, read [INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md](INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md). It is the editorial source of truth for realism, research, difficulty, hints, Definition of Done, originality, copyright, interview modes, and follow-up discussion.

This is the technical contract for future Labs. Stack-specific exceptions are allowed only when justified in the Lab README and when isolation and reproducibility remain intact.

## Minimum structure

```text
Lab-N/
├── README.md
├── compose.yml
├── Dockerfile(s)
├── .dockerignore
├── .env.example
├── source...
└── tests...
```

Use the next available `Lab-N` name and never couple Labs together.

## Environment

- Docker-first: no stack runtime should be required globally.
- Compose must rebuild from versioned files on a clean machine.
- Pin meaningful versions and document them.
- Scope networks, volumes, ports, and caches to the Lab; bind ports locally unless justified.
- Use profiles for optional test/debug services when appropriate.
- Separate development and test databases when state could interfere.
- `.env.example` contains safe placeholders only; never secrets.

AI Labs must keep their essential tests and evals deterministic and reproducible through fakes, mocks, fixtures, or equivalent test doubles. Any external model/provider integration must have an offline fallback or test double, be configured through environment variables, and work without versioned secrets. A paid API, Internet connection, nondeterministic model response, local model runtime, or GPU must not be required for baseline verification.

## Documentation

Each README describes purpose, learning objectives, difficulty (`Easy`, `Intermediate`, or `Advanced`), effective stack, architecture, profiles, commands, domain, baseline, tests, debugging approach, and completion criteria. Document justified exceptions.

## Challenge design

Intentional bugs must be reproducible, scoped, pedagogically useful, and observable through tests, logs, behavior, or debugging. Documentation may reveal symptoms, expected behavior, and learning goals, never root cause, file, line, fix, or corrected code. Pending features need boundaries, acceptance criteria, and test expectations. Prefer realistic failures over tricks.

Every ticket follows the standard structure in the editorial guide: context, observed and expected behavior, reproduction, constraints, acceptance criteria, difficulty-appropriate starting point/hints, and follow-up discussion. Easy requires a visible starting point; Intermediate uses progressive hints; Advanced provides minimal help. Code comments must remain production-plausible, never artificial markers such as `// BUG HERE`.

Every proposal declares its difficulty, target skill, challenge type, realism, and original nature. Public assessments may inspire recurring patterns, but confidential material and substantial copies of wording, datasets, or business rules are prohibited.

Seeds and migrations must deterministically recreate required data. Never depend on copied volumes or mutable external state.

## Tests and baseline

Tests provide evidence and protect contracts. Deliberate baseline failures are allowed but must be documented without leaking solutions. Test services must be reproducible and isolated.

Before submission, the author must temporarily demonstrate a valid solution, run verification, then restore and re-verify the intended challenge baseline. Record the evidence in the PR without publishing the solution. Only the challenge state is committed.

## Security and hygiene

Use least-privilege containers where practical and avoid unnecessary mounts or exposure. Never commit secrets, private data, generated dependencies, dumps, or host state. Solutions, solved copies, answer files, backups, `.bak` files, and solution patches are prohibited.

## Acceptance

A Lab is ready when its clean build is reproducible; Compose is valid; documentation is complete; tests match the documented baseline; seeds are deterministic; challenge bugs and pending features meet this contract; every ticket has realistic and observable acceptance criteria plus a follow-up discussion; hints match difficulty; originality is confirmed; a solution has been privately validated; and no solution leaks into the baseline.
