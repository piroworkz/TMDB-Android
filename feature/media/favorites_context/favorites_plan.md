# Implementation Plan: Favorites

**Branch**: `fav-001-favorites-toggle` | **Date**: 2026-01-02 | **Spec**: `feature/media/favorites_context/favorites-spec.md`  
**Input**: Feature specification from `feature/media/favorites_context/favorites-spec.md`

## Summary

Enable all user types to mark/unmark favorites, view them via a Favorites screen reachable from the navigation drawer, and clear favorites when the session ends. Implement as a clean-architecture feature slice (domain use cases + local persistence in framework + Compose UI and navigation), with tests written first for each layer. No repository is needed because a single local data source can implement the use case interfaces.

## Technical Context

**Language/Version**: Kotlin 2.2.21  
**Primary Dependencies**: Jetpack Compose (Material 3), Room, Hilt, Arrow, Coroutines/Flow  
**Storage**: Room (local-only)  
**Testing**: JUnit4, MockK, Turbine, Compose UI Test, Hilt Android Testing  
**Target Platform**: Android  
**Project Type**: Mobile (multi-module, clean architecture)  
**Performance Goals**: Smooth 60 fps scrolling; favorites list loads immediately from local storage  
**Constraints**: Local-only persistence; favorites cleared on session end; TDD-first (tests define interfaces, models, and contracts); no repository for this feature  
**Scale/Scope**: Single feature slice across media_domain, media_framework, media_ui, and app navigation

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Clean Architecture Boundaries: PASS (domain remains Android-free; framework implements data)  
- Modular Feature Structure: PASS (use existing media_* modules)  
- Test-First (TDD): PASS (tests planned before implementation)  
- Type-Safe Errors: PASS (Arrow Either in domain use cases)  
- Convention-Driven Build: PASS (use version catalog + convention plugins only)

## Project Structure

### Documentation (this feature)

```text
feature/media/favorites_context/
├── favorites-spec.md     # Feature specification
└── favorites_plan.md     # This plan
```

### Source Code (project root)

```text
feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/
├── entities/
│   └── FavoriteItem.kt
└── usecases/
    ├── ToggleFavorite.kt
    ├── ObserveFavorites.kt
    └── ClearFavoritesOnSessionEnd.kt

feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/
├── data/local/database/
│   ├── entities/favorites/RoomFavorite.kt
│   ├── dao/FavoritesDao.kt
│   └── MediaDatabase.kt             # add Favorites table + migration
└── data/local/storage/
    └── LocalFavoritesDataSource.kt  # implements use case interfaces

feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/
├── presenter/favorites/FavoritesViewModel.kt
├── view/favorites/FavoritesScreen.kt
└── navigation/MediaNavigation.kt    # add Favorites destination

app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/
└── NavDrawerView.kt                 # add Favorites entry point

feature/media/media_domain/src/test/
feature/media/media_framework/src/test/
feature/media/media_ui/src/test/
feature/media/media_ui/src/androidTest/
```

**Structure Decision**: Extend existing `media_domain`, `media_framework`, and `media_ui` modules for favorites data/use cases, persistence, and UI. Entry point is added to the app navigation drawer.

## Naming & Test Strategy (from PROJECT_CONTEXT)

- **Naming**: Use verb-first use case names (e.g., `ToggleFavorite`, `ObserveFavorites`), repositories without `Impl`, data sources `{Source}{Feature}DataSource`, ViewModels suffixed with `ViewModel`, and one public type per file with matching filename.
- **TDD**: Tests define interfaces, models, and contracts. Write tests first per layer (domain/unit, framework/unit, UI/viewmodel/unit, Compose UI/androidTest). Use MockK for unit tests, spies for integration tests, and Turbine for Flow assertions.

## Complexity Tracking

No constitution violations expected.
