![StackQuest Labs social preview](docs/assets/social-preview.png)

# StackQuest Labs

[Español](README.es.md)

StackQuest Labs is a collection of Docker-first, repository-based technical challenges. Each Lab is an unfamiliar project to run, inspect, debug, test, refactor and explain. The goal is to learn new stacks and technologies, practise production-minded engineering, solve realistic problems and prepare for technical interviews.

The current eight Labs are the initial content set. They deliberately include observable baseline failures: reproduce the symptom, form a hypothesis, use tests and logs, make a small change, and explain the trade-off. See each local README for candidate guidance, acceptance criteria, Hint 1/2/3, guided debugging, mentor spoilers, troubleshooting and validation evidence.

## Catalogue

| Lab | Stack | Content / objectives |
|---|---|---|
| [Lab 01 — Java/Spring Debugging](./lab-01-java-spring-debugging/) | Java 21, Spring Boot, Maven, PostgreSQL, JUnit | Debug an unfamiliar payment API, HTTP contracts, nullability, validation, persistence and tests. |
| [Lab 02 — Java Legacy & Refactoring](./lab-02-java-legacy-refactoring/) | Java 21, Spring Boot, Maven, PostgreSQL, JPA/Flyway, JUnit/Mockito | Characterization tests, legacy side effects, incremental refactoring, TDD, refunds and concurrency reasoning. |
| [Lab 03 — React + Spring Full-Stack](./lab-03-react-spring-fullstack/) | React, TypeScript, Java, Spring Boot, PostgreSQL | Cross-layer payment debugging, DTOs, async UI state, REST contracts, transactions and business rules. |
| [Lab 04 — Angular + Spring Enterprise](./lab-04-angular-spring-enterprise/) | Angular, TypeScript, Java, Spring Boot, PostgreSQL | Reactive Forms, RxJS, HTTP state, validation, authorization, JPA transactions and integration debugging. |
| [Lab 05 — Vue + Laravel/PHP Full-Stack](./lab-05-vue-laravel-php/) | Vue 3, TypeScript, PHP, Laravel, MySQL | Reactive task workflows, forms, API errors, authorization, business rules, persistence and transactions. |
| [Lab 06 — Python Data Engineering](./lab-06-python-data-engineering/) | Python, FastAPI, PostgreSQL, Elasticsearch, pytest | Ingestion, data quality, idempotency, reporting, synchronization, search and operational recovery. |
| [Lab 07 — Applied AI Engineering](./lab-07-applied-ai-engineering/) | Python, FastAPI, Pydantic, deterministic provider, SQLite | Structured extraction, evals, retrieval, grounding, safe Text-to-SQL, tools and prompt-injection defense. |
| [Lab 08 — AWS Cloud & DevOps](./lab-08-aws-cloud-devops/) | Docker, AWS concepts, Terraform, GitHub Actions, PostgreSQL | Containers, CI/CD, IaC, least privilege, RDS/ECR, observability, incident diagnosis and teardown. |

## How to use a Lab

Read its README, run the documented Compose configuration, and start with one focused challenge. Keep challenge failures separate from dependency, database or Docker failures. A temporary green solution is a learning exercise; restore the deliberate baseline afterward. No Lab requires paid cloud services for its main local path, and secrets must remain outside Git.

## Versioned development setup

Labs that need visual debugging include tracked `.devcontainer/devcontainer.json` and `.vscode/launch.json` files. Open the Lab directory in VS Code, install the Dev Containers extension, run **Dev Containers: Reopen in Container**, wait for the stack-specific extensions to finish installing, and choose the named launch configuration documented by that Lab. These files are intentionally committed: they contain reproducible editor/container configuration only—never tokens, personal paths, private keys or production credentials. Lab 08 uses Docker diagnostics instead and therefore has no artificial Dev Container/debug configuration.

## Contributing

These eight Labs are the initial content, not a closed curriculum. Any contributor can add Easy, Intermediate, Advanced or specialised challenges to any Lab. New work should preserve the canonical directory structure, deterministic local execution, independent observable defects, progressive hints, acceptance criteria, tests, troubleshooting, mentor guidance and Agent Continuity described in [LAB_SPEC](docs/LAB_SPEC.md). See [CONTRIBUTING.md](CONTRIBUTING.md) for the contribution workflow and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for community expectations.

The editorial rationale is documented in the [Interview Research & Editorial Guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md). [ROADMAP.md](docs/ROADMAP.md) is reserved for future project ideas, not construction history.

## Safety and scope

Run commands from the repository root unless a Lab says otherwise. Do not commit credentials, provider state, generated dependencies, dumps or local volumes. Cloud Labs require a cost review, least-privilege credentials and mandatory teardown before any optional live experiment.
