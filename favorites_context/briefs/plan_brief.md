# Plan Brief — favorites

## Authoritative decisions
- Persistence MUST reuse existing Room database in `media_data`.
- Do NOT create new tables.
- Extend existing `RoomMedia` by adding: `isFavorite: Boolean = false`.
- No migration work planned (app not published yet).
- Favorites are session-scoped and MUST be cleared when session ends.

## Preferred approach (discussable)
- Likely clear favorites by observing session state at a high-level owner (e.g., MainViewModel).
- If you propose a simpler/cleaner approach, STOP and ask before choosing it.

## STOP / Questions
STOP and ask before writing the plan if unclear:
- where session state is owned/observed in the current architecture
- whether `RoomMedia` stores both Movies and TV Shows in the same table or separate entries
