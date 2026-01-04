# Tasks: favorites

**Input**: Design documents from `favorites_context/`
**Prerequisites**: favorites_plan.md (required), favorites_specs.md (required for user stories)

**Tests**: Tests are REQUIRED for every user story and must follow TDD with Red → Green → Refactor (explicitly include all three phases in the task list).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID][P?][Story]Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Single project**: `src/`, `tests/` at repository root
- **Web app**: `backend/src/`, `frontend/src/`
- **Mobile**: `api/src/`, `ios/src/` or `android/src/`
- Paths shown below assume single project - adjust based on favorites_plan.md structure

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 [P] Inventory existing favorites-related UI entry points in `feature/media/media_ui/presenter/media/MediaCatalogViewModel.kt`, `feature/media/media_ui/view/media/MediaCatalogScreen.kt`, and `feature/media/media_ui/view/media/composables/MediaPager.kt`
- [ ] T002 [P] Inventory Favorites navigation wiring in `feature/media/media_ui/navigation/MediaNavigation.kt` and `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/Navigator.kt`
- [ ] T003 [P] Inventory session-close triggers in `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt` and `feature/auth/auth_ui/src/main/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginViewModel.kt`
- [ ] T004 [P] Confirm media table DAO entry points in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDao.kt`, `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDetailsDao.kt`, `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaVideosDao.kt`, and `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/RemoteKeysDao.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T005 Add `ClearFavorites` use case interface in `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ClearFavorites.kt`
- [ ] T006 Add DAO clear methods in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDao.kt`, `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDetailsDao.kt`, `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaVideosDao.kt`, and `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/RemoteKeysDao.kt`
- [ ] T007 Implement clear-all operation in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesLocalDataSource.kt` using DAO clears (depends on T006)
- [ ] T008 Wire ClearFavorites to the local data source in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/FavoritesDataModule.kt` (depends on T007)
- [ ] T009 [P] Add session-close integration test in `app/src/test/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainIntegrationTest.kt` (Red)
- [ ] T010 [P] Add session-expiration integration test in `feature/auth/auth_ui/src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginIntegrationTest.kt` (Red)
- [ ] T011 Update session-close flow in `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt` to invoke ClearFavorites (depends on T008)
- [ ] T012 Update session-expiration flow in `feature/auth/auth_ui/src/main/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginViewModel.kt` to invoke ClearFavorites (depends on T008)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Mark and unmark favorites on main lists (Priority: P1) 🎯 MVP

**Goal**: Allow users to favorite/unfavorite items in the main catalog lists with correct heart state.

**Independent Test**: Toggle a heart icon in a main list and confirm the UI reflects the favorite state.

### Tests for User Story 1 ⚠️

> **NOTE: Red = write tests first and ensure they FAIL before implementation. Green = implement the minimum to pass. Refactor = improve code with tests still passing.**

- [ ] T013 [P] [US1] Update tests for favorites toggling/observing in `feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesLocalDataSourceTest.kt` (Red)
- [ ] T014 [P] [US1] Update presenter tests for heart state changes in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModelTest.kt` (Red)
- [ ] T015 [P] [US1] Update integration tests for catalog favorites flow in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogIntegrationTest.kt` (Red)

### Implementation for User Story 1

- [ ] T016 [P] [US1] Add `isFavorite` and `mediaType` to `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt`
- [ ] T017 [P] [US1] Add favorites use case interfaces in `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ObserveFavoritesByType.kt` and `feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ToggleFavorite.kt`
- [ ] T018 [P] [US1] Update Room entity fields in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/media/RoomMedia.kt` to store favorites and media type
- [ ] T019 [US1] Update DAO APIs for favorites in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDao.kt` (depends on T018)
- [ ] T020 [US1] Update schema in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt` for favorites fields (depends on T018)
- [ ] T021 [US1] Implement favorites local source in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesLocalDataSource.kt` (depends on T019)
- [ ] T022 [US1] Wire DI in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/FavoritesDataModule.kt` for favorites use cases (depends on T021)
- [ ] T023 [US1] Preserve favorites/media type mapping in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/paging/MediaCatalogRemoteMediator.kt` (depends on T018)
- [ ] T024 [US1] Update favorites state + toggle handlers in `feature/media/media_ui/presenter/media/MediaCatalogViewModel.kt` (depends on T017, T021)
- [ ] T025 [US1] Render heart state + toggle in `feature/media/media_ui/view/media/MediaCatalogScreen.kt` (depends on T024)
- [ ] T026 [US1] Update catalog composables for favorite state in `feature/media/media_ui/view/media/composables/MediaPager.kt`, `feature/media/media_ui/view/media/composables/MediaTitleView.kt`, and `feature/media/media_ui/view/media/composables/ReelTitleView.kt` (depends on T025)

### Refactor for User Story 1 (MANDATORY)

- [ ] T027 [US1] Refactor favorites mapping and UI state in `feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/paging/MediaCatalogRemoteMediator.kt` and `feature/media/media_ui/presenter/media/MediaCatalogViewModel.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - View favorites by media type (Priority: P2)

**Goal**: Provide a Favorites screen with Movies/TV lists and correct default selection.

**Independent Test**: Open Favorites with mixed favorites and verify default tab and ordering.

### Tests for User Story 2 ⚠️

> **NOTE: Red = write tests first and ensure they FAIL before implementation. Green = implement the minimum to pass. Refactor = improve code with tests still passing.**

- [ ] T028 [P] [US2] Cover default list selection in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt` (Red)
- [ ] T029 [P] [US2] Cover catalog ordering integration in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelIntegrationTest.kt` (Red)
- [ ] T030 [P] [US2] Add default-tab UI checks in `feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt` (Red)

### Implementation for User Story 2

- [ ] T031 [US2] Build favorites lists + default selection in `feature/media/media_ui/presenter/favorites/FavoritesViewModel.kt` (depends on T028)
- [ ] T032 [US2] Update selection state in `feature/media/media_ui/presenter/favorites/FavoritesUiState.kt` (depends on T031)
- [ ] T033 [US2] Render favorites tabs and list selection in `feature/media/media_ui/view/favorites/FavoritesScreen.kt` (depends on T031)
- [ ] T034 [US2] Render ordered lists in `feature/media/media_ui/view/favorites/composables/FavoritesList.kt` (depends on T033)
- [ ] T035 [US2] Add Favorites destination in `feature/media/media_ui/navigation/MediaNavigation.kt` (depends on T033)
- [ ] T036 [US2] Add drawer entry/wiring in `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/model/DrawerItem.kt`, `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerView.kt`, `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/DrawerScaffoldView.kt`, and `app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/Navigator.kt` (depends on T035)

### Refactor for User Story 2 (MANDATORY)

- [ ] T037 [US2] Refactor selection/ordering helpers in `feature/media/media_ui/presenter/favorites/FavoritesViewModel.kt` with tests green

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Manage favorites on the Favorites screen (Priority: P3)

**Goal**: Allow removals from Favorites and show empty state when no items remain.

**Independent Test**: Remove the last favorite and see the empty message immediately.

### Tests for User Story 3 ⚠️

> **NOTE: Red = write tests first and ensure they FAIL before implementation. Green = implement the minimum to pass. Refactor = improve code with tests still passing.**

- [ ] T038 [P] [US3] Cover removals + empty state in `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt` (Red)
- [ ] T039 [P] [US3] Cover empty message + removal UI in `feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt` (Red)

### Implementation for User Story 3

- [ ] T040 [US3] Wire remove actions in `feature/media/media_ui/presenter/favorites/FavoritesViewModel.kt` (depends on T038)
- [ ] T041 [US3] Render empty message in `feature/media/media_ui/view/favorites/FavoritesScreen.kt` (depends on T040)
- [ ] T042 [US3] Wire item removal in `feature/media/media_ui/view/favorites/composables/FavoritesList.kt` (depends on T040)

### Refactor for User Story 3 (MANDATORY)

- [ ] T043 [US3] Refactor favorites UI state derivation in `feature/media/media_ui/presenter/favorites/FavoritesViewModel.kt` and `feature/media/media_ui/view/favorites/FavoritesScreen.kt`

**Checkpoint**: All user stories should now be independently functional

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T044 [P] Review favorites empty-state copy and styling in `feature/media/media_ui/view/favorites/FavoritesScreen.kt`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Update tests for favorites toggling/observing in feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesLocalDataSourceTest.kt"
Task: "Update presenter tests for heart state changes in feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModelTest.kt"
Task: "Update integration tests for catalog favorites flow in feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogIntegrationTest.kt"

# Launch all models for User Story 1 together:
Task: "Add isFavorite and mediaType to feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt"
Task: "Add favorites use case interfaces in feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ObserveFavoritesByType.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
