# Implementation Plan: favorites

**Branch**: `feature/favorites` | **Date**: 2026-01-18 | **Spec**: favorites_context/favorites_specs.md
**Input**: Feature specification from `favorites_context/favorites_specs.md`

**Note**: This template is filled in by the plan generation command.
**TDD Requirement**: Plan must assume Red → Green → Refactor for each user story.

## Summary

Implement session-scoped favorites for Movies/TV Shows with immediate UI feedback: extend local media storage with an `isFavorite` flag (no new tables), add domain contracts/use cases for toggling and querying favorites, wire data implementations and UI updates in media grids plus a dedicated Favorites screen with type filters and empty/loading/error states. Favorites are cleared when the session ends by observing session state in `SplashViewModel` and triggering a clear-favorites use case. All UI resources (strings/icons) live in `feature/core/core_ui` per shared-resources policy. Execution follows `context/WORKFLOW_TDD_EDGE_FIRST.md` and repo skills/few-shots.

**Planned phases (TDD edge-first)**
- Phase 1 (TDD Design + Test setup): define test cases starting at the repository edge (unit tests with mocks) using `context/skills/skill-android-tdd` + `context/skills/skill-unit-tests` few-shots. No production changes before RED tests exist.
- Phase 2 (Implementation with strict TDD): Red → Green → Refactor per story, following the edge-first flow (repository → local data source → move to final package hierarchy → domain contracts/modeling → UI state/Compose unit tests).
- Phase 3 (Integration final phase): add JVM integration tests with spies across layers using `context/skills/skill-integration-tests` few-shots.
- Phase 4 (Stabilization): verify session-end clearing for guest/logout, empty/error states, update docs/tasks.

**Execution order (per user story)**  
1) Repository edge: unit tests with mocks define behavior for favorite toggling and queries (remote interface first, then local DAO interface).  
2) Local data source: add/update local behavior after remote behavior is covered; keep tests unit-level with mocks.  
3) Move production code into final architecture packages/modules after behavior is validated.  
4) Domain: add/update contracts (interfaces/entities). No tests for pure interfaces/entities.  
5) UI: ViewModel/state unit tests and Compose unit tests drive filters, empty/error states, and rendering. Instrumented tests only if explicitly requested.  
6) Integration: JVM integration tests with spies to validate cross-layer behavior (not E2E/UI).

## Technical Context

**Language/Version**: Kotlin 2.3.0 (JVM) / Java 17
**Primary Dependencies**: Jetpack Compose, Koin, Room, Paging, Retrofit/OkHttp, Coroutines
**Storage**: Room database in `feature/media/media_data` (reuse existing RoomMedia table)
**Testing**: JUnit 4, MockK, Coroutines Test, Turbine (TDD required: Red → Green → Refactor)
**Target Platform**: Android (minSdk 28, targetSdk 35, compileSdk 36)
**Project Type**: mobile (Android multi-module)
**Performance Goals**: Smooth scrolling in media grids; favorite toggles update immediately
**Constraints**: No new tables; extend `RoomMedia` with `isFavorite: Boolean = false`; no migration work; session-scoped favorites; UI resources only in `feature/core/core_ui`
**Scale/Scope**: One feature spanning media domain/data/UI, auth UI session observation, and app navigation drawer entry

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Branching: use `feature/favorites` (feature branch pattern), never commit to `master`.
- Scope: only implement behavior in favorites spec; no unrelated refactors.
- Dependencies: no new dependencies without explicit approval.
- TDD: Red → Green → Refactor for behavioral changes; tests required for logic in domain/data/UI.
- Architecture: maintain Clean Architecture boundaries (domain independent of data/UI; UI depends on domain only).
- Shared resources: all new UI assets/strings in `feature/core/core_ui` only; no feature `res/` dirs.
- Working tree: documentation-only task permitted while dirty; plan changes limited to markdown.

## Project Structure

### Documentation (this feature)

```text
favorites_context/
├── favorites_specs.md
├── favorites_plan.md
└── favorites_tasks.md
```

### Source Code (repository root)

```text
app/
└── src/main/kotlin/com/davidluna/tmdb/app/di/  # drawer/nav wiring

feature/auth/auth_ui/
└── src/main/kotlin/com/davidluna/tmdb/auth_ui/presenter/splash/  # session observation

feature/media/media_domain/
└── src/main/kotlin/com/davidluna/tmdb/media_domain/  # entities/use cases/contracts

feature/media/media_data/
└── src/main/kotlin/com/davidluna/tmdb/media_data/  # RoomMedia, dao, repositories

feature/media/media_ui/
└── src/main/kotlin/com/davidluna/tmdb/media_ui/  # media cards, favorites screen, nav

feature/core/core_ui/
└── src/main/res/  # shared strings/drawables for favorites UI
```

**Structure Decision**: Android multi-module Clean Architecture with vertical feature slices; favorites spans `media_*` layers, session observation in `auth_ui`, navigation entry in `app`, and shared UI resources in `core_ui`.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

None.
