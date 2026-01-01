# Feature Specification: Media Favorites

**Feature Branch**: `[favorites-media]`  
**Created**: 2025-02-14  
**Status**: Draft  
**Input**: User description: "Add functionality to mark/unmark media items as favorites from a list; add a Favorites option in the drawer and a new screen with lists by media type (TV shows or movies)."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Toggle favorites from the media list (Priority: P1)

As a user, I want to mark or unmark a media item as favorite from the list so I can save preferred content without leaving the current screen.

**Why this priority**: This is the core action that enables the rest of the flow.

**Independent Test**: Test directly from the media list by toggling a favorite and verifying the UI updates.

**Acceptance Scenarios**:

1. **Given** a visible media list, **When** the user marks an item as favorite, **Then** the item reflects the favorite state and the selection is persisted locally.
2. **Given** a previously favorited item, **When** the user unmarks it, **Then** the item returns to non-favorite state and local persistence is updated.

---

### User Story 2 - Access Favorites screen from the drawer (Priority: P2)

As a user, I want a Favorites option in the drawer to quickly navigate to my favorites.

**Why this priority**: It enables discovery and access to the dedicated favorites list.

**Independent Test**: Test by opening the drawer and navigating to the Favorites screen.

**Acceptance Scenarios**:

1. **Given** the drawer is open, **When** the user selects Favorites, **Then** they navigate to the Favorites screen.

---

### User Story 3 - View favorites by media type (Priority: P3)

As a user, I want to see my favorites separated by type (TV shows or movies) to browse more easily.

**Why this priority**: It improves organization of favorite content.

**Independent Test**: Test by showing favorites with at least one item per type and verifying grouping.

**Acceptance Scenarios**:

1. **Given** favorites of both TV and movies, **When** the user enters the Favorites screen, **Then** they see separate lists by type.
2. **Given** there are no favorites of one type, **When** the user enters the Favorites screen, **Then** the other type is shown and an empty state appears for the missing type.

---

### Edge Cases

- What happens if the user is offline when toggling a favorite?
- How is an inconsistent state between UI and local persistence handled?
- What is shown when there are no favorites of any type?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow toggling a media item as favorite from the list.
- **FR-002**: The system MUST persist favorite state locally only (no remote sync).
- **FR-003**: The user MUST be able to access Favorites from a drawer option.
- **FR-004**: The system MUST display a dedicated Favorites screen.
- **FR-005**: The system MUST group favorites by media type (TV shows and movies).
- **FR-006**: The system MUST show an empty state message when there are no favorites.
- **FR-007**: The UI MUST reflect favorite changes immediately.

### Key Entities *(include if feature involves data)*

- **FavoriteItem**: A media item marked as favorite (id, mediaType, title, posterPath, timestamp).
- **MediaType**: Media type (TV, Movie) used for grouping and filtering favorites.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of toggled items reflect the correct favorite state in the UI immediately.
- **SC-002**: Users can reach Favorites from the drawer in 2 taps or fewer.
- **SC-003**: The Favorites screen correctly shows lists separated by type in all data scenarios.
- **SC-004**: Less than 1% of sessions report inconsistencies between UI and local persistence.
