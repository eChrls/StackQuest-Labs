# Contributing

Thank you for helping make StackQuest Labs realistic, reproducible, and useful. By participating, you agree to follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Workflow

1. Search Issues and Discussions and discuss large changes first.
2. Read the [editorial guide](docs/INTERVIEW_RESEARCH_AND_EDITORIAL_GUIDE.md) and [Lab specification](docs/LAB_SPEC.md).
3. Create one focused branch and coherent change.
4. Validate Compose, documentation, relevant tests, and the intended challenge baseline.
5. Open a pull request with evidence and no solution leakage.

## New Labs and challenges

Official directories use `lab-NN-stack-focus`. A proposal may target an official roadmap Lab or a Community/Future idea and should state:

- stack, focus, difficulty, and challenge type;
- realistic context, acceptance criteria, hints, and follow-up discussion;
- Docker portability and reproducible baseline;
- external services, possible cost, secrets handling, and offline/local path;
- public source inspiration and how originality is preserved;
- README context needed for the Agent Continuity Test.

Do not copy confidential material or substantial wording, datasets, or exact business rules. A baseline may deliberately fail, but accidental failures are unacceptable. Temporarily prove a solution, restore the challenge state, and record verification without publishing the solution.

Never commit secrets, generated artifacts, or solutions. Preserve documented behavior and avoid unrelated Lab edits.
