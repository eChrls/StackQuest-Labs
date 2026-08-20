# Real-World Technical Interview Labs

[English](README.md) | [Español](README.es.md)

Dockerized broken projects for practicing debugging, legacy code, testing, refactoring and technical interviews.

[![License: MIT](https://img.shields.io/github/license/eChrls/Labs)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/eChrls/Labs)](https://github.com/eChrls/Labs/stargazers)
[![Forks](https://img.shields.io/github/forks/eChrls/Labs)](https://github.com/eChrls/Labs/forks)
[![Contributors](https://img.shields.io/github/contributors/eChrls/Labs)](https://github.com/eChrls/Labs/graphs/contributors)
[![Issues](https://img.shields.io/github/issues/eChrls/Labs)](https://github.com/eChrls/Labs/issues)
[![Workspace integrity](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml/badge.svg?branch=main)](https://github.com/eChrls/Labs/actions/workflows/workspace-integrity.yml)

These deliberately imperfect projects simulate repositories received during a technical assessment or when joining an existing team. They are real-world applications—not algorithmic katas—and cover backend, frontend, data, tests, and legacy code in reproducible environments.

## Why this project?

Many technical assessments do not begin with an empty project. They ask you to run an unfamiliar application, understand its codebase, trace behavior across files and layers, locate bugs, interpret tests, debug failures, change legacy code, and add features without regressions. These Labs let you practice exactly that work.

## Philosophy

- **Docker-first and reproducible.** Git and Docker are the only expected host dependencies.
- **One Lab, one isolated environment.** Runtimes, services, databases, and dependencies stay inside each Lab.
- **Deliberate failures.** A failing test may be evidence for the challenge rather than repository breakage.
- **Debugging first.** Investigate symptoms and follow evidence before changing code.
- **Realistic code.** Labs model layered applications, persistence, side effects, and imperfect legacy design.
- **Tests as evidence.** Tests expose behavior, protect contracts, and guide refactoring.
- **No hidden global runtimes.** A clean machine can rebuild a Lab from its versioned files.
- **No committed solutions.** Challenge baselines remain unsolved; solution branches, patches, and backups do not belong here.

Docker provides the laboratory infrastructure. Docker itself is not necessarily the skill being assessed.

## Lab catalog

| Lab | Stack and focus | Difficulty | Status |
| --- | --- | --- | --- |
| [Lab 1](Lab-1/README.md) | Java + Spring Boot debugging | Beginner | 🟢 **Available** |
| [Lab 2](Lab-2/README.md) | Java legacy code + refactoring + TDD | Advanced | 🟢 **Available** |
| Lab 3 | React + TypeScript + Java/Spring Boot | Intermediate | 🟡 **In progress** |
| Lab 4 | Angular + TypeScript + NestJS | Intermediate | ⚪ **Planned** |
| Lab 5 | Vue 3 + TypeScript + Laravel | Intermediate | ⚪ **Planned** |
| Lab 6 | Python + FastAPI + PostgreSQL + Elasticsearch/Data | Advanced | ⚪ **Planned** |

See the single source of truth in the [roadmap](docs/ROADMAP.md).

## How it works

```text
clone
  ↓
choose Lab
  ↓
Docker builds environment
  ↓
run project/tests
  ↓
investigate failures
  ↓
debug
  ↓
fix/refactor/implement
```

Each Lab README documents its domain, services, commands, baseline, and completion criteria.

## Who is this for?

- Junior and early-mid developers building confidence with production-shaped code.
- Developers preparing for technical interviews.
- Developers practicing navigation of unfamiliar codebases.
- Contributors interested in designing realistic challenges.

## Difficulty

- **Beginner:** guided scope and a small codebase; core debugging skills.
- **Intermediate:** multiple layers or technologies; broader investigation and regression risk.
- **Advanced:** legacy constraints, ambiguous evidence, architectural tradeoffs, or substantial safe refactoring.

Difficulty describes the challenge, not a required job title.

## Contributing

Contributions are welcome. Read [CONTRIBUTING.md](CONTRIBUTING.md) before proposing a new Lab, improving an existing challenge, reporting a broken challenge, improving documentation, adding tests, or improving portability. New Labs must follow the [Lab specification](docs/LAB_SPEC.md).

## Community

- [Issues](https://github.com/eChrls/Labs/issues) — reproducible defects and documentation problems.
- [Discussions](https://github.com/eChrls/Labs/discussions) — questions, ideas, and learning conversations.
- [Code of Conduct](CODE_OF_CONDUCT.md) — community expectations.
- [Security policy](SECURITY.md) — responsible reporting and challenge-bug boundaries.
- [Support](SUPPORT.md) — where to ask or report.

## License

Released under the [MIT License](LICENSE).
