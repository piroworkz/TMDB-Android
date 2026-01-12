# AGENTS.md

Project-specific guidance for automated changes in this repository.

## About this repository workflow (Spec-driven + Codex)
This repository follows a Spec-Driven Development workflow:
- Specs define the user-facing behavior.
- Plans define the technical approach (aligned with repo patterns).
- Tasks define small actionable steps.
- Implementation must follow strict TDD (see Constitution).

This file defines **repo guardrails** (architecture/DI/dependencies/git rules).
It is not a tutorial and must be followed by any automated changes.

### Source priority
When multiple sources exist, use the following authority order (highest to lowest):
1) `AGENTS.md`
2) `contexts/templates/constitution.md` (process / TDD rules)
3) `contexts/PROJECT_CONTEXT.md` (operational manual, if present)
4) Feature docs (`*_spec.md`, `*_plan.md`, `*_tasks.md`)
5) Code (current implementation)
   If sources conflict, STOP and ask.

---

## General practices
- Prefer `rg` for search and `apply_patch` for small edits.
- Keep edits ASCII unless a file already uses Unicode.
- Avoid destructive git commands unless explicitly asked.
- If something is unclear, search the repo first (code, tests, docs). If still unclear, ask.

---

## Git workflow (branching, commits)

### Working tree policy
- Read-only tasks (review, analysis, summarization) MUST continue even if the working tree is dirty.
- If the working tree is dirty, do NOT run git operations that modify history or working state
  (commit, merge, rebase, checkout/switch, stash, reset, push) unless explicitly asked.

### What "updated" means
- "Updated `master`" means: sync with `origin/master` using rebase (not merge):
    - `git fetch origin`
    - `git checkout master`
    - `git pull --rebase origin master`
- If rebase would rewrite local history or create conflicts, STOP and ask how to proceed.

### Branching rules
- Base branches:
    - New features MUST branch from an updated `master`:
        - `feature/<name>`
    - Issues MUST branch from the current updated feature branch:
        - `issue/<issue-title-normalized>`
- "Current feature branch" means the feature branch explicitly mentioned in the request or issue context.
- Never infer the feature branch from the currently checked out branch.
- If the target feature branch is not explicitly provided, STOP and ask which `feature/<name>` branch to use.

### PR rules (strict)
- Never commit, merge, or push changes directly to `master`.
- Do not open PRs targeting `master`.
    - PRs MUST target the current feature branch.
- Only inform when the PR is ready for review.

### Commit rules
- Prefer small commits grouped by task.
- Do not amend/rewrite history unless explicitly asked.

---

## Repo layout
- Modules use a Clean Architecture split: `domain`, `data`, and `ui`.
- Features live under `feature/{auth,media,core}` with modules like `auth_domain`, `auth_data`, `auth_ui`.
- Shared test utilities live in `test_shared`.
- Some `data` modules expose shared test helpers via `testFixtures`.
- Gradle convention plugins live in `build-logic/convention`.

---

## Build & configuration
- Build files are named after their module (e.g., `feature/auth/auth_ui/auth_ui.gradle.kts`) via `setProjectBuildFileName` in `settings.gradle.kts`.
- Dependencies and plugin aliases are centralized in `gradle/libs.versions.toml`.
- Convention plugins used across modules:
    - `tmdb.android.application`
    - `tmdb.ui.module.plugin`
    - `tmdb.framework.module.plugin`
    - `tmdb.room.module.plugin`
    - `tmdb.kotlin.module.plugin`
    - `tmdb.test.shared.plugin`
- Sensitive config goes in `local.properties` (e.g., `TMDB_API_KEY`, signing fields). Never commit secrets.

---

## Dependencies (strict rules)
- No new dependencies without explicit approval.
- All dependencies MUST be added via `gradle/libs.versions.toml`.
- Prefer existing libraries already used in the repo.
- If a new approved dependency applies broadly:
    - If it should be used by all modules or by a module type (e.g., all `*_data` modules),
      add it to the appropriate Gradle convention plugin instead of repeating it across modules.
    - If unsure whether it belongs in a convention plugin, STOP and ask.
- If an approved dependency is needed only in a single module, it MAY be added directly to that module
  (e.g., `implementation(libs.roomPaging)` in `media_data` only).

---

## Architecture & DI
- Domain modules define contracts only (interfaces, models); implementations live in `data` modules.
- Use cases in `<feature>_domain` are contracts (interfaces) implemented by repositories in `<feature>_data`.
- UI is Jetpack Compose; ViewModels are wired with Koin (`koinViewModel`), and DI modules live in `di/` packages.
- Dependency rule: `ui` -> `domain` <- `data` (no `ui` -> `data` or `domain` -> anything).
- Keep DI wiring consistent with existing patterns in the repo. Do not introduce a new DI approach.

---

## Koin DI conventions
- Koin is the only DI framework used in this repo. Do not introduce Hilt/Dagger or other DI.
- DI modules MUST live in `di/` packages inside each module.

### Binding rules
- Use `single` for shared components (Retrofit, Database, DAOs, repositories).
- Use `factory` for lightweight/stateless objects (builders, small helpers).
- If parameters are required, use Koin parameter injection (`parametersOf(...)`) (follow existing patterns).

### Qualifiers & primitives (type-safe wrappers)
- Do NOT use `named()`, `StringQualifier`, or string-based qualifiers.
- Primitive values (String, Boolean, Int, etc.) that are shared (e.g., API keys, base URLs)
  MUST be wrapped in explicit types (`@JvmInline value class` preferred).
- Shared wrapper types MUST live in `core_data` under `com.davidluna.tmdb.core_data.di`
  (or a dedicated subpackage like `.values`).
- Do not duplicate wrapper types across features/modules.

---

## Canonical implementation patterns
## Architecture Reference
![CleanArch.jpg](CleanArch.jpg)

### Framework layer (inside `<name>_data/framework`)
- Remote:
    - Retrofit interfaces use `*Api` naming (e.g., `AuthenticationApi`)
    - Remote DTOs use `Remote*` naming
- Local (Room):
    - DAOs use `*Dao` naming
    - Entities use `Room*` naming

### Mapping conventions
- Mapping functions are repository implementation details and MUST be kept `private` inside repositories.
- Mappers are simple 1:1 field mapping (+ nullability checks).
- Do not write direct tests for mappers.
- Repositories must not leak `Remote*` or `Room*` types outside `<name>_data`.

---

## Testing expectations (Mandatory)
- Follow TDD for any behavior change:
    1) Write/adjust a failing test first (red)
    2) Make the minimal change to pass (green)
    3) Refactor only after green
- Prefer fast unit tests at `domain`/`data` boundaries. Use UI/instrumented tests only when necessary.

---

## Verification
- Default verification is unit tests only (`./gradlew test`) unless the Spec/Task explicitly requires instrumented tests.
- Instrumented tests are heavy: ask for confirmation before running them unless explicitly requested.
- If changes touch `build-logic/convention`, run:
    - `./gradlew test`
    - `./gradlew :build-logic:convention:build` (or closest available task)

---

## Important design decisions
- Domain use cases are contracts only (interfaces).
- Repositories are singletons and implement one or more domain contracts when appropriate.
- Mapping helpers are private repository details and not unit-tested directly.
- Avoid string-based Koin qualifiers. Use type-safe wrapper classes.
- Dependencies are centralized via `libs.versions.toml` and convention plugins.

---

## Governance
- This file defines repo guardrails and supersedes conflicting practices.
- For process/TDD rules: see `contexts/templates/constitution.md`.
- For operational setup and commands: see `contexts/PROJECT_CONTEXT.md`.
  If docs conflict, STOP and ask.
