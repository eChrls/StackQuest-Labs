# Lab 08 — AWS Cloud / DevOps Foundation

## Foundation

Small Python HTTP application for cloud/DevOps practice only. Docker is the only prerequisite: no AWS account, credentials, Terraform binary or paid service is required for the local path. Compose provides `api` and an isolated `checks` profile. The API exposes `/health` and `/config`, logs requests to stdout, reads `APP_PORT`, `APP_BIND` and `APP_GREETING`, and has a local healthcheck. Terraform in `infra/` is static AWS preparation; never run `apply` for the local challenge.

Run from the repository root: `docker compose -f lab-08-aws-cloud-devops/compose.yml config`, `docker compose -f lab-08-aws-cloud-devops/compose.yml up --build api`, and `docker compose -f lab-08-aws-cloud-devops/compose.yml --profile test run --rm checks`. The app is intended for `127.0.0.1:18088` in local mode. Terraform can be run without credentials in a Terraform container for `fmt` and syntax checks; provider download/validate is optional and documented separately.

## Baseline

The Easy baseline has exactly three independent failing checks and no accidental failures. The API process itself starts and its localhost healthcheck can be green; challenge checks exercise service reachability, runtime configuration and least-exposure infrastructure.

### E1 — Container / port / health configuration

Observed: the process works inside its container but another service cannot reach `/health`. Expected: the service binds an interface reachable through the Compose network and the health evidence agrees with external reachability. Starting Point: inspect `APP_BIND`, published ports, `docker compose ps` and `docker compose logs api`. Hint 1: compare `127.0.0.1` with `0.0.0.0`; Hint 2: test from the `checks` container, not only inside `api`; Hint 3: change binding/configuration before changing application logic.

### E2 — Environment / runtime configuration

Observed: `/config` returns an empty greeting although the image starts. Expected: non-secret configuration arrives through Compose environment and is visible in runtime diagnostics without committing secrets. Starting Point: `docker compose config` and `docker compose exec api env`. Hint 1: inspect effective environment; Hint 2: distinguish image defaults from Compose overrides; Hint 3: add only the required non-sensitive variable.

### E3 — Cloud-readiness / Terraform exposure

Observed: `infra/main.tf` permits the application ingress from the whole internet. Expected: a minimally exposed rule suitable for this local exercise, with variables/outputs formatted and no `apply`. Starting Point: `terraform fmt -check` and inspect the security-group ingress. Hint 1: identify the CIDR; Hint 2: separate app ingress from unrestricted egress; Hint 3: validate the narrow rule offline/static before considering AWS.

Guided debugging: reproduce one check, capture config/log/health evidence, state the infrastructure hypothesis, make the smallest reversible edit, rerun the focused check and then the full profile. Docker/health failures are infrastructure evidence; they are not reasons to weaken assertions.

## Learning / Interview / Review

Learning teaches container interfaces, environment precedence, healthchecks, logs, CIDR exposure and Terraform plan/validate concepts. Interview mode requires explaining why a process can be healthy internally but unreachable externally and why credentials are never needed for static validation. Review mode checks least exposure, deterministic teardown planning, secret boundaries, portable commands and minimal diffs.

### Mentor / AI spoilers

Verified roots: E1 is loopback binding inside the container; E2 is missing runtime Compose configuration; E3 is an unrestricted ingress CIDR. Valid solutions change deployment/configuration rather than application behavior. Wrong fixes: publish more random ports, hard-code a secret, disable the healthcheck, open every AWS port, or run `terraform apply` to “see if it works”.

## Validation matrix

| Area | Evidence |
|---|---|
| Compose | `docker compose config` passes |
| Image/runtime | image builds; `/health` and stdout logs available |
| Easy baseline | exactly E1/E2/E3 fail |
| Temporary green | all three checks pass after reversible corrections |
| Terraform | static CIDR check and `fmt`; no credentials/apply |
| Cost gate | AWS account and spending are unnecessary |

Terraform provider validation may require downloading a provider; that is distinct from the offline/static checks and still must not use credentials or `apply`. For troubleshooting, inspect `docker compose ps`, `docker compose logs api`, `docker compose config`, and the effective environment before editing code.

## Agent Continuity

Work only inside this directory. Preserve the three Easy defects and three Intermediate defects, each with an independent check. The main path uses only local/static validation; no AWS account is needed.

## Acceptance

Acceptance requires a new agent to reproduce all six red checks, give Hint 1/2/3, explain Docker versus cloud evidence, validate temporary corrections without AWS, restore the baseline and avoid billable resources.

## Intermediate

The three tickets are independent: solve and rerun one named check at a time. The local suite intentionally reports E1–E3 and I1–I3 as failures. Run it with `docker compose -f lab-08-aws-cloud-devops/compose.yml --profile test run --rm --build checks`; no AWS account or push is involved.

### I1 — CI/CD image identity

Ticket: `.github/workflows/ci.yml` must build and validate a deployable image for every revision without publishing it to AWS. The current pipeline produces an identity that can later refer to different bytes, invalidating promotion and rollback evidence.

Reproduce with the full local check, then inspect `context`, `push` and `tags`. Independently validate the build from this Lab directory with `docker build -t lab08:local .`; the workflow is a Lab-owned fixture because repository-level workflow placement is outside this task's ownership.

Acceptance criteria: the build context contains this Lab's Dockerfile and application; `push` remains false; the image tag is derived from the immutable Git commit SHA; no AWS credentials, login or real push is needed; the I1 check passes.

- Hint 1: identify which tag is overwritten by the next successful build.
- Hint 2: compare the value available as `github.sha` for two commits.
- Hint 3: replace the mutable tag with the revision identity while keeping the local-only safety gate.

Guided debugging: parse the workflow, distinguish build failure from artifact-identity failure, build the same context locally, make the smallest tag change and rerun I1 followed by all checks. Mentor/AI spoiler: the deliberate root cause is `lab08:latest`; the validated solution uses `lab08:${{ github.sha }}` and leaves `push: false`. Wrong fixes include enabling ECR push, adding long-lived AWS keys, tagging with a branch name, or removing the build step.

### I2 — Terraform, ECR and private RDS

Ticket: `infra/ecr_rds.tf` declares an immutable, scan-on-push ECR repository and PostgreSQL RDS in two private subnets across availability zones. The DB subnet group and a database security group restrict PostgreSQL ingress to the application security group, but one RDS setting defeats that private connectivity model.

Reproduce with the I2 local check. Format without credentials or state using `docker run --rm -v "$PWD/lab-08-aws-cloud-devops/infra:/workspace" -w /workspace hashicorp/terraform:1.9 fmt -check`. Optional `terraform init -backend=false` and `terraform validate` download the AWS provider but still require neither credentials nor `apply`.

Acceptance criteria: ECR stays immutable with scan-on-push; RDS is not publicly accessible; credentials remain sensitive input variables; RDS uses its explicit subnet group and database security group; Terraform is formatted and validates statically; the I2 check passes without creating resources.

- Hint 1: inspect the RDS exposure flag separately from its subnet placement.
- Hint 2: trace database reachability from `aws_security_group.app` to port 5432.
- Hint 3: align `publicly_accessible` with the already declared private subnets.

Guided debugging: draw the VPC → private subnets → DB subnet group → RDS chain, then trace the security-group reference. Run format/static validation before changing the single contradictory setting. Mentor/AI spoiler: the deliberate root cause is `publicly_accessible = true`; the validated correction is `false`. Wrong fixes include adding `0.0.0.0/0` on 5432, embedding a database password, deleting the subnet group, running `apply` as a syntax test, or weakening ECR immutability.

### I3 — Observability and incident detection

Ticket: the service emits structured JSON request logs with method, path, status, client and timestamp. `observability.json` models a health probe, CloudWatch log group/retention, a metric filter for HTTP 5xx logs and an alarm. In the baseline, normal health probes miss the real endpoint and logs expire immediately, so an incident can evade detection and leave no diagnostic history.

Reproduce with I3, start the API, and compare `curl http://127.0.0.1:18088/health` with the configured `health_path`. Inspect `docker compose -f lab-08-aws-cloud-devops/compose.yml logs api`; a request produces an `http_request` JSON event. No CloudWatch deployment is required.

Acceptance criteria: the probe uses `/health`; retention is at least 14 days; the metric filter deterministically counts structured logs whose status is at least 500; the alarm consumes the same metric name; logs retain operational fields; the I3 check passes.

- Hint 1: compare the configured probe path with the handler's routes.
- Hint 2: decide whether zero-day retention preserves evidence after an alert.
- Hint 3: verify the metric filter and alarm names join into one signal path.

Guided debugging: verify health independently, inspect a real stdout log, follow `status >= 500` through metric filter to alarm, and rerun I3 after correcting probe and retention. Mentor/AI spoiler: the deliberate defects are `/status` and retention `0`; the validated configuration uses `/health` and at least 14 days. Wrong fixes include silencing request logs, treating every 404 as an outage, inventing an alarm with no metric filter, or setting `treat_missing_data` to alarm continuously.

## Intermediate validation matrix

| Ticket | Baseline evidence | Temporary solution evidence | AWS required |
|---|---|---|---|
| I1 | immutable-tag check fails | local image builds and SHA-tag check passes | No |
| I2 | private-RDS check fails | `fmt -check`, static topology and private flag pass | No |
| I3 | health/retention/signal check fails | real stdout log plus configuration check pass | No |
| Full baseline | exactly 6 deliberate FAIL, 0 accidental | exactly 6 PASS | No |

## Optional live AWS execution and Cost Gate

The main path ends before `apply`. A future, explicitly approved live profile could create ECR, container compute such as ECS/EC2, a VPC and subnets, security groups, RDS PostgreSQL, CloudWatch log groups, metric filters and alarms. ECR storage/transfer, compute, public IPv4/NAT if added, RDS instances/storage/backups, logs ingestion/retention and alarms can all generate charges.

Before any live command, verify current official pricing and Free Tier eligibility for the exact account and region, estimate a spending ceiling, configure billing alerts, confirm least-privilege credentials and review the plan. Never assume credits or Free Tier are active. Do not use production data.

Teardown is mandatory: empty/delete ECR images as required, destroy every Terraform-managed resource, delete any out-of-band compute/logs/snapshots, and check ECR, compute, RDS, VPC/NAT/public IP and CloudWatch consoles/APIs until no billable resource remains. Preserve only non-secret validation evidence; do not commit state or credentials.

## Agent Continuity — Intermediate

A new agent should first run Compose config and the six-check baseline, then isolate I1, I2 and I3 using the tickets above. Challenge failures are the six named assertions; Docker daemon/image download/provider download failures are infrastructure failures and do not count. Contributors may add further Easy, Intermediate, Advanced or specialised cloud challenges.
