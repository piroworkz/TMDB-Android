---

description: "Task list template for feature implementation"
---

# Tasks: Favorites

**Input**: Design documents from `/favorites/`
**Prerequisites**: favorites_plan.md (required), favorites_specs.md (required for user stories)

**Tests**: Tests are REQUIRED for every user story and must follow TDD with Red → Green → Refactor (explicitly include all three phases in the task list).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Task format (MANDATORY)

Each task is a small, actionable unit of work.

### Format (block)

- [ ] <ID> <P?> <Story?> <Title>

  Description: <what/why in 2–4 lines; keep indentation>
    <optional extra description lines>

  Paths:
    - <path/to/file1>
    - <path/to/file2>

  Depends on: <optional, e.g., T006>

Where:
- <ID> is required (e.g., T001).
- <P?> is optional. Use "[P]" only when tasks can truly run in parallel (different files, no ordering dependency).
- <Story?> is optional. Use "[US1]", "[US2]", etc. Setup/Foundational tasks typically omit it.
- <Title> must be human-readable and MUST NOT include file paths, packages, or long technical details.
- File paths MUST appear ONLY under "Paths:".
- "Description" may span 2–4 lines as long as indentation is preserved.
- "Depends on" is optional but recommended when ordering matters.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Add shared Favorites strings and accessibility labels

  Description: Define drawer, tabs, empty state, error copy, and favorite toggle labels in core_ui.
    Keep all UI strings centralized to comply with the shared resource policy.

  Paths:
    - feature/core/core_ui/src/main/res/values/strings.xml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 Extend media domain entities for favorites and media type

  Description: Add mediaType to Catalog and isFavorite/mediaType to Media in the domain layer.
    Remove or update UI extensions that duplicate the domain mediaType logic.

  Paths:
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Catalog.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/utils/getMediaType.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/AppBottomBar.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/model/DrawerItem.kt

- [x] T003 Add favorites use case contracts in media_domain

  Description: Define ToggleFavorite, ObserveFavorites, and ClearAllFavorites as named-method interfaces.
    Keep the interfaces framework-agnostic and aligned with Arrow Either error handling.

  Paths:
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ToggleFavorite.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ObserveFavorites.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ClearAllFavorites.kt

- [ ] T004 Update RoomMedia schema for favorites state and media type

  Description: Add mediaType and isFavorite columns, bump the MediaDatabase version, and update test fakes.
    Keep schema changes scoped to the existing RoomMedia table as required by the plan.

  Paths:
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/media/RoomMedia.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt
    - feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/fakes/MediaFakes.kt

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Quick favorite toggles (Priority: P1) 🎯 MVP

**Goal**: Users can mark and unmark movies/TV shows as favorites directly from the main lists.

**Independent Test**: Toggle favorites on media cards in the main lists and verify icon state and labels update.

### Tests for User Story 1 ⚠️

> **NOTE: Red = write tests first and ensure they FAIL before implementation. Green = implement the minimum to pass. Refactor = improve code with tests still passing.**

- [ ] T006 [US1] Red: Add favorites data source tests for toggle behavior

  Description: Write tests for ToggleFavorite to verify DAO calls and error propagation via Either.
    Assert that mediaType and isFavorite mapping flows through to domain Media objects.

  Paths:
    - feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSourceTest.kt

  Depends on: T003

- [ ] T007 [US1] Red: Add MediaCatalogViewModel tests for favorite toggling

  Description: Add failing tests for toggle events to verify state updates and error handling.
    Cover both success and failure paths for ToggleFavorite.

  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModelTest.kt

  Depends on: T003

- [ ] T008 [US1] Red: Add media card UI tests for favorite state and accessibility

  Description: Add failing Compose tests to assert heart icon state and content descriptions on media cards.
    Ensure labels match the new core_ui strings for add/remove favorites.

  Paths:
    - feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaScreenTest.kt

  Depends on: T001

### Implementation for User Story 1

- [ ] T009 [US1] Implement FavoritesDao and wire database access

  Description: Add DAO queries to toggle favorites and update isFavorite in RoomMedia.
    Expose the DAO through MediaDatabase and Hilt providers for framework access.

  Paths:
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDao.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/RoomMediaModule.kt

  Depends on: T004

- [ ] T010 [US1] Implement ToggleFavorite data source wiring

  Description: Implement the ToggleFavorite use case using the FavoritesDao and Arrow Either error handling.
    Bind the implementation in MediaDataModule for Hilt injection.

  Paths:
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSource.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/MediaDataModule.kt

  Depends on: T006

- [ ] T011 [US1] Update media paging mappings with favorites fields

  Description: Populate mediaType and isFavorite when storing RoomMedia and when mapping to domain Media.
    Ensure existing paging flows include favorite state for UI rendering.

  Paths:
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/paging/MediaCatalogRemoteMediator.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/repositories/MediaCatalogRepository.kt

  Depends on: T006

- [ ] T012 [US1] Add favorite toggle UI to MediaCatalogScreen

  Description: Add the heart overlay, animation, and click handling on media cards in both pager and grid.
    Wire UI actions to MediaCatalogViewModel and surface error message on failure.

  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCatalogScreen.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/composables/FilmMaskImageView.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/composables/CarouselImageView.kt

  Depends on: T007

### Refactor for User Story 1 (MANDATORY)

- [ ] T013 [US1] Refactor favorites toggle flow with tests green

  Description: Simplify toggle state plumbing and composable extraction without changing behavior.
    Keep all tests passing and maintain clear separation of UI and domain logic.

  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCatalogScreen.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSource.kt

  Depends on: T012

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Browse favorites by category (Priority: P2)

**Goal**: Users can open Favorites from the drawer and switch between Movies and TV Shows when both exist.

**Independent Test**: Open Favorites with different mixes of favorite items and verify tabs, defaults, and lists.

### Tests for User Story 2 ⚠️

> **NOTE: Red = write tests first and ensure they FAIL before implementation. Green = implement the minimum to pass. Refactor = improve code with tests still passing.**

- [ ] T014 [US2] Red: Add FavoritesViewModel tests for tabs and empty state

  Description: Write failing tests for tab visibility, default tab selection, and empty state messaging.
    Include loading state coverage while favorites are fetched.

  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt

  Depends on: T003

- [ ] T015 [US2] Red: Add favorites data source observe tests for media type filtering

  Description: Add failing tests that verify ObserveFavorites returns only favorited media per type.
    Assert ordering and paging behavior using paging test utilities.

  Paths:
    - feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSourceTest.kt

  Depends on: T009

- [ ] T016 [US2] Red: Add UI tests for Favorites screen and drawer entry

  Description: Add failing Compose tests for tab visibility, default selection, and empty state text.
    Extend drawer UI tests to validate the Favorites entry and selection behavior.

  Paths:
    - feature/media/media_ui/src/androidTest/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt
    - app/src/androidTest/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerViewTest.kt

  Depends on: T001

### Implementation for User Story 2

- [ ] T017 [US2] Implement favorites queries and ObserveFavorites use case

  Description: Add FavoritesDao queries for favorites by media type and expose ObserveFavorites in the data source.
    Bind the ObserveFavorites implementation through Hilt.

  Paths:
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDao.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSource.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/MediaDataModule.kt

  Depends on: T015

- [ ] T018 [US2] Build FavoritesViewModel and FavoritesScreen UI

  Description: Implement state management for movies/TV favorites, tab logic, loading, and empty states.
    Render paging lists and handle navigation to media details.

  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt

  Depends on: T014

- [ ] T019 [US2] Add Favorites navigation route and drawer wiring

  Description: Add Favorites destination to media navigation and integrate a new drawer item entry point.
    Ensure the AppBottomBar does not conflict with Favorites-specific tabs.

  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/MediaNavigation.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/MoviesNavGraph.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/model/DrawerItem.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerView.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/DrawerScaffoldView.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/Navigator.kt

  Depends on: T018

### Refactor for User Story 2 (MANDATORY)

- [ ] T020 [US2] Refactor favorites navigation and UI after tests pass

  Description: Clean up tab logic, navigation wiring, and UI layout without changing behavior.
    Keep tests green while aligning with existing Compose patterns.

  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/DrawerScaffoldView.kt

  Depends on: T019

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Session-based favorites lifecycle (Priority: P3)

**Goal**: Favorites persist across restarts during a session and are cleared when the session ends or expires.

**Independent Test**: Restart the app, log out, and validate favorites are cleared on session end.

### Tests for User Story 3 ⚠️

> **NOTE: Red = write tests first and ensure they FAIL before implementation. Green = implement the minimum to pass. Refactor = improve code with tests still passing.**

- [ ] T021 [US3] Red: Add tests for clear favorites and session cleanup flow

  Description: Add failing tests for ClearAllFavorites behavior and MainViewModel session-end handling.
    Include guest session expiration cases that should trigger a favorites clear.

  Paths:
    - feature/media/media_framework/src/test/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSourceTest.kt
    - app/src/test/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModelTest.kt

  Depends on: T003

### Implementation for User Story 3

- [ ] T022 [US3] Implement ClearAllFavorites use case

  Description: Add the DAO update for clearing favorites and expose it via the data source and Hilt binding.
    Keep the cleanup independent of auth modules and rely on app orchestration.

  Paths:
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDao.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSource.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/di/MediaDataModule.kt

  Depends on: T021

- [ ] T023 [US3] Wire session end and guest expiration to favorites cleanup

  Description: After CloseSession succeeds, invoke ClearAllFavorites and update state accordingly.
    Observe guest session validity and clear favorites when the session is expired.

  Paths:
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt

  Depends on: T022

### Refactor for User Story 3 (MANDATORY)

- [ ] T024 [US3] Refactor session cleanup logic with tests green

  Description: Consolidate session cleanup branching for signed-in and guest flows.
    Keep data source usage and state updates straightforward and test-backed.

  Paths:
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt
    - feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/storage/FavoritesDataSource.kt

  Depends on: T023

**Checkpoint**: All user stories should now be independently functional

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T025 Update schema artifacts for MediaDatabase changes

  Description: Regenerate and verify Room schema output after adding favorites fields.
    Keep schema files aligned with the updated MediaDatabase version.

  Paths:
    - feature/media/media_framework/schemas/com.davidluna.tmdb.media_framework.data.local.database.MediaDatabase/1.json
    - feature/media/media_framework/schemas/com.davidluna.tmdb.media_framework.data.local.database.MediaDatabase/2.json

  Depends on: T004

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
Task: "Red: Add favorites data source tests for toggle behavior"
Task: "Red: Add MediaCatalogViewModel tests for favorite toggling"

# Launch all UI updates for User Story 1 together:
Task: "Add favorite toggle UI to MediaCatalogScreen"
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
