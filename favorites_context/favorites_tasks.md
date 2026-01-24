---

description: "Task list for favorites feature implementation"

---

# Tasks: favorites

**Input**: Design documents from `/favorites_context/`  
**Prerequisites**: `favorites_plan.md` (required), `favorites_specs.md` (required for user stories)

## Testing Policy (IMPORTANT)

**Tests**: Tests are **REQUIRED by default** for every user story that changes data/UI behavior.

**TDD**: Optional for this feature. If used, follow **Red → Green → Refactor**.

**Instrumented/UI tests**: Opt-in only; run only if explicitly requested or if UI/navigation changes justify the risk per AGENTS.md.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Align shared domain/data foundations used by all stories.

- [x] T001 Define favorites domain contracts and entities
  Description: Add `isFavorite` to `Media` and introduce use cases for observe/toggle/clear favorites.
  This is the shared contract required by UI and data layers.
  Paths:
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ObserveFavoriteMedia.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ToggleFavorite.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ClearFavorites.kt

- [x] T002 Implement favorites persistence layer in Room
  Description: Extend Room entities/mappers and add DAO queries for favorites by media type.
  Ensures local storage and paging sources reflect `isFavorite`.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/entities/media/RoomMedia.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/dao/FavoritesDao.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/MediaDatabase.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/mappers/RoomMediaMapper.kt
  Depends on: T001

- [x] T003 Implement FavoritesRepository and DI wiring
  Description: Provide observe/toggle/clear behavior and wire the repository + use cases in Koin.
  Keeps data flow consistent with existing media repositories.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/repository/FavoritesRepository.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/di/MediaDataModule.kt
  Depends on: T002

- [ ] T004 Integrate session end with favorites clearing
  Description: On session close/expiration, invoke `ClearFavorites` so lists reset immediately.
  Ensures favorites are session-scoped for guests and registered users.
  Paths:
    - feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/usecases/CloseSession.kt
  Depends on: T003

---

## Phase 2: User Story 1 - Toggle favorites from media cards (Priority: P1) 🎯 MVP

**Goal**: Users can favorite/unfavorite from movie/TV grids with immediate UI feedback.

**Independent Test**: Tap the heart on a media card and verify both the card state and favorites list update.

### Tests for User Story 1 (REQUIRED) ⚠️

- [ ] T005 [US1] Red: Media grid shows favorite state and toggles heart
  Description: Add failing tests for media card UI state and toggle event dispatch from grids.
  Cover happy path, edge case (toggle twice), and error path (toggle failure).
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/MediaGridViewModelTest.kt
  Depends on: T004

- [ ] T006 [US1] Green: Implement minimum toggle flow
  Description: Wire UI to call `ToggleFavorite` and bind `Media.isFavorite` to the heart state.
  Ensure immediate UI updates and non-blocking error feedback.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/MediaGridViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCard.kt
  Depends on: T005

- [ ] T007 [US1] Refactor: stabilize state + error handling
  Description: Clean up view model state management without changing behavior.
  Keep tests green and improve readability.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/MediaGridViewModel.kt
  Depends on: T006

- [ ] T007a [US1] Optional instrumented UI tests (opt-in)
  Description: Add/extend Compose UI tests for media grid cards if explicitly requested or justified.
  Paths:
    - feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaGridCardTest.kt
  Depends on: T007

---

## Phase 3: User Story 2 - View favorites by media type (Priority: P2)

**Goal**: Users open Favorites screen from the drawer and filter by Movies/TV Shows.

**Independent Test**: Open Favorites, switch filters, and verify the correct list or empty state.

### Tests for User Story 2 (REQUIRED) ⚠️

- [ ] T008 [US2] Red: Favorites screen states + filter switching
  Description: Add failing tests for filter selection, empty/loading/error states, and list rendering.
  Include a test for navigation entry from the drawer.
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt
  Depends on: T004

- [ ] T009 [US2] Green: Implement Favorites screen + navigation
  Description: Build Favorites screen UI, filter controls, and hook into drawer navigation.
  Wire lists from `ObserveFavoriteMedia` by selected media type.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/navigation/DrawerNavGraph.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/navigation/DrawerItems.kt
  Depends on: T008

- [ ] T010 [US2] Refactor: polish UI state mapping
  Description: Simplify filter state + list mapping without changing behavior.
  Ensure error/empty/loading states remain correct.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
  Depends on: T009

- [ ] T010a [US2] Optional instrumented UI tests (opt-in)
  Description: Add Compose UI + navigation tests if explicitly requested or justified.
  Paths:
    - feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt
    - app/src/androidTest/kotlin/com/davidluna/tmdb/app/navigation/DrawerFavoritesNavigationTest.kt
  Depends on: T010

---

## Phase 4: User Story 3 - Remove favorites from the Favorites screen (Priority: P3)

**Goal**: Users can remove favorites directly from the Favorites list.

**Independent Test**: Tap the heart on a favorite item in Favorites; it disappears and empty state updates if needed.

### Tests for User Story 3 (REQUIRED) ⚠️

- [ ] T011 [US3] Red: Remove favorite from Favorites list
  Description: Add failing tests for removing items and verifying list/empty state updates.
  Include error path where toggle fails and UI remains accurate.
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt
  Depends on: T009

- [ ] T012 [US3] Green: Wire toggle in Favorites cards
  Description: Connect Favorites card heart to `ToggleFavorite` and update list immediately.
  Reuse card UI patterns from media grids for consistency.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesCard.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
  Depends on: T011

- [ ] T013 [US3] Refactor: consolidate favorites UI components
  Description: Reduce duplication between media cards and favorites cards while preserving behavior.
  Keep accessibility labels correct.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCard.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesCard.kt
  Depends on: T012

- [ ] T013a [US3] Optional instrumented UI tests (opt-in)
  Description: Add/extend Favorites screen Compose UI tests if explicitly requested or justified.
  Paths:
    - feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt
  Depends on: T013

---

## Phase 5: Validation & Documentation

- [ ] T014 Verify unit tests and report results
  Description: Run unit tests for affected modules and record outcomes in the final report.
  Skip instrumented tests unless explicitly requested.
  Paths:
    - Tmdb2024.gradle.kts
  Depends on: T013
