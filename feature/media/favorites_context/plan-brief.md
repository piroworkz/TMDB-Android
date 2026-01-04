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
- Session lifecycle is managed by `auth_framework` - favorites must react to session changes

---

## 3. Existing Persistence / Data Layer (if any)

```md
Database(s):
- Name: MediaDatabase
- Location (module + path): feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/MediaDatabase.kt

Entities / Tables:
- Entity name: RoomMedia (existing - will be reused for context)
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/media/RoomMedia.kt
- Fields: category: String, id: Int, posterPath: String, title: String
- Primary Key: composite (id, category)

- NEW Entity required: RoomFavorite
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/entities/favorites/RoomFavorite.kt (to be created)
- Expected fields: mediaId: Int, mediaType: String (MOVIE/TV_SHOW), sessionId: String, addedAt: Long

DAOs:
- Existing: MediaDao
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/MediaDao.kt
- Current operations: insertMedia, getMedia (with paging), deleteCatalog

- NEW DAO required: FavoritesDao
- File path: feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/local/database/dao/FavoritesDao.kt (to be created)
```

**Session Database:**
```md
Database: AuthenticationDatabase
Location: feature/auth/auth_framework/src/main/kotlin/com/davidluna/tmdb/auth_framework/data/local/database/AuthenticationDatabase.kt

Entity: RoomSession
Fields: id: Int, sessionId: String, isGuest: Boolean, expiresAt: String?

DAO: SessionDao
Location: feature/auth/auth_framework/src/main/kotlin/com/davidluna/tmdb/auth_framework/data/local/database/dao/SessionDao.kt
```

**Constraints:**
- Reuse required: YES - must use existing `MediaDatabase` and `AuthenticationDatabase`
- New tables allowed: YES - can add `RoomFavorite` table to `MediaDatabase`
- Migration strategy: Room database version bump from 1 to 2 required in `MediaDatabase` with proper migration strategy
- Favorites MUST be linked to `sessionId` to enable session-based lifecycle
- Favorites MUST be cleared when:
  - Authenticated user logs out (session deleted)
  - Guest session expires (`expiresAt` timestamp passed)

---

## 4. Existing Models / Contracts

```md
Domain models involved:
- Media (existing - will be extended with favorite status in UI state)
  Path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Media.kt
  Fields: id: Int, posterPath: String, title: String

- MediaType (existing - will be used to differentiate movie vs TV favorites)
  Path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/MediaType.kt
  Values: MOVIE, TV_SHOW

- Session (existing - used for session-based favorites lifecycle)
  Path: feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/entities/Session.kt
  Fields: sessionId: String, isGuest: Boolean, expiresAt: String?

- Catalog (existing - used for navigation and categorization)
  Path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Catalog.kt

NEW domain models required:
- Favorite entity (to be created in media_domain)
  Expected path: feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/Favorite.kt
  Expected fields: mediaId: Int, mediaType: MediaType, addedAt: Long

Use case interfaces involved (existing):
- ObserveSession (to react to session changes)
  Path: feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/usecases/ObserveSession.kt

- IsGuestSessionValid (to check guest session expiration)
  Path: feature/auth/auth_domain/src/main/kotlin/com/davidluna/tmdb/auth_domain/usecases/IsGuestSessionValid.kt

NEW use case interfaces required (to be created in media_domain):
- ToggleFavorite: (mediaId: Int, mediaType: MediaType) -> Either<AppError, Unit>
- ObserveFavorites: (mediaType: MediaType?) -> Flow<List<Favorite>>
- IsFavorite: (mediaId: Int, mediaType: MediaType) -> Flow<Boolean>
- ClearSessionFavorites: () -> Either<AppError, Unit>
```

**Notes:**
- Existing contracts are stable - NO changes to `Media`, `MediaType`, `Session` entities
- New use cases follow existing patterns (fun interface with Either for error handling)
- Favorite status will be derived at UI layer by combining Media + IsFavorite flow
- Session management is read-only from favorites perspective

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
```

**Navigation flow:**
1. User taps "Favorites" in drawer → navigates to FavoritesScreen
2. FavoritesScreen shows bottom tabs (Movies/TV Shows) only when both categories have favorites
3. Default tab: Movies (unless empty, then TV Shows)
4. User can tap media item → navigates to existing MediaDetailScreen

---

## 6. Resource & Asset Constraints

```md
UI resources policy:
- ALL resources MUST live in: feature/core/core_ui/src/main/res/
- NO feature-exclusive resources allowed
- Resources are shared across all features by design

Required new resources (to be added in core_ui):
- Strings:
  - drawer_favorites ("Favorites")
  - favorites_empty_state ("No favorites yet. Start adding your favorite movies and TV shows!")
  - favorites_error_toggle ("Could not update favorite. Please try again.")
  - favorites_tab_movies ("Movies")
  - favorites_tab_tv_shows ("TV Shows")
  - content_description_favorite ("Marked as favorite")
  - content_description_not_favorite ("Not marked as favorite")
  - action_add_favorite ("Add to favorites")
  - action_remove_favorite ("Remove from favorites")

- Drawables (icons):
  - Material Icons already available: Icons.Outlined.Favorite, Icons.Filled.Favorite
  - Drawer icon: Icons.Outlined.Star (or similar for Favorites menu item)
```

**Constraint:** All string resources MUST be added to `feature/core/core_ui/src/main/res/values/strings.xml`

---

## 7. Technical Constraints & Non-Goals

```md
Must NOT:
- Create new databases (reuse MediaDatabase and AuthenticationDatabase)
- Change existing Media, Session, or MediaType entities
- Introduce new libraries (use existing Stack: Room, Hilt, Arrow, Compose, Flow)
- Add server synchronization (local-only, session-based storage)
- Support cross-device favorites (session-scoped only)

Must DO:
- Use session-based lifecycle (clear on logout or guest expiration)
- Store sessionId with each favorite for proper cleanup
- Implement Room migration for MediaDatabase version bump
- Follow TDD for ViewModels, repositories, data sources, and use case implementations
- Use Arrow's Either<AppError, Success> for error handling
- Follow existing naming conventions (no Impl suffix, Spy for test doubles)
- Apply proper convention plugins per module type
- Ensure all UI resources live in core_ui module

Architectural Constraints:
- Domain layer MUST remain Android-free
- Use fun interface for single-method use cases
- Repositories implement use case interfaces directly (no Impl suffix)
- StateFlow for reactive UI state
- Hilt for dependency injection
- Type-safe navigation with @Serializable routes
```

---

## 8. References

- Reference assets root: `feature/media/favorites_context/screenshots`
- `feature/media/favorites_context/screenshots/FavsHome.png`: Confirms heart overlay placement/state on home media cards
- `feature/media/favorites_context/screenshots/FavsDrawer.png`: Confirms drawer entry label and position near "Close Session"

---

## 9. Known Risks / Gotchas (Optional)

```md
Risk: Session expiration cleanup timing
- Guest sessions expire based on expiresAt timestamp
- Need background mechanism or on-app-start check to clear expired favorites
- Suggestion: Check session validity on FavoritesScreen initialization and app startup

Risk: Database migration complexity
- MediaDatabase version 1 → 2 requires migration strategy
- Must handle existing data gracefully
- Test migration thoroughly with existing media data

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
- Consider optimistic UI updates with rollback on error

Gotcha: Media entity doesn't include mediaType
- RoomMedia has "category" field but it's catalog-specific (e.g., "MOVIE_NOW_PLAYING")
- Need to derive MediaType from catalog or add mediaType field to Favorite entity
- MediaType must be stored in RoomFavorite for filtering
```

---

## 10. Authority Statement (DO NOT REMOVE)

```md
If this PlanBrief is present:
- It is authoritative for repository facts.
- The Plan MUST NOT contradict it.
- If conflicts or ambiguities are detected, the agent MUST ask before proceeding.
```

---

**Document Version**: 1.0  
**Created**: 2026-01-04  
**Author**: Tech Lead / Software Architect  
**Status**: Draft - Ready for Plan Creation
