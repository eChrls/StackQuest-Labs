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

Before designing a Lab or challenge, read the [Interview Research & Editorial Guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md), then follow [docs/LAB_SPEC.md](docs/LAB_SPEC.md). The guide is authoritative for realism, difficulty, hints, originality, copyright, Definition of Done, interview modes, and follow-up discussion; LAB_SPEC is the technical contract.

Every proposal must declare `Easy`, `Intermediate`, or `Advanced`; identify the target skill and challenge type; explain why the scenario is realistic; include the standard ticket structure and objective acceptance criteria; describe difficulty-appropriate starting points/progressive hints; and include a follow-up discussion. It must be original and must not copy confidential material or substantial wording, datasets, or exact rules. Cite a public source only when it actually informed the pattern.

Every new Lab must be Docker-first, isolated, rebuildable from scratch, independent of global infrastructure, and include a complete README, reproducible challenge baseline, tests, learning objective, difficulty, and completion criteria. Authors must temporarily prove a solution exists and run its verification, then restore and re-verify the intended challenge baseline before committing. Record evidence in the PR without publishing the solution. Never publish solutions or commit secrets.

## Deliberate bugs

A README may describe the **symptom**, **expected behavior**, and **learning objective**. It must not reveal the root cause, file, line, fix, or corrected code.

## Tests and CI

A baseline may intentionally contain failing tests. Document their expected purpose without disclosing solutions. Repository-wide CI checks workspace integrity and Compose syntax; it does not assume every challenge test should pass.

## Pull Requests

Use a clear scope and related issue where applicable. Include validation evidence, update the relevant README, validate Docker Compose, and confirm there are no secrets, generated artifacts, or solution leakage. Preserve the documented baseline and avoid unrelated Lab edits.
