# Detailed Roadmap

The root [README](../README.md) is the public status board. Editorial decisions follow the [editorial guide](INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md); construction follows [LAB_SPEC](LAB_SPEC.md).

## Product roadmap — eight official Labs

| Lab                                   | Canonical directory                | Base         | Easy         | Intermediate    | Advanced           | Agent Continuity | Full experience |
| ------------------------------------- | ---------------------------------- | ------------ | ------------ | --------------- | ------------------ | ---------------- | --------------- |
| Lab 01 — Java/Spring Debugging        | `lab-01-java-spring-debugging`     | `✅ DONE`    | `✅ DONE`    | `✅ DONE`       | `✅ DONE`          | `✅ DONE`        | `✅ DONE`       |
| Lab 02 — Java Legacy & Refactoring    | `lab-02-java-legacy-refactoring`   | `✅ DONE`    | `✅ DONE`    | `✅ DONE`       | `✅ DONE`          | `✅ DONE`        | `✅ DONE`       |
| Lab 03 — React + Spring Full-Stack    | `lab-03-react-spring-fullstack`    | `✅ DONE`    | `✅ DONE`    | `✅ DONE`       | `⏳ PENDING`       | `✅ DONE`        | `⏳ PENDING`    |
| Lab 04 — Angular + Spring Enterprise  | `lab-04-angular-spring-enterprise` | `✅ DONE`    | `✅ DONE`    | `✅ DONE`       | optional/community | `✅ DONE`        | `✅ DONE`       |
| Lab 05 — Vue + Laravel/PHP Full-Stack | `lab-05-vue-laravel-php`           | `✅ DONE`    | `✅ DONE`    | `✅ DONE`       | optional/community | `✅ DONE`        | `✅ DONE`       |
| Lab 06 — Python Data Engineering      | `lab-06-python-data-engineering`   | `✅ DONE`    | `✅ DONE`    | `⏳ PENDING`    | `⏳ PENDING`       | `✅ DONE`        | `⏳ PENDING`    |
| Lab 07 — Applied AI Engineering       | `lab-07-applied-ai-engineering`    | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING`    | `🚫 NOT PLANNED`   | `⏳ PENDING`     | `⏳ PENDING`    |
| Lab 08 — AWS Cloud & DevOps           | `lab-08-aws-cloud-devops`          | `⏳ PENDING` | `⏳ PENDING` | `⏳ PENDING`    | `🚫 NOT PLANNED`   | `⏳ PENDING`     | `⏳ PENDING`    |

Base and track completion are separate. Lab 01 and Lab 02 are complete at 100%. Lab 03 Advanced remains pending because A1/A2 still require technical validation. Lab 04 and Lab 05 have completed their initial scopes, with Advanced optional/community. Lab 06 has Foundation and Easy validated; Intermediate and Advanced remain pending.

## Lab 04 — Angular + Spring Enterprise

Planned stack: Angular, TypeScript, Java, Spring Boot, and PostgreSQL. Focus: Reactive Forms, RxJS, services, HttpClient, interceptors, guards, validation on both sides, REST, DTOs, transactions, JPA, integration debugging, and authorization boundaries.

The former Angular + NestJS direction is no longer an official numbered Lab. A **Node.js + NestJS backend challenge** remains a Future / Community Lab idea without an official number.

## Lab 07 — Applied AI Engineering

Self-learning is primary; realistic AI interview practice is secondary. Baseline tests/evals cannot require Internet, paid APIs, secrets, nondeterministic responses, GPU/CUDA, or specialized hardware. Advanced remains `🚫 NOT PLANNED` initially.

## Lab 08 — AWS Cloud & DevOps

**Docker • AWS • CI/CD • Terraform • Debugging**

The local Docker/Compose application must work before cloud activity. AWS live mode is optional, cost-gated, and followed by mandatory teardown. The principal learning path has no paid requirement.

All milestones are `⏳ PENDING`:

- local Docker baseline and static/IaC validation;
- Cost Gate: plan, eligibility, credits, expiration, region, eligible services, estimate, and active resources;
- E1 cloud foundations and EC2 deployment;
- E2 networking, least exposure, public backend/private database;
- E3 configuration, health, logs, and cloud debugging;
- I1 ECR/image deployment flow;
- I2 managed RDS PostgreSQL;
- I3 GitHub Actions CI/CD;
- I4 Terraform reproducibility;
- I5 observability, CloudWatch basics, failures, and rollback reasoning;
- teardown verification for EC2, RDS, ECR, networking, and unintended billable resources.

Before live deployment, current AWS pricing and Free Tier/Free Plan eligibility must be re-verified. Completion without verified teardown does not meet Definition of Done.

## Current build order (temporary)

This is implementation sequencing, not product priority:

1. Lab 01 — reference Lab at 100%
2. Lab 02 — reference-standard adaptation
3. Lab 03 — reference-standard adaptation
4. Lab 06 — Data Engineering
5. Lab 07 — Applied AI
6. Lab 04 — Angular + Spring
7. Lab 05 — Vue + Laravel/PHP
8. Lab 08 — AWS Cloud/DevOps

The sequence stabilizes and reuses the Reference Lab pattern before eight implementations diverge. It may disappear when all Labs are complete.

## Agent Continuity standard

A Lab reaches 100% only when a new agent can understand it, teach it, give Hint 1/2/3, guide debugging, separate challenge and infrastructure failures, know the root cause, validate and review alternatives, and fully resolve it when requested without rediscovery.

Planned capabilities remain visible while pending. Update status only with evidence.

When closing or promoting a Lab track, reconcile in the same closeout: the local Lab README, `README.md`, `README.es.md`, and `docs/ROADMAP.md`.

## Repository presentation

The existing social preview remains a useful descriptor but still displays the former “Real-World Technical Interview Labs” wording. **SOCIAL PREVIEW REBRAND — `⏳ PENDING`**; it does not block this migration.
