# TMDB-Android Constitution
<!-- Process Constitution (TDD + Quality) -->

## Core Principles

### I. Test-First (NON-NEGOTIABLE)
All behavior changes MUST follow strict TDD:
- Always follow **RED → GREEN → REFACTOR**
- RED may be **non-compiling** (valid)
- Design MUST emerge from tests (never upfront design)
- Never stop mid-iteration: each step ends with tests GREEN
- Non-compiling RED is allowed only locally during the iteration; before any commit/push the project MUST compile and tests MUST be GREEN

### II. Side-Effect Free Construction
Creating an object MUST NOT trigger behavior:
- No IO, no coroutines, no collaborator calls in constructors or `init {}`
- The first test for any new component SHOULD verify creation has no side effects

### III. Tooling Failures Before Behavior Failures
A failing test must represent missing behavior, not broken tooling.
If failures are caused by:
- coroutine test setup
- dispatcher issues
- mocking misuse
  Fix the harness first, then return to behavior.

### IV. Mocking Discipline
- Early scaffolding may temporarily mock the SUT
- Once boundaries exist, the SUT MUST be real
- Prefer mocking **collaborators**, not the SUT
- Tests MUST be behavior-oriented (outputs/state/interactions)

### V. YAGNI for Tests
- Delete tests that stop adding value
- Avoid tests that only validate structure/mechanics
  Rule:
> If a test does not protect behavior, it is noise.

### VI. Clean Boundaries (After Behavior Stabilizes)
Architecture purity matters, but never at the cost of progress:
- Enforce boundaries after behavior is complete
- Avoid premature packaging/refactoring
- Keep changes minimal and feature-scoped

## Additional Constraints

### What MUST be tested
- Data sources with behavior
- Repositories with orchestration/coordination
- ViewModels (state + events)
- Error paths + meaningful edge cases
- Bugfixes require regression tests

### What MUST NOT be tested
- Domain contracts/interfaces (use cases)
- Pure models/DTOs/entities without behavior
- DI wiring
- Private mapping helpers
- Constants/enums

### Use cases in this repo (important)
Use cases in Domain are **contracts only** (interfaces / fun interfaces).
Implementations live in Data (repositories/data sources).
Do NOT create standalone `UseCaseImpl` / `Interactor` classes.
Test implementations, not contracts.

### Coroutine testing baseline
- Use `kotlinx-coroutines-test`
- `TestDispatcher` + `TestScope`
- Override `Dispatchers.Main`
- Use `coEvery` / `coVerify` for suspend calls

## Development Workflow

### Default workflow (edge-first)
When implementing a new feature:
1) Start from the data edge (remote/local)
2) Let tests define APIs, collaborators, return types, and error model
3) Complete happy path and error path first
4) Enforce architecture purity only after behavior stabilizes

### Iteration completion rule
Never stop mid-iteration:
- tests must be GREEN
- design must be same or cleaner

## Governance
- This Constitution defines the development process and supersedes any conflicting practice.
- `AGENTS.md` is the authoritative repository guardrails (architecture, DI, dependencies, git workflow).
- `PROJECT_CONTEXT.md` contains operational setup (commands, local configuration, troubleshooting).
- `TDD_COOKBOOK.md` contains example-based guidance (few-shots). Do not load by default unless needed.
- If documents conflict, STOP and ask.

**Version**: 1.0.0 | **Ratified**: 2026-01-12 | **Last Amended**: 2026-01-12