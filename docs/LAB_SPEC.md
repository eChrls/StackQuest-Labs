# Lab Specification

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

## Documentation

Each README describes purpose, learning objectives, difficulty (`Beginner`, `Intermediate`, `Advanced`), effective stack, architecture, profiles, commands, domain, baseline, tests, debugging approach, and completion criteria. Document justified exceptions.

## Challenge design

Intentional bugs must be reproducible, scoped, pedagogically useful, and observable through tests, logs, behavior, or debugging. Documentation may reveal symptoms, expected behavior, and learning goals, never root cause, file, line, fix, or corrected code. Pending features need boundaries, acceptance criteria, and test expectations. Prefer realistic failures over tricks.

Seeds and migrations must deterministically recreate required data. Never depend on copied volumes or mutable external state.

## Tests and baseline

Tests provide evidence and protect contracts. Deliberate baseline failures are allowed but must be documented without leaking solutions. Test services must be reproducible and isolated.

Before submission, the author must temporarily demonstrate a valid solution, run verification, then restore and re-verify the intended challenge baseline. Only the challenge state is committed.

## Security and hygiene

Use least-privilege containers where practical and avoid unnecessary mounts or exposure. Never commit secrets, private data, generated dependencies, dumps, or host state. Solutions, solved copies, answer files, backups, `.bak` files, and solution patches are prohibited.

## Acceptance

A Lab is ready when its clean build is reproducible; Compose is valid; documentation is complete; tests match the documented baseline; seeds are deterministic; challenge bugs and pending features meet this contract; completion criteria are testable; a solution has been privately validated; and no solution leaks into the baseline.
