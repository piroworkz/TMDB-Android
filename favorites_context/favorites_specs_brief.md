# Favorites (Movies & TV Shows)

## Overview
Users can mark and unmark Movies or TV Shows as favorites directly from the media grid using a heart button. Favorites are available only during the active session and are grouped in a dedicated Favorites screen, allowing users to quickly access saved content without searching again.

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
- Favorites are stored **locally** and are valid only for the **current active session**.
- When the session ends:
    - Guest session expiration → favorites are cleared
    - Registered user logout → favorites are cleared
- Favorites do **not** sync to a server and are not required to persist across sessions.

## UI/UX Specifications

### Visual Components
- [ ] **Favorite Heart Button (Overlay on Media Card)**  
  Purpose: Toggle favorite state.  
  Appearance: Heart icon displayed in the **top-right corner** of each media card, using a subtle circular background to ensure contrast over posters.

- [ ] **Favorites Drawer Item**  
  Purpose: Entry point to Favorites screen.  
  Appearance: Drawer row labeled **"Favorites"** with a heart icon.

- [ ] **Favorites Filter Controls**  
  Purpose: Switch between favorite Movies and favorite TV Shows.  
  Appearance: Two pill/chip-style buttons:
    - "♡ FAVORITE MOVIES"
    - "♡ FAVORITE TV SHOWS"  
      The selected option is visually highlighted.

### Navigation
- [ ] **Entry point**: Navigation Drawer → **Favorites**
- [ ] **Flow between screens**:
    - Movies/TV Shows screens → toggle favorites from media cards
    - Favorites screen → browse favorites and optionally toggle/remove favorites
- [ ] **Exit point**: Back navigation returns to the previous screen

### States
- [ ] **Empty state**
    - If there are no favorites for the selected type, show a placeholder message:
        - "No favorite movies yet." / "No favorite TV shows yet."
    - Include a suggested action like:
        - "Browse and tap the heart to add favorites."

- [ ] **Loading state**
    - While favorites are loading, show a loading indicator (spinner or skeleton grid).

- [ ] **Error state**
    - If a favorite update fails, show a non-blocking error message (snackbar/toast) and ensure the UI reflects the correct state.
    - If favorites cannot be loaded, show an error placeholder with a retry action.

## Accessibility
- [ ] Heart button must include an accessible label that changes with state:
    - "Add to favorites" / "Remove from favorites"
- [ ] Ensure good contrast for the overlay controls over posters.
- [ ] Ensure tappable areas meet minimum touch target guidelines.
- [ ] Favorites filter controls are accessible and properly announced by screen readers.

## Success Criteria
- Users can favorite/unfavorite an item with one tap and see the change immediately.
- Favorites screen correctly displays favorites for the **current session**, separated by Movies vs TV Shows.
- Favorites are cleared correctly when the session ends:
    - Guest session expiration
    - Registered user logout
