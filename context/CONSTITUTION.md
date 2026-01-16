# CONSTITUTION.md

## 1. Purpose
This document defines the non-negotiable, repository-wide rules for development.
All contributors (human or automated agents) MUST follow this constitution.

---

## 2. Authority & Document Hierarchy
When rules conflict, follow this order (highest authority first):

1) `context/CONSTITUTION.md`
2) `context/PROJECT_CONTEXT.md`
3) Feature `*_specs.md`
4) Feature `*_plan.md`
5) Feature `*_tasks.md`
6) `AGENTS.md`

If any conflict is detected, STOP and ask the user.

---

## 3. Scope Control (Anti-Scope Creep)
- Only change files required to implement the behavior defined by the spec/tasks.
  This may involve multiple modules if necessary to complete the feature end-to-end.
- Do NOT refactor, rename, reformat, or "clean up" unrelated code.
- Do NOT introduce architectural changes unless explicitly required by spec/tasks.
- Do NOT expand the work beyond the feature’s acceptance criteria.

### Multi-module changes (allowed)
- Cross-module edits are allowed when they are necessary to implement the feature, for example:
    - domain contracts + data implementations + UI integration
    - repositories + mappers + database/network wiring
    - shared/core updates required by the feature

### Disclosure rule (required)
- If implementation requires touching additional modules beyond those explicitly mentioned,
  the agent MUST state:
    - which modules will be touched, and
    - why each one is necessary
      before proceeding.
- If necessity is unclear or debatable, STOP and ask.

### Ambiguity handling
- If the spec/tasks do not clearly justify a change, STOP and ask.

---

## 4. Git & Branching Laws
- Never commit or push directly to `master`.
- All changes MUST be made on a feature/issue branch.
- Branch naming MUST follow the repository strategy below.
- History rewriting is forbidden unless explicitly requested.

### Branch Types
- Feature branches:
    - Base: updated `master`
    - Pattern: `feature/<name>`

- Issue branches:
    - Base: the current feature branch (MUST be explicitly stated in the request/issue context)
    - Pattern: `issue/<issue-title-normalized>`

### Non-inference rule
- The "current feature branch" MUST NOT be inferred from the checked-out branch or GitHub metadata.
- If the target base branch is not explicit, STOP and ask.

### PR Target
- PRs MUST target the feature branch, NOT `master`.

---

## 5. Working Tree Policy (Global)
This repo distinguishes between documentation work and code-development work.

- Documentation-only tasks MAY proceed on a dirty working tree, as long as changes are strictly limited to documentation.
- Code-development tasks MUST NOT proceed if the working tree is dirty.
- If code changes are required while dirty, STOP and ask for explicit user approval.

---

## 6. Dependency Policy
- Adding any dependency (even if commonly used) requires explicit user approval.
- Prefer existing approved dependencies and existing internal abstractions.
- If a new dependency seems required: STOP and ask.

---

## 7. Test-Driven Development (TDD) Laws
- Strict TDD is mandatory: RED → GREEN → REFACTOR.
- Any behavior change MUST be accompanied by tests.
- No "drive-by" changes without tests.
- If tests cannot be written or executed: STOP and ask.

---

## 8. Architecture & Module Boundaries
- Respect existing module boundaries.
- Domain layer MUST NOT depend on data/UI layers.
- Do not move code across modules unless tasks/spec explicitly require it.

---

## 9. Security & Secrets
- Never commit API keys, tokens, credentials, or secrets.
- Use the repo-approved mechanism for secrets injection.
- If the mechanism is unclear, consult `context/PROJECT_CONTEXT.md` or STOP and ask.
- Avoid logging sensitive data.

---

## 10. Destructive Operations
Forbidden unless explicitly requested:
- `git clean -fd`
- `git reset --hard`
- rewriting published history

Preferred recovery strategy:
- discard the branch and restart from base.

---

## 11. Definition of Done (DoD)
A change is considered done only when:
- Tests pass (unit tests minimum)
- Build succeeds
- No new warnings introduced
- No new dependencies added without approval
- Changes remain within the defined scope

---

## 12. STOP Rules (Summary)
The agent MUST STOP and ask the user if:
- scope is unclear
- a dependency is required
- branch/base is unclear or cannot be derived safely
- touching additional modules is necessary but not clearly justified or disclosed
- working tree is dirty and code changes are required
- destructive git actions are needed
- tests cannot reasonably be added/executed
- any doc/source-of-truth conflicts exist
