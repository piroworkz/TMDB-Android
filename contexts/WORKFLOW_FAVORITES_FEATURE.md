# Workflow: Favorites Feature

1) Goal and scope
- Allow users to mark/unmark media items (movies, series, etc.) as favorites directly from poster cards on the listings screen only.
- Show favorite state visually on each card while keeping existing layout spacing, hierarchy, and Material 3 styling seen in the provided reference image.
- Persist favorite state locally on-device; no network sync or server storage.

2) User stories and acceptance criteria
- As a viewer, I can tap the heart icon on any media card to toggle its favorite state; the icon updates instantly.
- As a viewer, when I return to the listings screen, cards remember their favorite state from prior sessions.
- As a viewer, favorites do not affect other screens (details, search, navigation); only the listings grid/carousel shows the state.

3) UI behavior (icon states and placement)
- Place a heart icon overlay at the top-right corner of each poster card, inset with the same padding and elevation treatment used for card content in the reference.
- Disabled/inactive state: outlined heart with neutral stroke (e.g., on-surface variant) on a translucent container to preserve contrast over imagery.
- Enabled/active state: filled heart using the primary color, same size and position as the inactive icon; tap target meets Material minimums (48dp).
- Icon visibility is always on; no hover/long-press needed. Taps toggle immediately with ripple/pressed feedback per Material 3.

4) Data model and local persistence strategy
- Model: `FavoriteMedia` with fields `mediaId: Long`, `mediaType: MediaType` (or similar enum used alongside `Media`), `addedAt: Instant`; optional `title/posterPath` cache if needed for display resilience.
- Storage: Room or DataStore (Room recommended for querying ID + type pairs) in a new local data source inside the framework layer, keyed by (`mediaId`, `mediaType`).
- Repository: `FavoritesRepository` exposing `isFavorite(mediaId: Long, mediaType: MediaType): Flow<Boolean>`, `toggle(mediaId: Long, mediaType: MediaType): Unit`, `favorites(): Flow<Set<MediaKey>>` (where `MediaKey` is a value object of id + type) for UI consumption.
- No remote API calls; persistence is device-only. Avoid storing large poster images; rely on existing image loader.

5) State management flow (UI → persistence → UI)
- On listings load, ViewModel collects `favorites()` to build UI state and mark cards (match by `mediaId` + `mediaType` against `Media` items).
- User taps heart on a card → ViewModel calls `toggle(mediaId, mediaType)` → repository writes to local store.
- Repository emits updated `favorites()` → ViewModel maps to UI model → Composables recomposed with new icon state.
- Handle optimistic updates: update UI state immediately, rely on persistence flow to confirm. Log errors but keep UI responsive.

6) Step-by-step implementation checklist
1. Review the reference image to match card hierarchy: section headers (e.g., UPCOMING, NOW PLAYING), horizontal and grid lists, poster sizing, and icon overlay placement/padding consistent with Material 3.
2. Add domain contracts in `feature/media/media_domain`: define `FavoritesRepository` interface and use cases `ObserveFavorites` and `ToggleFavorite` that accept `mediaId` + `mediaType` (aligned with `Media` model).
3. Implement local data source in `feature/media/media_framework`: Room entity/DAO or DataStore schema keyed by (`mediaId`, `mediaType`); provide repository implementation, mappers, and Hilt module bindings.
4. Extend UI models in `feature/media/media_ui` to carry `isFavorite` per `Media`-backed card within existing listing item models; default to false until data arrives.
5. Update the media listings ViewModel in `feature/media/media_ui` to combine the media feed with `favorites()` flow; expose UI state with favorite flags and toggle handler that passes both ID and type.
6. Update poster card composable(s) in `feature/media/media_ui` to render top-right heart overlay (outlined/filled states), apply padding/elevation matching the reference, and add content description for accessibility.
7. Ensure the listings screen in `feature/media/media_ui` collects ViewModel state and passes `onFavoriteClick(mediaId, mediaType)` to each card; keep behavior scoped to this screen only.
8. Add Compose preview(s) in `feature/media/media_ui` showing mixed favorite states across media types to verify spacing, icon placement, and Material 3 theming.

7) Testing checklist (unit + UI)
1. Unit: `FavoritesRepository` toggles persistently (add/remove), emits expected `favorites()` sequences, and handles duplicate toggles idempotently.
2. Unit: ViewModel maps repository flows to UI state (favorite flag per card) and triggers repository on toggle.
3. Unit: Use cases validate behavior (observe + toggle) without UI dependencies.
4. UI test: tapping the heart on a card flips between outlined/filled states and persists across recompositions/process recreation (use fake local store or in-memory Room).
5. UI test: listings show correct icon states when favorites already exist before screen load.

8) Edge cases and non-goals
- Edge cases: rapid double-taps should remain idempotent; stale IDs for media items no longer in feed should be ignored gracefully; handle missing posters by keeping icon placement consistent.
- Non-goals: no server sync, no cross-device sync, no favorites badge in nav or other screens, no sort/filter by favorites unless separately scoped.
