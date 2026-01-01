# Implementation Plan: Media Favorites

**Branch**: `[favorites-media]` | **Date**: 2025-02-14 | **Spec**: `feature/media/media_ui/favorites_context/FAVORITE_SPECS.md`
**Input**: Feature specification from `feature/media/media_ui/favorites_context/FAVORITE_SPECS.md`

**Note**: This plan is written to decide architecture and file structure before coding.

## Summary

Add local-only favorites for media items with list-level toggle, a new Favorites destination in the drawer, and a dedicated Favorites screen showing separate lists for TV and movies. Implement using Clean Architecture across media modules: domain use cases + repository contracts, framework local persistence, and UI state via ViewModel/StateFlow.

## Technical Context

**Language/Version**: Kotlin 2.2.21  
**Primary Dependencies**: Jetpack Compose (Material 3), Hilt, Arrow, Navigation Compose  
**Storage**: Local-only persistence (Room or DataStore; align with existing media framework patterns)  
**Testing**: JUnit 4, MockK, Turbine, Coroutines Test; Compose UI tests in `src/androidTest`  
**Target Platform**: Android (min/target API per build-logic)  
**Project Type**: Mobile (multi-module clean architecture)  
**Performance Goals**: Smooth UI updates, avoid jank (60 fps target)  
**Constraints**: Offline-capable; no remote sync for favorites  
**Scale/Scope**: Single new screen + list updates within existing media flows

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Clean Architecture boundaries preserved (UI -> Domain <- Framework).
- Module structure follows `{feature}_{layer}` and registrations remain consistent.
- TDD enforced for use cases and ViewModels; Compose UI tests for screens.
- Arrow `Either` used for domain errors.
- Version catalog and convention plugins used for dependencies.

## Project Structure

### Documentation (this feature)

```text
feature/media/media_ui/favorites_context/
├── FAVORITE_SPECS.md     # Feature spec
└── FAVORITE_PLAN.md      # This file
```

### Source Code (repository root)

```text
feature/
└── media/
    ├── media_domain/
    │   ├── src/main/kotlin/.../favorites/
    │   │   ├── entities/
    │   │   │   └── FavoriteItem.kt
    │   │   ├── types/
    │   │   │   └── MediaType.kt
    │   │   ├── errors/
    │   │   │   └── FavoriteError.kt
    │   │   ├── repository/
    │   │   │   └── FavoritesRepository.kt
    │   │   └── usecase/
    │   │       ├── ObserveFavoritesUseCase.kt
    │   │       └── ToggleFavoriteUseCase.kt
    ├── media_framework/
    │   ├── src/main/kotlin/.../favorites/
    │   │   ├── local/
    │   │   │   ├── FavoriteEntity.kt
    │   │   │   ├── FavoritesDao.kt
    │   │   │   └── FavoritesDatabase.kt
    │   │   ├── mapper/
    │   │   │   └── FavoriteMapper.kt
    │   │   └── repository/
    │   │       └── FavoritesRepositoryImpl.kt
    │   └── src/testFixtures/kotlin/.../favorites/
    │       └── FakeFavoritesRepository.kt
    └── media_ui/
        ├── src/main/kotlin/.../favorites/
        │   ├── navigation/
        │   │   └── FavoritesRoute.kt
        │   ├── ui/
        │   │   ├── FavoritesScreen.kt
        │   │   └── FavoritesEmptyState.kt
        │   ├── state/
        │   │   └── FavoritesUiState.kt
        │   └── viewmodel/
        │       └── FavoritesViewModel.kt
        └── src/androidTest/kotlin/.../favorites/
            └── FavoritesScreenTest.kt
```

**Structure Decision**: Use existing media feature modules. Domain defines entities, errors, and use cases; framework provides local-only persistence and repository implementation; UI exposes navigation and screens. Tests follow existing module patterns, with UI tests in `androidTest` and repository fakes in framework test fixtures.

## Complexity Tracking

No constitution violations detected; no added complexity beyond standard module layering.
