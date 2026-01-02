---

description: "Task list for Favorites feature implementation"
---

# Tasks: Favorites

**Input**: Design documents from `feature/media/favorites_context/`  
**Prerequisites**: `feature/media/favorites_context/favorites_plan.md`, `feature/media/favorites_context/favorites-spec.md`

**Tests**: TDD required. Tests define behavior for data sources, ViewModels, and UI flows. Avoid tests for pure data classes or interfaces; use tests to drive contracts and interactions. Write tests first and confirm they fail before implementation.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: User Story 1 - Save and Unsave Favorites (Priority: P1)

**Goal**: Users can mark and unmark media items as favorites.

**Independent Test**: Mark an item as favorite and confirm it is saved and can be unmarked.

**Note**: For new ViewModels, inject a CoroutineScope (or dispatcher + scope) instead of using `viewModelScope`.

### Tests for User Story 1 (TDD first)

- [ ] T101 [P] [US1] Unit test for local data source toggle behavior in `feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/LocalFavoritesDataSourceTest.kt`
- [ ] T102 [P] [US1] ViewModel test for toggle flow in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt`

### Implementation for User Story 1

- [ ] T104 [US1] Create `FavoriteItem` in `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/FavoriteItem.kt`
- [ ] T105 [US1] Define `ToggleFavorite` use case interface in `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ToggleFavorite.kt`
- [ ] T106 [US1] Add Room entity `RoomFavorite` in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/favorites/RoomFavorite.kt`
- [ ] T107 [US1] Add DAO `FavoritesDao` in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDao.kt`
- [ ] T108 [US1] Update `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt` to include Favorites table and migration
- [ ] T109 [US1] Add DAO spy for tests in `feature/media/media_framework/src/testFixtures/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDaoSpy.kt`
- [ ] T110 [US1] Implement local data source and mapping in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/LocalFavoritesDataSource.kt`
- [ ] T111 [US1] Bind `LocalFavoritesDataSource` to `ToggleFavorite` in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/MediaDataModule.kt` and `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/RoomMediaModule.kt`
- [ ] T112 [US1] Add toggle UI hook in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt`
- [ ] T113 [US1] Surface favorite state in catalog UI in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCatalogScreen.kt`

---

## Phase 2: User Story 2 - Return to Favorites (Priority: P2)

**Goal**: Users can access Favorites from the navigation drawer and see their saved items.

**Independent Test**: Favorite items appear in Favorites screen after opening it from drawer.

### Tests for User Story 2 (TDD first)

- [ ] T201 [P] [US2] Unit test for local data source observe behavior in `feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/LocalFavoritesDataSourceTest.kt`
- [ ] T202 [P] [US2] ViewModel test for list state in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt`
- [ ] T203 [P] [US2] Compose UI test for Favorites screen in `feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt`

### Implementation for User Story 2

- [ ] T204 [US2] Define `ObserveFavorites` use case interface in `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ObserveFavorites.kt`
- [ ] T205 [US2] Bind `LocalFavoritesDataSource` to `ObserveFavorites` in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/MediaDataModule.kt` and `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/RoomMediaModule.kt`
- [ ] T206 [US2] Implement `FavoritesViewModel` state for list in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt`
- [ ] T207 [US2] Create `FavoritesScreen` in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt`
- [ ] T208 [US2] Add Favorites destination in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/MediaNavigation.kt`
- [ ] T209 [US2] Add Favorites entry in drawer in `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerView.kt`

---

## Phase 3: User Story 3 - Remove from Favorites List (Priority: P3)

**Goal**: Users can remove items directly in the Favorites screen.

**Independent Test**: Removing an item from Favorites screen updates the list immediately.

### Tests for User Story 3 (TDD first)

- [ ] T301 [P] [US3] Unit test for remove flow in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt`
- [ ] T302 [P] [US3] Compose UI test for remove interaction in `feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt`

### Implementation for User Story 3

- [ ] T303 [US3] Add remove action to `FavoritesScreen` in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt`
- [ ] T304 [US3] Wire remove event in `FavoritesViewModel` in `feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt`

---

## Phase 4: Session End Cleanup (Cross-Cutting)

**Goal**: Favorites are cleared when session ends.

### Tests (TDD first)

- [ ] T401 [P] [US2] Unit test for local data source clear behavior in `feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/LocalFavoritesDataSourceTest.kt`
- [ ] T402 [P] [US2] Integration test for session end flow in `app/src/test/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainIntegrationTest.kt`

### Implementation

- [ ] T403 [US2] Define `ClearFavoritesOnSessionEnd` use case interface in `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ClearFavoritesOnSessionEnd.kt`
- [ ] T404 [US2] Bind `LocalFavoritesDataSource` to `ClearFavoritesOnSessionEnd` in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/MediaDataModule.kt` and `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/RoomMediaModule.kt`
- [ ] T405 [US2] Invoke cleanup on session end in `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt`

---

## Dependencies & Execution Order

- **US1 (Phase 1)**: First slice; delivers toggle persistence and UI hook.
- **US2 (Phase 2)**: Builds on shared data source and adds Favorites screen + drawer entry.
- **US3 (Phase 3)**: Adds remove-from-list interactions; independent of session-end cleanup.
- **Session End Cleanup (Phase 4)**: Depends on app session end hook; uses shared data source.
