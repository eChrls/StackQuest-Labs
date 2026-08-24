# Lab Specification

> Read the [Interview Research & Editorial Guide](INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md) before designing or modifying a challenge.

## Canonical structure

Official Labs use `lab-NN-stack-focus`: lowercase, two-digit number, concise stack/focus slug. Deviations require an explicit editorial decision. Labs remain independent.

Each Lab includes a complete README, Compose definition, Dockerfile(s) where needed, safe `.env.example`, source, and tests. Docker-first setup must rebuild from versioned files on a clean machine; isolate networks, volumes, ports, caches, and test data. Use deterministic migrations, seeds, and fixtures—not copied volumes or mutable external state.

A Docker-first Lab cannot silently depend on the stack runtime, compiler, package manager, database, or debugging tooling being installed on the host. Document every permitted host prerequisite explicitly, such as Git, Docker, VS Code, or Dev Containers; keep stack-specific tooling inside containers.

When visual debugging is part of a Lab, its documented path must work without a host stack runtime and must launch or attach to the process that actually reproduces the selected challenge. Validate setup and debugging from a clean checkout or an equivalently isolated Compose project; cached images or volumes are not sufficient evidence.

## External services and cloud

If a Lab uses an external provider, everything reasonably testable without it needs a reproducible/local path. Keep secrets outside Git, state possible costs, and distinguish provider failures from challenge failures.

Cloud Labs additionally require:

- a reproducible local baseline and no paid requirement for the main learning path;
- a Cost Gate before resource creation;
- environment-based credentials that are never versioned;
- least privilege and explicit live-provider assumptions;
- revalidation of current pricing and free-tier eligibility;
- tests/static validation when live cloud is unnecessary;
- mandatory teardown and post-teardown resource verification.

These cloud requirements do not apply to Labs without cloud providers. AI integrations likewise require deterministic offline tests/evals; Internet, paid APIs, nondeterministic models, local runtimes, GPUs, or specialized hardware cannot be baseline requirements.

## Documentation and Agent Continuity Test

Each README documents purpose, outcomes, difficulty, stack, architecture, profiles, commands, domain, baseline, tests, debugging, challenge/infrastructure boundaries, completion criteria, mentor context, root cause, validation, and reasonable alternatives.

A Lab is not 100% unless a new agent can understand its design, give Hint 1/2/3, teach, guide debugging, distinguish challenge from infrastructure failures, know the root cause, validate, review alternatives, and fully resolve it when requested without rediscovering it. This is the **Agent Continuity Test**.

## Challenge design, tests, and security

Intentional defects must be independent, reproducible, scoped, pedagogically useful, and observable. Candidate-facing text may reveal symptoms and expected behavior, never root cause or fix. Tickets include context, observed/expected behavior, reproduction, constraints, acceptance criteria, appropriate hints, and follow-up discussion.

Deliberate baseline failures are allowed only when documented and free of accidental failures. Temporarily prove a valid solution, verify it, restore the challenge baseline, and re-verify. Record evidence without publishing the solution.

A Lab's test/QA harness may be delivered fully built and provided to the candidate rather than written by them, when the pedagogical focus is implementation and design judgment rather than test authorship; this must be explicit in the Lab README, and acceptance tests must assert on behavior, structure, or content rather than on the presence of a specific package, so a candidate's implementation choices remain genuinely open. Where a requirement is inherently subjective (visual/UX interpretation of a brief), pair automated behavioral checks with a documented review rubric instead of pixel-comparison assertions.

Use least privilege and never commit secrets, private data, generated dependencies, dumps, host state, solutions, solved copies, backups, or patches.
