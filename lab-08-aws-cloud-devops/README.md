# Lab 08 — AWS Cloud / DevOps Foundation

## Foundation

Small Python HTTP application for cloud/DevOps practice only. Docker is the only prerequisite: no AWS account, credentials, Terraform binary or paid service is required for Easy. Compose provides `api` and an isolated `checks` profile. The API exposes `/health` and `/config`, logs requests to stdout, reads `APP_PORT`, `APP_BIND` and `APP_GREETING`, and has a local healthcheck. Terraform in `infra/` is static AWS preparation; never run `apply` in this phase.

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

Work only inside this directory. Preserve the three Easy defects and their independent checks. The temporary green proof was: E1 bind `0.0.0.0`, E2 provide `APP_GREETING=hello from lab08`, E3 narrow the ingress CIDR; all three passed, then defects were restored. Intermediate remains explicitly pending: ECR, RDS PostgreSQL, GitHub Actions CI/CD, fuller Terraform, CloudWatch/observability, optional AWS deployment and mandatory teardown verification.

## Completion

Easy is complete when a new agent can reproduce all three red checks, give Hint 1/2/3, explain Docker versus cloud evidence, validate temporary corrections without AWS, restore the baseline and avoid billable resources. Advanced is not planned in this phase.
