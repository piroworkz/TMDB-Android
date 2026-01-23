# Feature Specification: favorites

**Feature Branch**: `feature/favorites`  
**Created**: 2026-01-16  
**Status**: Draft  
**Input**: User description: "# Favorites (Movies & TV Shows)

## Overview
Users can quickly save Movies or TV Shows while browsing by tapping a heart on the media card. Saved items appear in a dedicated Favorites screen during the current session, so users can return to what they care about without re-searching.

### User-centered What / Why
- **What**: Let users add or remove favorites from any media card and view them in one place, separated by Movies and TV Shows.
- **Why**: Users want a fast, low-effort way to keep track of items they like during a session so they can continue watching or decide later without losing their place.

## Functional Requirements

### Core Features
- [ ] Users can **toggle favorite status** for a Movie or TV Show by tapping the **heart icon** on a media card.
- [ ] The heart icon must **reflect the current favorite state**:
    - Filled/active when the item is a favorite
    - Outline/inactive when it is not a favorite
- [ ] Favorite changes must be **visible immediately** in the UI after toggling.
- [ ] The app provides a **Favorites screen** that displays all favorited items from the **current session**.
- [ ] Favorites are **separated by media type**:
    - Favorite Movies list
    - Favorite TV Shows list
- [ ] Users can **remove an item from favorites** from:
    - The original grid/card where the item appears
    - The Favorites screen grid/card
- [ ] Favorites are **session-scoped** and are **cleared when the session ends** (regardless of the session type).

### User Scope
This feature is available to:
- **Guest users** (session expires automatically)
- **Registered users** (session ends on explicit logout)

### Data Management
- Favorites are stored **locally** and are valid only for the **current active session**, so users are never surprised by stale lists.
- When the session ends:
    - Guest session expiration → favorites are cleared
    - Registered user logout → favorites are cleared
- Favorites do **not** sync to a server and are not required to persist across sessions.

## UI/UX Specifications

### Visual Components
- [ ] **Favorite Heart Button (Overlay on Media Card)**  
  User need: Save or remove an item without leaving the grid.  
  Appearance: Heart icon displayed in the **top-right corner** of each media card, using a subtle circular background to ensure contrast over posters.

- [ ] **Favorites Drawer Item**  
  User need: Reach saved items in one tap.  
  Appearance: Drawer row labeled **"Favorites"** with a heart icon.

- [ ] **Favorites Filter Controls**  
  User need: Focus on Movies or TV Shows without extra searching.  
  Appearance: Two pill/chip-style buttons:
    - "♡ FAVORITE MOVIES"
    - "♡ FAVORITE TV SHOWS"  
      The selected option is visually highlighted.

### Navigation
- [ ] **Entry point**: Navigation Drawer → **Favorites** so users can access saved items from anywhere.
- [ ] **Flow between screens**:
    - Movies/TV Shows screens → toggle favorites from media cards to save in place.
    - Favorites screen → browse favorites and optionally toggle/remove favorites to keep the list accurate.
- [ ] **Exit point**: Back navigation returns to the previous screen to preserve user context.

### States
- [ ] **Empty state**
    - If there are no favorites for the selected type, show a placeholder message:
        - "No favorite movies yet." / "No favorite TV shows yet."
    - Include a suggested action like:
        - "Browse and tap the heart to add favorites." so users know what to do next.

- [ ] **Loading state**
    - While favorites are loading, show a loading indicator (spinner or skeleton grid) so users know the list is on its way.

- [ ] **Error state**
    - If a favorite update fails, show a non-blocking error message (snackbar/toast) and ensure the UI reflects the correct state so users trust the list.
    - If favorites cannot be loaded, show an error placeholder with a retry action so users can recover quickly.

## Accessibility
- [ ] Heart button must include an accessible label that changes with state:
    - "Add to favorites" / "Remove from favorites"
- [ ] Ensure good contrast for the overlay controls over posters so controls are easy to spot.
- [ ] Ensure tappable areas meet minimum touch target guidelines so actions are easy to perform.
- [ ] Favorites filter controls are accessible and properly announced by screen readers so users can switch lists confidently.

## Success Criteria
- Users can favorite/unfavorite an item with one tap and see the change immediately.
- Users can access a Favorites screen that reflects only the **current session**, separated by Movies vs TV Shows.
- Users are not surprised by stale lists because favorites clear when the session ends:
    - Guest session expiration
    - Registered user logout

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

### User Story 1 - Toggle favorites from media cards (Priority: P1)

As a user browsing Movies or TV Shows, I want to mark or unmark an item as a favorite from the media grid so I can save what matters without breaking my flow.

**Why this priority**: This is the core action that enables favorites and delivers immediate user value.

**Independent Test**: Can be fully tested by tapping the heart icon on a media card and observing the favorite state change and list update.

**Acceptance Scenarios**:

1. **Given** a media card shows a non-favorited item, **When** the user taps the heart icon, **Then** the heart shows the favorited state and the item appears in the Favorites list for its media type.
2. **Given** a media card shows a favorited item, **When** the user taps the heart icon, **Then** the heart shows the non-favorited state and the item no longer appears in the Favorites list for its media type.

---

### User Story 2 - View favorites by media type (Priority: P2)

As a user, I want to open a Favorites screen and switch between Movies and TV Shows so I can quickly find saved items without extra searching.

**Why this priority**: Users need a dedicated place to access saved items without searching again.

**Independent Test**: Can be fully tested by entering the Favorites screen and switching the media-type filter to see the corresponding list.

**Acceptance Scenarios**:

1. **Given** the navigation drawer is open, **When** the user taps the Favorites entry, **Then** the Favorites screen is displayed.
2. **Given** the Favorites screen shows favorite movies and favorite TV shows exist, **When** the user taps the "♡ FAVORITE TV SHOWS" filter, **Then** the Favorites screen shows the TV show favorites list.

---

### User Story 3 - Remove favorites from the Favorites screen (Priority: P3)

As a user, I want to remove an item from favorites while browsing the Favorites screen so I can keep my saved list accurate and relevant.

**Why this priority**: Removing items from the saved list maintains relevance and trust in the feature.

**Independent Test**: Can be fully tested by tapping the heart icon on a favorites card and verifying the item is removed from the list.

**Acceptance Scenarios**:

1. **Given** the Favorites screen shows a favorited item, **When** the user taps the heart icon on that item, **Then** the item is removed from the Favorites list.

---

### Edge Cases

- The selected filter has no favorites; the empty state message and suggested action are shown.
- The user removes the last remaining favorite item; the list updates to the empty state.
- A favorite update fails; the user sees a non-blocking error message and the visible state remains accurate.
- The session ends while the Favorites screen is open; favorites are cleared and the empty state is shown.

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: The system MUST allow users to mark a Movie or TV Show as a favorite from a media card.
- **FR-002**: The system MUST allow users to unmark a favorite from a media card.
- **FR-003**: The system MUST show a Favorites screen that lists favorited items from the current session.
- **FR-004**: The system MUST separate favorite Movies and favorite TV Shows into distinct lists.
- **FR-005**: The system MUST allow users to remove a favorite from the Favorites screen.
- **FR-006**: The system MUST clear favorites when the active session ends for guest or registered users.
- **FR-007**: The system MUST reflect favorite state changes immediately in the UI.

*Example of marking unclear requirements:*

- None at this time.

### Key Entities *(include if feature involves data)*

- **Favorite Item**: A Movie or TV Show the user has marked as a favorite.
- **Favorites List**: The collection of a user's favorited items, separated by Movies and TV Shows.
- **Session**: The user's active session that determines the lifetime of favorites.

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: 95% of users can add or remove a favorite from a media card on the first attempt without assistance.
- **SC-002**: 90% of users can find a saved item by opening the Favorites screen within 10 seconds.
- **SC-003**: 99% of favorites are cleared for users whose session ends, as verified in session-end checks.
- **SC-004**: 90% of users can switch between Movies and TV Shows in Favorites and see the correct list on the first try.
