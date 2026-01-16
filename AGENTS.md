# AGENTS.md

## Purpose
This file defines the operating protocol for automated agents (e.g., Codex) contributing to this repository.
It prioritizes safety, correctness, and adherence to repo-specific rules.

---

## Authority / Source of Truth
When rules conflict, follow this order:

1) `context/CONSTITUTION.md`
2) `context/PROJECT_CONTEXT.md`
3) Feature `*_specs.md`
4) Feature `*_plan.md`
5) Feature `*_tasks.md`
6) This file (`AGENTS.md`)

If any conflict is detected, STOP and ask the user.

---

## Required Context Loading (Read Order)
Before making changes, always read (in order):

1) `context/CONSTITUTION.md`
2) `context/PROJECT_CONTEXT.md`
3) Feature docs referenced by the user (spec/plan/tasks)

If any of these are missing, ambiguous, or contradictory, STOP and ask the user.

---

## STOP Rules (Non-negotiable)
The agent MUST STOP and ask the user if any of the following is true:

- Scope is ambiguous or the requested change is not clearly defined by spec/tasks.
- The change requires editing files outside the described scope.
- Touching additional modules beyond those explicitly mentioned is necessary but not clearly justified or disclosed.
- A new dependency is required (even if commonly used or already approved elsewhere).
- The correct feature branch is not explicitly provided AND cannot be derived from repo rules.
- Any destructive git action would be needed:
    - `git reset --hard`
    - `git clean -fd`
    - rewriting history
- Instrumented/UI tests appear necessary but were not explicitly requested (see runbook).
- Any rule conflict exists between Constitution/Project Context/Feature docs.

---

## Tooling Policy (GitHub, Agents, Context)

### GitHub interactions
- For ALL GitHub operations (issues, PRs, labels, milestones, projects, comments), use the GitHub CLI: `gh`.
- Do NOT use MCP-based GitHub tooling unless the user explicitly asks for it.
- If GitHub changes are required but `gh` is not available/configured, STOP and ask the user.

### Context discipline
- Do not load or paste large context blobs unless required by the task.
- Prefer referencing in-repo sources (spec/plan/tasks/context) over importing external context.

---

# Git & GitHub Workflow

## Working Tree Policy (Operational)
Working tree laws are defined in `context/CONSTITUTION.md`.
This section explains the operational guardrails for agents.

### Documentation-only tasks (context/specs/plans/tasks/*.md)
- If the working tree is dirty, the agent MAY proceed and directly edit documentation files.
- Keep edits strictly limited to documentation under `context/` or other markdown docs.
- Do NOT touch production code, build files, or tests while the tree is dirty.
- If the dirty state includes non-doc files, proceed ONLY if you can avoid touching any non-doc file.

### Code-development tasks (source/build/tests)
- If the working tree is dirty, STOP and ask for explicit approval before editing:
    - Kotlin/Java source
    - Gradle/build logic
    - tests
    - resources/assets

### Updating master while dirty
- If the request requires updating `master` and the working tree is dirty, STOP and ask (user will clean first).

---

## Updating `master` (Operational)
If the request explicitly requires updating `master`, the operational steps are:

```bash
git fetch origin
git checkout master
git pull --rebase origin master
```

If rebase would rewrite history or create conflicts, STOP and ask (user will handle it).

---

## Branching Strategy (Operational)
Branching laws are defined in `context/CONSTITUTION.md`.
This section provides the operational steps for agents.

### Branch naming
- Follow `context/CONSTITUTION.md` for branch strategy and naming.
- If the correct base branch is not explicitly provided and cannot be derived safely, STOP and ask.

Example workflow:
```bash
git checkout -b issue/<kebab-case-title>
git push -u origin HEAD
```

---

## PR Conventions (Operational)
PR laws are defined in `context/CONSTITUTION.md`.
This section standardizes PR metadata for agents.

### PR Rules (Strict)
- Never commit/push directly to `master`.
- PRs target the feature branch, NOT `master`.
- Only inform when PR is ready for review.

### PR Title format
Use an imperative, scoped format:
- `feat(<scope>): <short title>`
- `fix(<scope>): <short title>`
- `test(<scope>): <short title>`
- `refactor(<scope>): <short title>`

Examples:
- `feat(favorites): add favorites screen entry point`
- `fix(media): prevent duplicate favorites`
- `test(media): cover favorites repository toggling`

### PR Description format
Include these sections in the PR body:

- **Summary**
    - What changed and why (2–5 bullets)

- **Covers**
    - Task IDs covered (e.g. `T001, T002`)

- **Validation**
    - Commands executed and results

- **Notes / Risks**
    - Tradeoffs, follow-ups, known limitations

---

## Commit Rules
- Prefer small commits grouped by task/phase.
- Do NOT rewrite history unless explicitly asked.

Examples:
- `feat(favorites): add toggle favorite use case`
- `test(media): cover favorites repository behavior`
- `refactor(media): simplify favorite mapping`

---

## Forbidden Commands
Forbidden unless explicitly requested:
- `git clean -fd`
- `git reset --hard`

If a branch is unrecoverable, prefer deleting the branch and restarting from base.

---

# Engineering Workflow

## Standard Workflow
1) Load sources of truth (Authority + Required Context Loading).
2) Restate scope (briefly) and identify impacted modules/files.
3) Ensure working tree state complies with repo policy.
4) Create / switch to correct branch naming convention.
5) Implement with strict TDD:
    - write failing test
    - make it pass minimally
    - refactor
6) Run validation commands (tests + build/lint if applicable).
7) Commit changes with clear messages.
8) Provide final report with checklist.

---

## TDD Policy
- Strict TDD: tests first (RED → GREEN → REFACTOR).
- No comments in production code.
- Prefer small, incremental changes with frequent validations.
- If adding or changing behavior, tests MUST cover:
    - happy path
    - edge case(s)
    - error path (where applicable)

---

## TDD Support: Skills First
When working with tests, TDD workflow, or test structure questions:

- First, check for relevant built-in skills under `~/.codex/skills/` (especially TDD-related skills).
- Use those skills as primary guidance for examples, steps, and conventions.
- If no skill applies OR guidance conflicts with repo rules, STOP and ask the user.

---

## Coding Conventions
- Keep changes minimal and aligned with current architecture.
- Respect module boundaries and naming conventions.
- Avoid premature abstraction (YAGNI).
- No new dependencies without explicit user approval.

---

## Validation / Definition of Done (DoD)
Before reporting completion:

- [ ] All relevant unit tests pass
- [ ] Build succeeds
- [ ] No new warnings introduced
- [ ] No new dependencies added
- [ ] No files edited outside the defined scope
- [ ] Documentation updated only if required by the tasks/spec

---

## Standard Commands (Runbook)
Use these commands as default validation tools.
If any command is not applicable, adapt minimally and document what was executed.

### Instrumented / UI tests (slow - opt-in)
Instrumented tests are SLOW and must NOT be run by default.

Run them only if:
- the user explicitly requests them, OR
- the change touches UI behavior (Compose), navigation flows, or Android framework integration, AND the risk justifies it.

Commands:
- Run all instrumented UI tests:
    - `./gradlew connectedUiTests`
- Aggregate Android UI test reports:
    - `./gradlew aggregateUiAndroidTestReports`

If instrumented tests are not executed, the final report MUST explicitly state:
- "Instrumented tests were skipped due to runtime cost (slow), unless requested."

---

# Reporting

## Reporting Format
Final response MUST include:

- Summary of changes
- Files modified
- Tasks covered (IDs)
- Validation executed (commands + results)
- Any follow-ups / risks
