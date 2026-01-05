# Feature Specification: favorites

**Feature Branch**: `feature/favorites`  
**Created**: 2026-01-04  
**Status**: Draft  
**Input**: User description: "# Favorites Feature

## Overview
Users can mark movies and TV shows as favorites to quickly access their preferred content. This feature allows both authenticated and guest users to save favorites during their session, accessible through a dedicated Favorites screen in the navigation drawer. Favorites persist across app restarts during a session and are cleared when the session ends (explicit logout for signed-in users, session expiration for guest users).

## Functional Requirements

### Core Features
- [ ] Users can mark/unmark movies as favorites from main screen movie lists
- [ ] Users can mark/unmark TV shows as favorites from main screen TV show lists
- [ ] Users can access all favorites through a \"Favorites\" option in navigation drawer
- [ ] Favorites screen displays two separate tabs: Movies and TV Shows
- [ ] Users can switch between Movies and TV Shows using bottom navigation bar
- [ ] Favorite status is visually indicated on each media item card

### User Scope
All users (authenticated and guest users) can access favorites functionality. No authentication required.

### Data Management
Session-based local storage using local database. Favorites persist during the current user session across app restarts and are cleared when the session ends (explicit logout for signed-in users, expiresAt timestamp for guest users). No server synchronization, and no cross-device availability.

## UI/UX Specifications

### Visual Components
- [ ] Favorite toggle button: Heart icon overlay inside a semi-transparent light-gray circular chip on the top-right of each media poster card in lists
- [ ] Favorite icon states: Outlined heart (not favorited), filled heart (favorited)
- [ ] Bottom navigation bar: Two tabs labeled \"Movies\" and \"TV Shows\" (consistent with MediaCatalogScreen design) only show if favorites exist in both categories. Default tab is \"Movies\" unless it is empty.
- [ ] Feedback mechanism: Icon animation (fill/unfill) when toggling favorite status
- [ ] Empty state illustration: Centered message with optional icon

### Navigation
- [ ] Entry point: \"Favorites\" menu item in navigation drawer (heart icon + label \"Favorites\")
- [ ] Flow between screens: Drawer → FavoritesScreen → Bottom navigation switches between Movies/TV tabs
- [ ] Exit point: Back button or drawer menu selection returns to previous screen
- [ ] Session end: \"Close Session\" menu item in navigation drawer clears favorites when used by signed-in users

### States
- [ ] Empty state: Display centered message \"No favorites yet. Start adding your favorite movies and TV shows!\" when no items in current tab
- [ ] Loading state: Display circular progress indicator when retrieving favorites
- [ ] Error state: Show ErrorView with message \"Could not update favorite. Please try again.\"

## Accessibility
- [x] Screen reader labels for favorite toggle buttons: \"Add to favorites\" / \"Remove from favorites\"
- [x] Content descriptions for favorite status icons: \"Marked as favorite\" / \"Not marked as favorite\"
- [ ] Keyboard navigation support for bottom navigation tabs
- [ ] High contrast for favorite icon (sufficient contrast ratio for visibility)

## References
- `feature/media/favorites/screenshots/FavsHome.png`: Heart overlay placement/state on home media cards
- `feature/media/favorites/screenshots/FavsDrawer.png`: Navigation drawer entry label and session close item

## Success Criteria
- Users can successfully mark and unmark movies/TV shows as favorites with immediate visual feedback
- Favorites persist across app restarts during the session and are correctly displayed in the Favorites screen
- Bottom navigation correctly switches between Movies and TV Shows lists without data loss
- Empty state is properly displayed when no favorites exist in either category
- All favorites are automatically cleared when the session ends (logout for signed-in users, guest session expiration)

## Implementation Notes
This feature extends the existing MediaCatalogScreen with favorite toggle functionality and adds a new FavoritesScreen accessible via the navigation drawer. Session-based storage will be used with persistence across app restarts until the session ends.
"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Quick favorite toggles (Priority: P1)

As a user, I want to mark and unmark movies and TV shows as favorites from the main lists so I can quickly keep track of what I like.

**Why this priority**: It delivers the core value of saving preferred content with minimal effort.

**Independent Test**: Can be fully tested by toggling favorites on items in the main lists and seeing immediate status changes.

**Acceptance Scenarios**:

1. **Given** a movie item is visible in a main list and is not marked as favorite, **When** the user taps the heart icon on that movie, **Then** the icon shows the favorite state.
2. **Given** a movie item is visible in a main list and is marked as favorite, **When** the user taps the heart icon on that movie, **Then** the icon shows the non-favorite state.
3. **Given** a TV show item is visible in a main list and is not marked as favorite, **When** the user taps the heart icon on that TV show, **Then** the icon shows the favorite state.
4. **Given** a TV show item is visible in a main list and is marked as favorite, **When** the user taps the heart icon on that TV show, **Then** the icon shows the non-favorite state.

---

### User Story 2 - Browse favorites by category (Priority: P2)

As a user, I want the Favorites screen to show category tabs only when both Movies and TV Shows exist so I can focus on the content that is actually available.

**Why this priority**: It prevents empty navigation and makes favorites faster to browse.

**Independent Test**: Can be fully tested by opening Favorites with different mixes of favorite items and verifying tab visibility, default view, and category switching.

**Acceptance Scenarios**:

1. **Given** the user has at least one favorite movie and at least one favorite TV show, **When** the user selects "Favorites" in the navigation drawer, **Then** the Movies and TV Shows tabs are visible.
2. **Given** the user has at least one favorite movie and at least one favorite TV show, **When** the user selects "Favorites" in the navigation drawer, **Then** the Movies tab is selected by default.
3. **Given** the user is on the Favorites screen with Movies and TV Shows tabs visible and has at least one favorite TV show, **When** the user taps the "TV Shows" tab, **Then** the TV Shows favorites list is displayed.
4. **Given** the user has at least one favorite TV show and no favorite movies, **When** the user selects "Favorites" in the navigation drawer, **Then** the Movies and TV Shows tabs are not shown.

---

### User Story 3 - Session-based favorites lifecycle (Priority: P3)

As a user, I want favorites to persist across app restarts during my session so I can rely on them until I end my session.

**Why this priority**: It sets clear expectations for how long favorites last for signed-in and guest users.

**Independent Test**: Can be fully tested by restarting the app during a session, logging out, and opening Favorites after guest session expiration.

**Acceptance Scenarios**:

1. **Given** a signed-in user has favorites and the app has been restarted, **When** the user opens the Favorites screen, **Then** the favorites are shown.
2. **Given** a signed-in user has favorites, **When** the user selects "Log out" from the navigation drawer, **Then** the Favorites screen shows the empty state message.
3. **Given** the guest session has expired, **When** the user opens the Favorites screen, **Then** the empty state message "No favorites yet. Start adding your favorite movies and TV shows!" is displayed.

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- The user has favorites in only one category and should not see category tabs.
- The user has no favorite movies and opens Favorites, which should show TV Shows by default when available.
- The user logs out while having favorites, and Favorites should be empty afterward.

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: Users MUST be able to mark and unmark movies as favorites from main movie lists.
- **FR-002**: Users MUST be able to mark and unmark TV shows as favorites from main TV show lists.
- **FR-003**: The system MUST visually indicate favorite status on each media item card.
- **FR-004**: Users MUST be able to access a Favorites screen from the navigation drawer.
- **FR-005**: The Favorites screen MUST show Movies and TV Shows tabs only when favorites exist in both categories.
- **FR-006**: When Movies and TV Shows tabs are shown, the Movies tab MUST be selected by default unless it has no favorites.
- **FR-007**: Signed-in users' favorites MUST persist across app restarts until they log out.
- **FR-008**: Guest users' favorites MUST persist across app restarts until the guest session expires.
- **FR-009**: When a signed-in user logs out, favorites MUST be cleared.
- **FR-010**: The Favorites screen MUST display the specified empty state message when the selected view has no favorites.
- **FR-011**: The Favorites screen MUST show a loading indicator while favorites are being retrieved.
- **FR-012**: When a favorite update cannot be completed, the user MUST see the specified error message.
- **FR-013**: When Movies and TV Shows tabs are shown, users MUST be able to switch between the two categories.

*Example of marking unclear requirements:*

- None.

### Key Entities *(include if feature involves data)*

- **Favorite**: A saved marker that links a user session to a movie or TV show.
- **Media Item**: A movie or TV show that can be marked as favorite and shown in lists.
- **Session**: A single app usage period that defines how long favorites are available.

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: At least 90% of users can mark or unmark a favorite within 5 seconds on their first attempt.
- **SC-002**: In manual QA runs, 100% of signed-in users retain favorites after an app restart until they log out.
- **SC-003**: In manual QA runs, 100% of guest users see an empty Favorites screen after the guest session expires.
- **SC-004**: At least 95% of users successfully locate Favorites via the navigation drawer without assistance.
- **SC-005**: The empty state message appears in 100% of sessions when the selected view has no favorites.
