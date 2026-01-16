# AGENTS.md

Project-specific guidance for AI agents working on this repository.

---

## 1. Project Overview

Android app consuming TMDB API, built with Clean Architecture, Kotlin, Jetpack Compose, and Koin DI.

### Tech Stack
| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| DI | Koin (no Hilt/Dagger) |
| Build | Gradle + convention plugins |
| Architecture | Clean Architecture (domain/data/ui) |

### Repo Layout
```
feature/{auth,media,core}/   → feature modules (*_domain, *_data, *_ui)
build-logic/convention/      → Gradle convention plugins
test_shared/                 → shared test utilities
context/                     → specs, plans, tasks, constitution
```

### Workflow (Spec-Driven + TDD)
- **Specs** define user-facing behavior
- **Plans** define technical approach (aligned with repo patterns)
- **Tasks** define small actionable steps
- **Implementation** follows strict TDD (see Constitution)

### Source Priority (highest → lowest)
1. `AGENTS.md` (this file)
2. `context/constitution.md` (process/TDD rules)
3. `context/PROJECT_CONTEXT.md` (ops cheat sheet)
4. Feature docs (`*_spec.md`, `*_plan.md`, `*_tasks.md`)
5. Code (current implementation)
6. *Optional*: `TDD_COOKBOOK.md` (examples, on demand only)

> If sources conflict, **STOP and ask**.

---

## 2. Build and Test Commands

```bash
# Unit tests (default verification)
./gradlew test

# Build convention plugins (if build-logic/ changes)
./gradlew :build-logic:convention:build

# Full build
./gradlew build
```

### Verification Rules
- Default: unit tests only (`./gradlew test`)
- Instrumented tests: **ask for confirmation** before running (they are heavy)
- If changes touch `build-logic/convention`: run both test and convention build

---

## 3. Code Style Guidelines

### General Practices
- Prefer `rg` for search and `apply_patch` for small edits
- Keep edits ASCII unless file already uses Unicode
- Avoid destructive git commands unless explicitly asked
- If unclear, search the repo first; if still unclear, ask

### Architecture Rules
- Dependency rule: `ui` → `domain` ← `data` (never `ui` → `data` or `domain` → anything)
- Domain modules: contracts only (interfaces, models)
- Data modules: implementations
- Use cases: interfaces in `*_domain`, implemented by repositories in `*_data`

### Koin DI Conventions
- Koin is the **only** DI framework; do not introduce Hilt/Dagger
- DI modules live in `di/` packages inside each module
- `single` for shared components (Retrofit, Database, DAOs, repositories)
- `factory` for stateless objects (builders, small helpers)
- **No** `named()` or string-based qualifiers
- Wrap primitives in `@JvmInline value class` (shared wrappers in `core_data`)

### Dependencies (Strict)
- No new dependencies without explicit approval
- All dependencies via `gradle/libs.versions.toml`
- Adding any dependency to a module requires approval first (explicit confirmation)
- Broadly-used dependencies go in convention plugins, not repeated across modules

### Build Configuration
- Build files named after module (e.g., `auth_ui.gradle.kts`)
- Convention plugins:
  - `tmdb.android.application`
  - `tmdb.ui.module.plugin`
  - `tmdb.framework.module.plugin`
  - `tmdb.room.module.plugin`
  - `tmdb.kotlin.module.plugin`
  - `tmdb.test.shared.plugin`

---

## 4. Testing Instructions

### TDD Workflow (Mandatory)
Strict TDD for **any** change (including refactors) with no exceptions:
1. **Red**: Write/adjust a failing test first
2. **Green**: Minimal change to pass
3. **Refactor**: Only after green

### Test Placement
- Prefer fast unit tests at `domain`/`data` boundaries
- UI/instrumented tests only when necessary
- Some `data` modules expose helpers via `testFixtures`

---

## 5. Security Considerations

- Sensitive config in `local.properties` only:
  - `TMDB_API_KEY`
  - Signing fields
- **Never** commit secrets
- Wrap shared primitives (API keys, URLs) in explicit types

---

## 6. Git & GitHub Workflow

### Working Tree Policy

This repo distinguishes between **documentation/spec tasks** and **code-development tasks**.

#### Documentation-only tasks (context/specs/plans/tasks/*.md)
- If the working tree is dirty, the agent **MAY proceed** and directly edit documentation files.
- The agent must keep edits **strictly limited** to documentation under `context/` or other markdown docs.
- The agent must **NOT** touch production code, build files, or tests while the tree is dirty.
- If the dirty state includes non-doc files, the agent must still proceed **only if** it can avoid touching any non-doc file.

#### Code-development tasks (source/build/tests)
- If the working tree is dirty and the task requires editing:
    - Kotlin/Java source
    - Gradle/build logic
    - tests
    - resources/assets
      then the agent must **STOP and ask for explicit approval** before making edits.

#### Updating master while dirty
- If the request requires updating `master` and the working tree is dirty, the agent must **STOP and ask** (user will clean first).

### What "Updated" Means
```bash
git fetch origin
git checkout master
git pull --rebase origin master
```
> If rebase would rewrite history or create conflicts, **STOP and ask** (user will clean).

### Branching Rules
| Type | Base | Pattern |
|------|------|---------|
| Feature | updated `master` | `feature/<name>` |
| Issue | current feature branch | `issue/<issue-title-normalized>` |

- "Current feature branch" = explicitly mentioned in request/issue context
- Never infer from checked-out branch or issue metadata
- If target feature branch not explicit: **STOP and ask** (always ask)

### PR Rules (Strict)
- Never commit/push directly to `master`
- PRs target the feature branch, **not** `master`
- Only inform when PR is ready for review

### Commit Rules
- Small commits grouped by task
- No history rewrite unless explicitly asked

### GitHub CLI Required
All GitHub actions via `gh` CLI:
- Issues: list, create, update, comment
- PRs: create, update, check status
- Labels, assignees, milestones

> If `gh` unavailable or unauthenticated: **STOP and ask**

### Forbidden Commands
- `git clean -fd` — banned unless explicitly requested
- `git reset --hard` — banned unless explicitly requested
- If branch unrecoverable: delete and restart from base

---

## 7. Governance

This file defines repo guardrails and supersedes conflicting practices.

| Topic | Reference |
|-------|-----------|
| Process/TDD rules | `context/constitution.md` |
| Ops setup | `context/PROJECT_CONTEXT.md` |
> If docs conflict, **STOP and ask**.
