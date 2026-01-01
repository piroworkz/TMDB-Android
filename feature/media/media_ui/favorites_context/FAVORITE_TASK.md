---

description: "Task list for Media Favorites feature"
---

# Tasks: Media Favorites

**Input**: Design documents from `feature/media/media_ui/favorites_context/`
**Prerequisites**: `feature/media/media_ui/favorites_context/FAVORITE_PLAN.md` (required), `feature/media/media_ui/favorites_context/FAVORITE_SPECS.md`

**Tests**: Included and mandatory (TDD required by project constitution/spec).

**Organization**: Tasks are grouped by user story for independent delivery.

## Format: `[ID] [P?] [Story] Description`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Local-only favorites persistence and shared building blocks

- [ ] T001 Create favorites package structure per plan in `feature/media/media_domain/src/main/kotlin/.../favorites/` and `feature/media/media_framework/src/main/kotlin/.../favorites/`
- [ ] T002 Add Room schema for favorites in `feature/media/media_framework/src/main/kotlin/.../favorites/local/FavoriteEntity.kt`, `FavoritesDao.kt`, `FavoritesDatabase.kt`
- [ ] T003 Add mapping utilities in `feature/media/media_framework/src/main/kotlin/.../favorites/mapper/FavoriteMapper.kt`
- [ ] T004 Add repository contract in `feature/media/media_domain/src/main/kotlin/.../favorites/repository/FavoritesRepository.kt`
- [ ] T005 Add errors and types in `feature/media/media_domain/src/main/kotlin/.../favorites/errors/FavoriteError.kt` and `feature/media/media_domain/src/main/kotlin/.../favorites/types/MediaType.kt`
- [ ] T006 Wire repository implementation in `feature/media/media_framework/src/main/kotlin/.../favorites/repository/FavoritesRepositoryImpl.kt`
- [ ] T007 Add framework test fixture in `feature/media/media_framework/src/testFixtures/kotlin/.../favorites/FakeFavoritesRepository.kt`

---

## Phase 2: User Story 1 - Toggle favorites from the media list (Priority: P1) 🎯 MVP

**Goal**: Toggle favorite status from list and persist locally

**Independent Test**: Toggle favorite on a list item and verify UI state + local persistence updates

### Tests for User Story 1 ⚠️

- [ ] T010 [P] [US1] Unit test for toggle use case in `feature/media/media_domain/src/test/kotlin/.../favorites/ToggleFavoriteUseCaseTest.kt`
- [ ] T011 [P] [US1] Repository implementation test in `feature/media/media_framework/src/test/kotlin/.../favorites/FavoritesRepositoryImplTest.kt`
- [ ] T012 [P] [US1] ViewModel test for list toggle behavior in `feature/media/media_ui/src/test/kotlin/.../favorites/FavoritesViewModelTest.kt`

### Implementation for User Story 1

- [ ] T013 [P] [US1] Create use case `ToggleFavoriteUseCase.kt` in `feature/media/media_domain/src/main/kotlin/.../favorites/usecase/`
- [ ] T014 [US1] Implement repository methods for toggle in `feature/media/media_framework/src/main/kotlin/.../favorites/repository/FavoritesRepositoryImpl.kt`
- [ ] T015 [US1] Expose toggle in UI list item interaction (existing list screen) and route through `feature/media/media_ui/src/main/kotlin/.../favorites/viewmodel/FavoritesViewModel.kt`

**Checkpoint**: User Story 1 functional and testable independently

---

## Phase 3: User Story 2 - Access Favorites screen from the drawer (Priority: P2)

**Goal**: Navigate from drawer to Favorites screen

**Independent Test**: Open drawer, tap Favorites, navigate to Favorites screen

### Tests for User Story 2 ⚠️

- [ ] T020 [P] [US2] Navigation test in `feature/media/media_ui/src/androidTest/kotlin/.../favorites/FavoritesScreenTest.kt`

### Implementation for User Story 2

- [ ] T021 [US2] Add navigation route in `feature/media/media_ui/src/main/kotlin/.../favorites/navigation/FavoritesRoute.kt`
- [ ] T022 [US2] Add drawer item and navigation hook in existing drawer UI file (update the relevant file under `feature/media/media_ui/src/main/kotlin/.../`)

**Checkpoint**: Favorites screen reachable from drawer

---

## Phase 4: User Story 3 - View favorites by media type (Priority: P3)

**Goal**: Display TV and Movie favorites in separate lists with empty-state messaging

**Independent Test**: Show both lists when data exists; show empty message when none

### Tests for User Story 3 ⚠️

- [ ] T030 [P] [US3] Compose UI tests for lists and empty state in `feature/media/media_ui/src/androidTest/kotlin/.../favorites/FavoritesScreenTest.kt`
- [ ] T031 [P] [US3] Domain test for observe favorites use case in `feature/media/media_domain/src/test/kotlin/.../favorites/ObserveFavoritesUseCaseTest.kt`

### Implementation for User Story 3

- [ ] T032 [P] [US3] Create `ObserveFavoritesUseCase.kt` in `feature/media/media_domain/src/main/kotlin/.../favorites/usecase/`
- [ ] T033 [US3] Build UI state models in `feature/media/media_ui/src/main/kotlin/.../favorites/state/FavoritesUiState.kt`
- [ ] T034 [US3] Implement `FavoritesViewModel.kt` in `feature/media/media_ui/src/main/kotlin/.../favorites/viewmodel/`
- [ ] T035 [US3] Implement screen UI and empty state in `feature/media/media_ui/src/main/kotlin/.../favorites/ui/FavoritesScreen.kt` and `FavoritesEmptyState.kt`

**Checkpoint**: Favorites screen shows grouped lists and empty message when no favorites exist

---

## Dependencies & Execution Order

- Setup (Phase 1) blocks all user story work.
- US1 must be complete before US2/US3 integrate with shared favorites state.
- Tests must be written and fail before implementation for each user story.

