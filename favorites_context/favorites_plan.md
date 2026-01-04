# Implementation Plan: favorites

**Branch**: `feature/favorites` | **Date**: 2026-01-03 | **Spec**: favorites_context/feature_specs.md
**Input**: Feature specification from `favorites_context/feature_specs.md`

**Note**: This template is filled in by the plan generation command.
**TDD Requirement**: Plan must assume Red → Green → Refactor for each user story.

## Summary

Implement a session-scoped Favorites capability inside the existing `feature/media` modules by extending existing media models: add `isFavorite` and `mediaType` to domain `Media`, and `isFavorite` to Room media entities to persist locally. Ensure mapping updates (including `MediaCatalogRemoteMediator`) populate `mediaType` and preserve favorites. Expose domain use case interfaces to toggle/observe favorites, implemented directly by a local data source (single source), and build Favorites lists by filtering the current catalog flow so ordering always matches the latest main list. Update the main catalog UI (`MediaCatalogScreen`, `MediaCatalogViewModel`, and media composables) to reflect favorite state and toggle actions. Add a new drawer entry in `app` to navigate into the Favorites destination hosted by the `media_ui` nav graph, and clear favorites by wiping media tables via DAO methods exposed through `FavoritesLocalDataSource` and invoked from `MainViewModel`/`LoginViewModel` on session end.

## Technical Context

**Language/Version**: Kotlin 2.2.21  
**Primary Dependencies**: Jetpack Compose (Material 3), Hilt, Arrow, Room, Retrofit  
**Storage**: Room (local persistence, session-scoped favorites)  
**Testing**: JUnit4, MockK, Turbine, Compose UI Test (TDD required: Red → Green → Refactor)  
**Target Platform**: Android (API per build-logic)  
**Project Type**: Mobile (multi-module clean architecture)  
**Performance Goals**: Maintain 60 fps for list interactions and scrolling  
**Constraints**: Clean Architecture boundaries; session-scoped data cleared on Close Session/guest expiration  
**Scale/Scope**: Single Android app with feature modules under `feature/media`

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Clean Architecture Boundaries: PASS
- Modular Feature Structure: PASS
- Test-First (Red → Green → Refactor): PASS
- Type-Safe Errors (Arrow Either): PASS
- Convention-Driven Build (version catalog + convention plugins): PASS
- Kotlin-only + Coroutines/Flow: PASS
- Compose + state hoisting + naming: PASS
- Hilt DI + serialization for navigation: PASS
- One public type per file: PASS

## Project Structure

### Documentation (this feature)

```text
favorites_context/
├── feature_specs.md
├── favorites_plan.md
└── favorites_tasks.md
```

### Source Code (repository root)

```text
feature/media/
├── media_domain/
│   ├── src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_domain/entities/MediaType.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/
│   │   ├── ObserveFavoritesByType.kt
│   │   ├── ToggleFavorite.kt
│   │   └── ClearFavorites.kt
├── media_framework/
│   ├── src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/media/RoomMedia.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/
│   │   ├── MediaDao.kt
│   │   ├── MediaDetailsDao.kt
│   │   ├── MediaVideosDao.kt
│   │   └── RemoteKeysDao.kt
│   │   └── [other DAOs as needed for table clears]
│   ├── src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/
│   │   └── FavoritesLocalDataSource.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_framework/di/
│   │   └── FavoritesDataModule.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_framework/data/paging/MediaCatalogRemoteMediator.kt
│   └── src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/
│       └── FavoritesLocalDataSourceTest.kt
├── media_ui/
│   ├── src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/
│   │   └── MediaNavigation.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/
│   │   ├── MediaCatalogScreen.kt
│   │   └── composables/
│   │       ├── CarouselImageView.kt
│   │       ├── FilmMaskImageView.kt
│   │       ├── MediaPager.kt
│   │       ├── MediaTitleView.kt
│   │       ├── ReelTitleView.kt
│   │       └── rememberItemWidth.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/
│   │   ├── FavoritesScreen.kt
│   │   └── composables/FavoritesList.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/media/
│   │   └── MediaCatalogViewModel.kt
│   ├── src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/
│   │   ├── FavoritesViewModel.kt
│   │   └── FavoritesUiState.kt
│   ├── src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/
│   │   ├── FavoritesViewModelTest.kt
│   │   └── FavoritesViewModelIntegrationTest.kt
│   └── src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/
│       └── FavoritesScreenTest.kt
app/
├── src/main/kotlin/com/davidluna/tmdb/app/main_ui/model/DrawerItem.kt
├── src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt
├── src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerView.kt
├── src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/DrawerScaffoldView.kt
└── src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/Navigator.kt
└── src/test/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainIntegrationTest.kt
feature/auth/auth_ui/
└── src/main/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginViewModel.kt
└── src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginIntegrationTest.kt
```

**Structure Decision**: Extend existing `feature/media` clean-architecture modules with favorites-specific domain models, Room persistence, and UI screens. Navigation to Favorites is added to the app drawer and wired into the existing `media_ui` nav graph, keeping feature behavior rooted under `feature/media` while acknowledging required app-level entry points.

### Naming & Test Strategy

- Names follow project conventions: use case verbs (e.g., `ToggleFavorite`), repositories without `Impl`, data sources with source prefixes.
- Tests define interfaces and contracts first; each layer runs Red → Green → Refactor independently (domain → framework → UI).
- Compose UI tests live in `src/androidTest`, use semantic checks for icon states and empty message.

### TDD Execution (per User Story)

- User Story 1 (Toggle favorite in main lists)
  - Red: framework tests for `FavoritesLocalDataSource` contract and error mapping.
  - Green: `FavoritesLocalDataSource` implements use case interfaces and Room storage updates.
  - Refactor: remove duplication and tighten mapping; keep contracts unchanged.
  - UI Red/Green/Refactor: update `MediaCatalogViewModel`, `MediaCatalogScreen`, and media composables to reflect favorites flow and toggle state.

- User Story 2 (View favorites by media type)
  - Red: `FavoritesViewModelTest` covers default list selection logic by media type and list availability.
  - Green: ViewModel uses favorites + current catalog ordering to build UI state.
  - Refactor: extract ordering/selection logic to pure functions for testability.
  - UI Red/Green/Refactor: Favorites screen, bottom nav switching, and default tab selection.

- User Story 3 (Manage favorites on Favorites screen + empty state)
  - Red: UI tests for empty state message and item removal; ViewModel tests for list updates.
  - Green: wire `ToggleFavorite` and `ClearFavorites` to UI; propagate empty state.
  - Refactor: simplify state derivation and composable structure.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
