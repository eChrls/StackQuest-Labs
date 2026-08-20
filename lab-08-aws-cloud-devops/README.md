# Lab 08 — AWS Cloud Deployment & DevOps

**Docker • AWS • CI/CD • Terraform • Debugging**

Status: `⏳ PENDING` · Easy: `⏳ PENDING` · Intermediate: `⏳ PENDING` · Advanced: `🚫 NOT PLANNED`

This planning and continuity scaffold defines a future Lab. It contains no application, Terraform implementation, AWS resources, or deployment credentials.

## Objective

Learn how to take a working Dockerized application from a reproducible local environment to a secure cloud environment. A secondary objective is practicing recurring Cloud/DevOps technical-assessment patterns.

## Docker-first baseline

```text
LOCAL / SAFE MODE
Git + Docker + Docker Compose
             ↓
application works locally
             ↓
CLOUD / OPTIONAL LIVE MODE
same application/container → AWS
```

AWS must never be required to clone, understand, run, test, or debug the local application. Java/Maven, Node, PostgreSQL, Terraform, and AWS CLI should not be global prerequisites when containerization can reasonably avoid them. LocalStack is not an initial requirement.

## No-paid requirement and Cost Gate

The principal learning path must be completable in Local / Safe Mode without spending money. AWS Live Mode is optional and allowed only when the learner verifies eligibility and explicitly accepts deployment.

Before creating any resource, the Cost Gate must verify:

- account eligibility and whether it uses the Free plan or Paid plan;
- remaining credits and expiration date;
- region and eligible services/configurations;
- a cost estimate before deployment;
- resources already active in the account.

Research snapshot: **2026-08-20**. At that date AWS documents a new-customer Free plan lasting up to six months, $100 initial credits, up to $100 additional credits, and eligible EC2 and RDS/PostgreSQL options. These terms are time-sensitive.

> Before any live AWS deployment, re-check the current official AWS Free Tier / Free Plan documentation.

See the detailed sources and classification in the [editorial guide](../docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md).

## Planned Easy track

### E1 — Cloud deployment foundations

A small working Dockerized application reaches EC2. Learn instance/AMI concepts, Security Groups, IAM basics, environment variables, Docker on a VM, health, logs, public HTTP, and teardown through the realistic symptom “works locally, fails in cloud”. Starting point and progressive hints are required.

### E2 — Networking / exposure

Expose the backend while keeping the database private. Investigate inbound/outbound rules, application/database ports, public/private access, and least exposure.

### E3 — Configuration / debugging

Diagnose wrong environment or port, missing variables, crashing containers, connection failures, logs, and health evidence.

## Planned Intermediate track

- **I1 — Container registry / deployment flow:** ECR, tagging, push/pull, and deployment artifact.
- **I2 — Managed PostgreSQL:** RDS PostgreSQL, migrations, private connectivity, Security Groups, secrets, and configuration.
- **I3 — CI/CD:** GitHub → tests → Docker image → registry → deployment, preferably with GitHub Actions.
- **I4 — Infrastructure as Code:** Terraform providers, resources, variables, outputs, plan, apply, state, and destroy without premature enterprise complexity.
- **I5 — Observability and cloud debugging:** logs, health, CloudWatch basics, deployment failures, connectivity, configuration, and rollback reasoning.

## Security and credentials

Credentials are environment-based and never versioned. Future implementation must apply least privilege, document live-provider assumptions, keep the database non-public, and distinguish AWS failures from challenge defects. Static validation and local tests should cover everything that does not require a live provider.

## Teardown is Definition of Done

After each cloud session, run `terraform destroy` or the documented equivalent and verify EC2, RDS, ECR, networking, and every created resource. Confirm no unintended billable resources remain.

“Challenge completed but resources still billing” is **not complete**.

## Agent Continuity checklist

Future implementation must enable a new agent to explain local and live modes, enforce the Cost Gate, guide progressive hints, separate infrastructure from challenge defects, validate each track, explain root causes and alternatives, and verify teardown without rediscovering the Lab.
