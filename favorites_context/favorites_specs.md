# Feature Specification: favorites

**Feature Branch**: `feature/favorites`  
**Created**: 2026-01-03  
**Status**: Draft  
**Input**: User description: "Allow all users, signed in and guests to mark/unmark favorites so they can return to favorite media items anytime.
Users can do so from any list from main screen.
movies and TV shows can be marked as favorites.
Favorites will be shown on a dedicated Favorites screen, through new drawer option \"Favorites\" two lists by media type (movies, tv shows) with bottom navigation bar like in main screen to switch between lists of favorites.
When the Favorites list is empty show empty message.
Favorites expected to persist only within the current session, persisted locally only."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Mark and unmark favorites on main lists (Priority: P1)

Users can favorite or unfavorite a movie or TV show directly from any main screen list so they can quickly save items while browsing.

**Why this priority**: This is the core action that creates value; without it, users cannot save items to return to.

**Independent Test**: From a main screen list, toggle the heart icon on an item and confirm the visual state changes accordingly.

**Acceptance Scenarios**:

1. **Given** the user is viewing any main screen list with a movie or TV show that is not a favorite, **When** the user taps the heart icon on that item, **Then** the heart icon shows the filled state for that item.
2. **Given** the user is viewing any main screen list with a movie or TV show that is a favorite, **When** the user taps the heart icon on that item, **Then** the heart icon shows the stroked state for that item.

---

### User Story 2 - View favorites by media type (Priority: P2)

Users can open the Favorites screen from the drawer and see their saved movies and TV shows separated into two lists so they can find items quickly.

**Why this priority**: This delivers the “return to favorites” value once favorites exist.

**Independent Test**: With at least one movie and one TV show marked as favorites, open the Favorites screen and verify the default list and list contents.

**Acceptance Scenarios**:

1. **Given** the user has at least one favorite movie and one favorite TV show, **When** the user opens the Favorites screen from the drawer, **Then** the Movies list is shown by default and contains the favorited movies in the same order as their source list.
2. **Given** the user has favorites in only one media type, **When** the user opens the Favorites screen from the drawer, **Then** the non-empty list is shown by default and contains the favorited items in the same order as their source list.

---

### User Story 3 - Manage favorites on the Favorites screen (Priority: P3)

Users can remove favorites from the Favorites screen and see an empty state when no favorites remain so they can keep their list current.

**Why this priority**: This supports cleanup and keeps the Favorites screen useful over time.

**Independent Test**: Open the Favorites screen with at least one item, remove it, and verify the list updates and empty message appears when appropriate.

**Acceptance Scenarios**:

1. **Given** the user is viewing a Favorites list that contains a favorited item, **When** the user taps the heart icon on that item, **Then** the item is removed from that Favorites list and the heart icon shows the stroked state for that item.
2. **Given** the user has no favorite movies and no favorite TV shows, **When** the user opens the Favorites screen from the drawer, **Then** the message "You haven't added Favorites yet" is shown.

---

### Edge Cases

- What happens when the user has favorite TV shows but no favorite movies and opens the Favorites screen?
- What happens when the user removes the last item in a Favorites list?
- What happens when a guest user marks favorites and later opens the Favorites screen in the same session?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow both signed-in and guest users to mark a movie or TV show as a favorite from any main screen list.
- **FR-002**: The system MUST allow users to unmark a favorite from any main screen list.
- **FR-003**: The system MUST display a filled heart icon for favorited items and a stroked heart icon for non-favorited items in the main lists.
- **FR-004**: The system MUST provide a drawer option labeled "Favorites" that opens the Favorites screen.
- **FR-005**: The Favorites screen MUST present two lists by media type (Movies and TV Shows) and allow switching between them using the same bottom navigation control as the main screen.
- **FR-006**: When both lists are non-empty, the Favorites screen MUST open on Movies; when only one list is non-empty, it MUST open on that list.
- **FR-007**: The system MUST show the message "You haven't added Favorites yet" when the Favorites screen has no items.
- **FR-008**: Favorites MUST remain available during the current session and MUST be cleared when the session ends.
- **FR-009**: The system MUST allow users to unmark favorites from the Favorites screen lists.

### Key Entities *(include if feature involves data)*

- **User**: A signed-in or guest person using the app.
- **Favorite Item**: A saved movie or TV show the user marked as a favorite.
- **Favorites List**: A collection of favorite items grouped by media type (Movies or TV Shows).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: In usability testing, 90% of users can mark a favorite and see the filled heart state change on the first tap.
- **SC-002**: In usability testing, 90% of users who open the Favorites screen can find a favorited item within 30 seconds.
- **SC-003**: In sessions where users remove their last favorite, 95% see the empty message immediately after opening Favorites.
