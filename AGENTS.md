# AGENTS

Project-specific guidance for automated changes in this repository.

## General
- Prefer `rg` for search and `apply_patch` for small edits.
- Keep edits ASCII unless a file already uses Unicode.
- Avoid destructive git commands unless explicitly asked.

## Repo Layout
- Modules use a Clean Architecture split: `domain`, `data`, and `ui`.
- Features live under `feature/{auth,media,core}` with modules like `auth_domain`, `auth_data`, `auth_ui`.
- Shared test utilities live in `test_shared`.
- Some `data` modules expose shared test helpers via `testFixtures`.
- Gradle convention plugins live in `build-logic/convention`.

## Build & Configuration
- Build files are named after their module (e.g., `feature/auth/auth_ui/auth_ui.gradle.kts`) via `setProjectBuildFileName` in `settings.gradle.kts`.
- Dependencies and plugin aliases are centralized in `gradle/libs.versions.toml`.
- Convention plugins used across modules: `tmdb.android.application`, `tmdb.ui.module.plugin`, `tmdb.framework.module.plugin`, `tmdb.room.module.plugin`, `tmdb.kotlin.module.plugin`, `tmdb.test.shared.plugin`.
- Sensitive config goes in `local.properties` (e.g., `TMDB_API_KEY`, signing fields).

## Architecture & DI
- Domain modules define contracts only (interfaces, models); implementations live in `data` modules.
- UI is Jetpack Compose; ViewModels are wired with Koin (`koinViewModel`), and DI modules live in `di/` packages.
- Dependency rule: `ui` -> `domain` <- `data` (no `ui` -> `data` or `domain` -> anything).

## Architecture Reference
![CleanArch.jpg](CleanArch.jpg)

## Testing Expectations
- Follow `agents/TDD-instructions.md` for behavior changes (strict TDD).
- Tests live in `src/test` and `src/androidTest`; data modules also use `testFixtures`.
- If Room schemas change, update the schema JSONs in `feature/**/schemas`.

## Verification
- Use Gradle tasks when needed, for example:
  - `./gradlew test`
  - `./gradlew :feature:auth:auth_ui:connectedDebugAndroidTest`
  - `./gradlew :app:connectedDebugAndroidTest`
