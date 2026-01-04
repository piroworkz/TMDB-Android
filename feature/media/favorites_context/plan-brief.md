# planBrief.md

> **Purpose**  
> This document captures **existing technical facts and constraints** of the repository that are relevant for planning the Favorites feature.  
> It is **authoritative for repository facts**.

---

## 1. Feature Context

```md
Feature name: Favorites
Primary TARGET_SCOPE module: feature/media/
```

The Favorites feature lives within the media context since it deals with marking and displaying favorite movies and TV shows. It extends the existing media catalog functionality with favorite toggle capability and adds a new dedicated Favorites screen accessible via the navigation drawer.

---

## 2. Existing Modules & Ownership

```md
- Domain module(s) involved:
  - feature/media/media_domain (primary)
  - feature/auth/auth_domain (session management)

- Framework module(s) involved:
  - feature/media/media_framework (primary - persistence)
  - feature/auth/auth_framework (session data access)

- UI module(s) involved:
  - feature/media/media_ui (primary - screens and components)
  - app (navigation drawer modification)
```

**Cross-module dependencies:**
- Favorites feature MUST access session data from `auth_framework` to:
  - Determine if user is guest or authenticated
  - Access session expiration timestamp (`expiresAt`) for guest users
  - Clear favorites on session end
- The UI layer will extend existing `MediaCatalogScreen` composables with favorite toggle functionality
- Navigation drawer in `app` module requires adding a new "Favorites" menu item

**Known coupling constraints:**
- Cannot create new databases - must use existing `MediaDatabase` in `media_framework`
- Must reuse existing `Media` domain entity and `RoomMedia` persistence entity
- Session lifecycle is managed by `auth_framework` - favorites must react to session changes (if necessary to avoid circular dependencies, could clear database from app module, look at MainViewModel.kt)

---

## 3. Existing Persistence / Data Layer (if any)

```md
Database(s):
- Name: MediaDatabase
- Location (module + path): feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt

Entities / Tables:
- Entity name: RoomMedia (existing - will be reused for context)
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/media/RoomMedia.kt
- Current fields: category: String, id: Int, posterPath: String, title: String
- NEW fields to add:
  - mediaType: String (values: "MOVIE" or "TV_SHOW" - derived from catalog in MediaCatalogRemoteMediator)
  - isFavorite: Boolean (default: false - toggleable by user)
- Primary Key: composite (id, category) - NO CHANGES
- Note: NO new table needed - reusing existing RoomMedia entity

DAOs:
- Existing: MediaDao
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDao.kt
- Current operations: insertMedia, getMedia (with paging), deleteCatalog
- NO new operations needed - favorites handled by separate DAO

- NEW DAO: FavoritesDao (to be created)
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDao.kt
- Responsible for all favorite-related operations on RoomMedia table
- Expected operations:
  - toggleFavorite(mediaId: Int, category: String, isFavorite: Boolean): Int
  - getFavoritesByMediaType(mediaType: String): PagingSource<Int, RoomMedia>
  - clearAllFavorites(): Int
  - Note: Queries RoomMedia table filtering by isFavorite and mediaType fields
```

**Constraints:**
- Reuse required: YES - must extend existing `RoomMedia` entity (NO new tables)
- Database access: Only `MediaDatabase` (NO access to `AuthenticationDatabase` to avoid circular dependency)
- Migration strategy: **NOT REQUIRED** - app not published yet, can modify schema freely
- Schema changes needed:
  - Add `mediaType: String` field to `RoomMedia` (derived from `Catalog.mediaType` in RemoteMediator)
  - Add `isFavorite: Boolean` field to `RoomMedia` (default: false)
  - Mapping happens in `MediaCatalogRemoteMediator.kt` during remote to local conversion
- Favorites cleanup strategy:
  - **NO sessionId linking in database** - this would create tight coupling with auth module
  - Instead: Simple "set all isFavorite = false" strategy triggered from `app` module when session ends
  - `MainViewModel` orchestrates: `CloseSession` → `ClearAllFavorites` (sequential execution)
  - This keeps `media_framework` independent from `auth_framework`
  - Guest session expiration handled similarly via `MainViewModel` on app startup or screen navigation

---

## 4. Existing Models / Contracts

```md
Domain models involved:
- Media (existing - will be EXTENDED)
  Path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt
  Current fields: id: Int, posterPath: String, title: String
  NEW fields to add:
    - mediaType: MediaType (to differentiate MOVIE vs TV_SHOW)
    - isFavorite: Boolean (default: false, reflects favorite status)

- MediaType (existing - will be used to differentiate movie vs TV favorites)
  Path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/MediaType.kt
  Values: MOVIE, TV_SHOW

- Catalog (existing - will be EXTENDED with mediaType property)
  Path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Catalog.kt
  NEW property to add:
    - val mediaType: MediaType (computed property to derive MediaType from catalog name)
    - Example: MOVIE_NOW_PLAYING.mediaType → MediaType.MOVIE
    - Example: TV_AIRING_TODAY.mediaType → MediaType.TV_SHOW

NO new domain entities required:
- NO Favorite entity needed - favorite status is now a field in Media entity
- Session entity (existing - read-only for app module orchestration):
  Path: feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/entities/Session.kt
  Fields: sessionId: String, isGuest: Boolean, expiresAt: String?
  Usage: Only used by `app` module to trigger cleanup - NO direct usage in media module

Use case interfaces involved from other modules (read-only, used by app module):
- ObserveSession (already used by MainViewModel)
  Path: feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/usecases/ObserveSession.kt
  Usage: `app` module can observe session state changes if needed for cleanup triggers

- CloseSession (already used by MainViewModel)
  Path: feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/usecases/CloseSession.kt
  Current usage: MainViewModel.endSession() → will be extended to also call ClearAllFavorites

Note: `media_domain` and `media_framework` MUST NOT depend on these auth use cases directly

NEW use case interfaces required (to be created in media_domain):
- Favorites use cases are interfaces with named members (no `operator invoke`):
  - ToggleFavorite
    - fun toggle(mediaId: Int, category: String): Either<AppError, Unit>
    - Note: Takes category instead of mediaType since RoomMedia primary key is (id, category)
  - ObserveFavorites
    - val favorites: Flow<PagingData<Media>>
    - Note: Returns paginated Media items filtered by mediaType and isFavorite = true
    - Consistent with existing media catalog pagination pattern
  - ClearAllFavorites
    - fun clear(): Either<AppError, Unit>
    - Note: Invoked from `app` module's `MainViewModel` when session ends
    - Implemented in `media_framework` to set isFavorite = false for all RoomMedia entries
    - NO dependency on auth modules - pure favorites cleanup logic
```

**Notes:**
- Existing contracts are stable where not explicitly extended in this document
- Favorites use cases are interfaces with named members (no `operator invoke`)
- Favorite status will be derived at UI layer by combining Media + IsFavorite flow
- **Session cleanup orchestration strategy:**
  - `MainViewModel` already uses `ObserveSession` from `auth_domain` (read-only access)
  - `MainViewModel.endSession()` calls `CloseSession` use case which deletes session from auth database
  - After successful session deletion, `MainViewModel` will also call `ClearAllFavorites` use case
  - This avoids circular dependency: `media_framework` doesn't depend on `auth_framework`
  - Clean separation: auth manages session lifecycle, media manages favorites data, app orchestrates both

---

## 5. Existing UI Entry Points

```md
Screens / ViewModels already present:
- MediaCatalogScreen (will be extended with favorite toggle)
  Path: feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/media/MediaCatalogScreen.kt
  ViewModel: MediaCatalogViewModel
  Path: feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogViewModel.kt

NEW screens required:
- FavoritesScreen (to be created)
  Expected path: feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/view/favorites/FavoritesScreen.kt
  ViewModel: FavoritesViewModel (to be created)
  Expected path: feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/presenter/favorites/FavoritesViewModel.kt

Navigation:
- Existing navigation: MoviesNavGraph
  Path: feature/media/media_ui/src/main/kotlin/com/davidluna/tmdb/media_ui/navigation/MoviesNavGraph.kt

- Navigation drawer: NavDrawerView
  Path: app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/view/composables/NavDrawerView.kt
  Drawer items model: app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/model/DrawerItem.kt

- Existing routes affected:
  - Drawer menu MUST add new "Favorites" item between media categories and "Close Session"
  - New route required: FavoritesScreen destination in MoviesNavGraph

- Session cleanup coordination:
  - Location: app/src/main/kotlin/com/davidluna/tmdb/app/main_ui/presenter/MainViewModel.kt
  - Current behavior: `endSession()` calls `CloseSession` use case to delete session/account from auth database
  - Required modification: After successful `CloseSession`, also invoke `ClearAllFavorites` to clean favorites
  - This ensures atomic cleanup: session ends → favorites cleared
```

**Navigation & lifecycle flows:**

User navigation:
1. User taps "Favorites" in drawer → navigates to FavoritesScreen
2. FavoritesScreen shows bottom tabs (Movies/TV Shows) only when both categories have favorites
3. Default tab: Movies (unless empty, then TV Shows)
4. User can tap media item → navigates to existing MediaDetailScreen

Session cleanup flow:
1. User taps "Close Session" in drawer → triggers `MainEvent.OnCloseSession`
2. `MainViewModel.endSession()` executes:
   - Calls `CloseSession` use case (deletes session from auth database)
   - On success, calls `ClearAllFavorites` use case (deletes all favorites from media database)
   - Updates `state.isSessionClosed = true`
3. FavoritesScreen observes empty favorites list → shows empty state automatically

---

## 6. Resource & Asset Constraints

```md
UI resources policy:
- ALL resources MUST live in: feature/core/core_ui/src/main/res/
- NO feature-exclusive resources allowed
- Resources are shared across all features by design
```

---

## 7. Technical Constraints & Non-Goals

```md
Must NOT:
- Create new databases or tables (extend existing RoomMedia entity only)
- Create separate Favorite entity (reuse Media with isFavorite field)
- Introduce new libraries (use existing Stack: Room, Hilt, Arrow, Compose, Flow)
- Add server synchronization (local-only, session-based storage)
- Support cross-device favorites (session-scoped only)
- Break existing MediaType or Session entities
- Add a repository for single data source flows (use data source directly)
- Define favorites use cases as `fun interface` with `operator invoke`

Must DO:
- Extend `Media` domain entity with `mediaType: MediaType` and `isFavorite: Boolean` fields
- Extend `RoomMedia` entity with `mediaType: String` and `isFavorite: Boolean` fields
- Add computed property `mediaType` to `Catalog` enum
- Map `mediaType` in `MediaCatalogRemoteMediator` when converting RemoteMedia → RoomMedia
- Preserve existing `isFavorite` value when updating media from remote
- Use session-based lifecycle (clear on logout) orchestrated from `app` module
- Keep `media_framework` independent - NO direct auth dependencies
- Follow TDD for ViewModels, repositories, data sources, and use case implementations
- Use Arrow's Either<AppError, Success> for error handling
- Use named functions for favorites use cases so the local data source implements them
- Follow existing naming conventions (no Impl suffix, Spy for test doubles)
- Apply proper convention plugins per module type
- Ensure all UI resources live in core_ui module
- Extend `MainViewModel.endSession()` to call `ClearAllFavorites` after successful `CloseSession`

Architectural Constraints:
- Domain layer MUST remain Android-free
- Use fun interface for single-method use cases
- Repositories implement use case interfaces directly (no Impl suffix)
- StateFlow for reactive UI state
- Hilt for dependency injection
- Type-safe navigation with @Serializable routes
```

---

## 8. Known Risks / Gotchas (Optional)

```md
Risk: Session expiration cleanup timing
- Guest sessions expire based on expiresAt timestamp
- Cleanup strategy: Orchestrate from `MainViewModel` on app startup
- Option 1: Check session validity via `ObserveSession` or `IsGuestSessionValid` in MainActivity/MainViewModel init
- Option 2: Clear favorites when FavoritesScreen detects expired session (defensive approach)
- Recommended: MainViewModel init checks session validity → triggers cleanup if expired
- This centralizes session lifecycle management in app module (Single Responsibility)

Risk: Database migration complexity
- NOT APPLICABLE: app not published yet, schema can change without migration

Risk: Drawer item selection state
- Adding Favorites item to drawer requires updating DrawerItem sealed class
- Must coordinate with MainViewModel for drawer item selection
- Ensure proper navigation flow when "Favorites" is selected

Risk: Bottom navigation tab visibility logic
- Tabs only show when BOTH categories have favorites
- Edge case: User favorites last item in one category - tabs should disappear
- Need proper reactive logic to show/hide tabs dynamically

Risk: Favorite toggle race conditions
- User might toggle favorite rapidly
- Need debouncing or state management to prevent duplicate operations
- Note: PagingSource invalidation happens asynchronously - UI update has slight delay
- Consider optimistic UI updates with rollback on error for better UX

Risk: PagingSource invalidation performance
- Every ToggleFavorite triggers PagingSource invalidation
- Could cause unnecessary re-fetches if user toggles multiple items quickly
- Consider batching updates or debouncing invalidation if performance issues arise
- For MVP: Simple invalidation is acceptable given expected usage patterns

Gotcha: MediaType derivation from Catalog
- RoomMedia has "category" field which is catalog-specific (e.g., "MOVIE_NOW_PLAYING")
- Solution: Add computed property `mediaType` to Catalog enum
- Mapping happens in MediaCatalogRemoteMediator when converting RemoteMedia → RoomMedia
- Store derived `mediaType: String` in RoomMedia for efficient querying
- FavoritesDao queries: `WHERE isFavorite = 1 AND mediaType = :mediaType`

Gotcha: Preserving existing isFavorite on update
- MediaCatalogRemoteMediator updates media from remote (pagination, refresh)
- Must preserve existing `isFavorite` value when updating RoomMedia
- Strategy: Use Room @Upsert with conflict strategy or check existing value before update
- Avoid resetting user's favorites when new pages load

Gotcha: Module dependency direction
- `app` module already depends on both `media_domain` and `auth_domain` (see MainViewModel imports)
- This makes `app` the ideal orchestration layer for cross-module workflows
- `media_framework` MUST NOT depend on `auth_framework` - violates clean architecture
- Solution: `app` module acts as composition root and workflow coordinator
```

---

## 9. Authority Statement (DO NOT REMOVE)

```md
If this PlanBrief is present:
- It is authoritative for repository facts.
- The Plan MUST NOT contradict it.
- If conflicts or ambiguities are detected, the agent MUST ask before proceeding.
```
