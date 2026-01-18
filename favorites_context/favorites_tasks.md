# Tasks: favorites

## User Story 1 (P1): Toggle favorites from media cards

### Red (tests first)

- [ ] T001 [US1] Define repository edge tests for toggle behavior
  Description: Specify toggle success/failure behavior and immediate state updates when changing
    favorite status from local storage.
  Paths:
    - feature/media/media_data/src/test/kotlin/com/davidluna/tmdb/media_data/data/repositories/MediaFavoritesRepositoryTest.kt

- [ ] T002 [US1] Define MediaCatalog ViewModel tests for favorite toggles
  Description: Validate UI state updates and error signaling when toggling favorites in the grid.
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModelTest.kt

### Green (minimal implementation)

- [ ] T003 [US1] Add toggle favorite contract and favorite flag on Media
  Description: Introduce the toggle use case contract and surface `isFavorite` on Media so UI
    can render favorite state from domain data.
  Paths:
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ToggleFavorite.kt
  Depends on: T001, T002

- [ ] T004 [US1] Extend RoomMedia and mappings to track favorite state
  Description: Add `isFavorite` to RoomMedia and ensure remote/local mappings preserve the flag
    without introducing new tables or migrations.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/entities/media/RoomMedia.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/paging/MediaCatalogRemoteMediator.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/repositories/MediaCatalogRepository.kt
    - feature/media/media_data/src/testFixtures/kotlin/com/davidluna/tmdb/media_data/data/local/database/dao/MediaDaoSpy.kt
  Depends on: T003

- [ ] T005 [US1] Implement favorites toggle repository and DAO updates
  Description: Add DAO update operations, repository implementation, and Koin wiring required
    to satisfy toggle tests.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/dao/MediaDao.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/repositories/MediaFavoritesRepository.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/di/MediaFrameworkRemoteModule.kt
  Depends on: T004

- [ ] T006 [US1] Wire favorite toggle into MediaCatalog UI (including strings)
  Description: Add a heart overlay on media cards, update ViewModel state to reflect toggles,
    surface non-blocking errors, and add required shared strings.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCatalogScreen.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/composables/FavoriteToggleButton.kt
    - feature/core/core_ui/src/main/res/values/strings.xml
  Depends on: T005

### Refactor

- [ ] T007 [US1] Refactor toggle flow for clarity
  Description: Simplify state handling and mapping while keeping tests green.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/repositories/MediaFavoritesRepository.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModel.kt
  Depends on: T006

## User Story 2 (P2): View favorites by media type

### Red (tests first)

- [ ] T008 [US2] Define repository tests for observing favorites by media type
  Description: Specify behavior for movie vs TV favorites queries, including empty results.
  Paths:
    - feature/media/media_data/src/test/kotlin/com/davidluna/tmdb/media_data/data/repositories/MediaFavoritesRepositoryTest.kt

- [ ] T009 [US2] Define Favorites ViewModel and UI state tests
  Description: Validate filter selection, loading/empty/error states, and list rendering.
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreenTest.kt

### Green (minimal implementation)

- [ ] T010 [US2] Add observe favorites contract
  Description: Introduce the observe favorites use case contract required by Favorites UI.
  Paths:
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ObserveFavorites.kt
  Depends on: T008, T009

- [ ] T011 [US2] Implement observe favorites queries and wiring
  Description: Add DAO queries and repository implementation for observing favorites by media type.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/framework/local/database/dao/MediaDao.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/repositories/MediaFavoritesRepository.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/di/MediaFrameworkRemoteModule.kt
  Depends on: T010

- [ ] T012 [US2] Build Favorites screen with filters and states (including strings)
  Description: Create Favorites ViewModel and UI with filter controls, list rendering, and
    empty/loading/error placeholders, adding required shared strings.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/di/MediaPresentationModule.kt
    - feature/core/core_ui/src/main/res/values/strings.xml
  Depends on: T011

- [ ] T013 [US2] Add Favorites navigation destination and drawer entry
  Description: Register a Favorites route and add the drawer item for navigation entry.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/MediaNavigation.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/MoviesNavGraph.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/model/DrawerItem.kt
    - app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerView.kt
  Depends on: T012

### Refactor

- [ ] T014 [US2] Refactor favorites filtering flow
  Description: Simplify filter handling and rendering boundaries with tests green.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
  Depends on: T013

## User Story 3 (P3): Remove favorites from the Favorites screen

### Red (tests first)

- [ ] T015 [US3] Define Favorites ViewModel tests for removal
  Description: Verify removal updates list state and reports non-blocking errors on failure.
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModelTest.kt
  Depends on: T012

### Green (minimal implementation)

- [ ] T016 [US3] Wire removal actions in Favorites UI
  Description: Connect favorites list items to the toggle use case and update list state immediately.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
  Depends on: T015

### Refactor

- [ ] T017 [US3] Refactor removal handling
  Description: Tidy list state transitions while keeping tests green.
  Paths:
    - feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt
  Depends on: T016

## Cross-cutting: Session end clears favorites

### Red (tests first)

- [ ] T018 Define SplashViewModel tests for session-end clearing
  Description: Verify clear-favorites is invoked on guest expiration or logout flows.
  Paths:
    - feature/auth/auth_ui/src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/splash/SplashViewModelTest.kt

### Green (minimal implementation)

- [ ] T019 Add clear favorites contract
  Description: Introduce the clear-favorites use case contract required by session-end handling.
  Paths:
    - feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/ClearFavorites.kt
  Depends on: T018

- [ ] T020 Implement clear favorites behavior and wiring
  Description: Implement clear favorites in data, wire Koin, and trigger from SplashViewModel.
  Paths:
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/repositories/MediaFavoritesRepository.kt
    - feature/media/media_data/src/main/kotlin/com/davidluna/tmdb/media_data/di/MediaFrameworkRemoteModule.kt
    - feature/auth/auth_ui/src/main/kotlin/com/davidluna/tmdb/auth_ui/presenter/splash/SplashViewModel.kt
  Depends on: T019

### Refactor

- [ ] T021 Refactor session-end clearing flow
  Description: Simplify invocation paths while keeping tests green.
  Paths:
    - feature/auth/auth_ui/src/main/kotlin/com/davidluna/tmdb/auth_ui/presenter/splash/SplashViewModel.kt
  Depends on: T020

## Integration

- [ ] T022 Add JVM integration tests for favorites flows
  Description: Use spies to validate cross-layer behavior for toggling, observing, and clearing favorites.
  Paths:
    - feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesIntegrationTest.kt
    - feature/auth/auth_ui/src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/splash/SplashIntegrationTest.kt
  Depends on: T020
