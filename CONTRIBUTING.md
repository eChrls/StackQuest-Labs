# Contributing

Thank you for helping make the Labs realistic, reproducible, and useful. By participating, you agree to follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Ways to contribute

New Labs, improvements to existing Labs, Lab bug reports, better test coverage, documentation, portability, accessibility, and developer experience are all welcome.

## Workflow

1. Search Issues and Discussions.
2. Discuss large changes before implementing them.
3. Fork the repository and create a focused branch.
4. Implement one coherent change.
5. Validate Compose, documentation, and relevant tests.
6. Open a Pull Request with evidence.

## Requirements for a new Lab

Every new Lab must follow [docs/LAB_SPEC.md](docs/LAB_SPEC.md). It must be Docker-first, isolated, rebuildable from scratch, independent of global infrastructure, and include a complete README, reproducible challenge baseline, tests, learning objective, difficulty, and completion criteria. Authors must temporarily prove a solution exists, then restore the challenge baseline before committing. Never publish solutions or commit secrets.

## Deliberate bugs

A README may describe the **symptom**, **expected behavior**, and **learning objective**. It must not reveal the root cause, file, line, fix, or corrected code.

## Tests and CI

A baseline may intentionally contain failing tests. Document their expected purpose without disclosing solutions. Repository-wide CI checks workspace integrity and Compose syntax; it does not assume every challenge test should pass.

## Pull Requests

Use a clear scope and related issue where applicable. Include validation evidence, update the relevant README, validate Docker Compose, and confirm there are no secrets, generated artifacts, or solution leakage. Preserve the documented baseline and avoid unrelated Lab edits.
