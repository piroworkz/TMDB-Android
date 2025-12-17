# Workflow: Create a New Feature

Quick checklist

- [ ] Ask user for the branch name (agent must not create a branch until the user confirms the name).
- [ ] Create a new branch from `master` using the confirmed name.
- [ ] Ask user whether they will provide UI references (images, screenshots, Figma, design files). If yes, require using them to generate the UI.
- [ ] Plan feature (ticket, scope, contracts, API surface).
- [ ] Implement modules: `feature/<name>_domain`, `feature/<name>_framework`, `feature/<name>_ui`.
- [ ] Wire DI (Hilt) and module boundaries.
- [ ] Implement ViewModels, UseCases, Repositories.
- [ ] UI implementation (Compose) using references when provided.
- [ ] Tests: unit + UI tests.
- [ ] Build, run, and create PR with checklist.
- [ ] Follow TDD: write failing tests before implementing feature code and iterate (red → green → refactor).

Overview

This document defines the step-by-step workflow the agent will follow to add a new feature to the TMDB-Android project. It is written to match repository conventions (modularized features split into `_domain`, `_framework`, and `_ui` modules, shared `build-logic` convention plugins, and the `libs` versions catalog). The agent must always ask for two pieces of information before making changes that affect VCS or code generation:

1. Branch name (required) — the agent will not create a branch until the user provides and confirms the branch name.
2. UI references (optional) — the agent will ask whether the user will provide design references (images, screenshots, Figma links, design tokens). If provided, the agent must use them to generate the UI (extract assets, design tokens, spacing/color measurements, and accessibility guidance).

Repository conventions referenced

- Feature modules follow a 3-way split: `*_domain`, `*_framework`, `*_ui`.
- Shared convention plugins live in `build-logic/convention` and should be applied to new modules where appropriate (e.g., `tmdb.kotlin.module.plugin`, `tmdb.framework.module.plugin`, `tmdb.ui.module.plugin`).
- Libraries and versions come from `gradle/libs.versions.toml` and are referenced through `libs` in Gradle.

Example minimal command list

- Create branch (after user provides name):

```bash
git fetch origin
git checkout master
git pull --ff-only origin master
git checkout -b <branch-name> master
```

- Build project:

```bash
./gradlew build
```

- Run unit tests:

```bash
./gradlew test
```

- Run UI/instrumented tests (project has `connectedUiTests` aggregator):

```bash
./gradlew connectedUiTests
```

Detailed ordered workflow

1) Plan (define scope & contracts)

- Confirm ticket/issue ID, acceptance criteria, and API/back-end changes (if any).
- Define the public contract between modules:
  - Domain: business models and use-case interfaces.
  - Framework: repository implementations, data sources, network, local DB.
  - UI: composables, ViewModels, navigation integration.
- If a backend API is required, sketch the request/response DTOs and error cases.
- Decide whether new third-party libraries are needed. If yes, plan to add entries to `gradle/libs.versions.toml` and reference them via the `libs` catalog.
- Adopt a TDD-first approach during planning: identify the behavior to test (unit tests for domain/use-cases first), write failing tests that capture acceptance criteria, then implement minimal code to satisfy tests. This keeps the domain API small and well-specified.

2) Branch creation (agent must ask user first)

- Always ask: "What branch name should I create from `master` for this feature?" Pause and wait for user confirmation.
- Use a branch naming convention. Examples:
  - feat/ABC-123-add-login
  - feat/media/ABC-456-feature-name
  - chore/update-xyz
- Once the user provides the branch name, run the commands shown in "Example minimal command list" (create branch from `master`).

3) Ask for UI references

- Prompt the user: "Will you provide any design references (images, screenshots, Figma, Sketch, or other)? If yes, please attach or provide links now." 
- If the user says no, proceed and implement a reasonable default UI following the app's design language (Material 3 + design tokens from `build-logic` or existing modules).
- If the user provides references, the agent must:
  - Download or accept the provided assets (PNG/SVG, Figma link, PDF, etc.).
  - Extract or document design tokens: colors, typography, spacing, corner radii, iconography.
  - Extract images and icons as appropriately sized assets (prefer SVG where possible). Add them to `*_ui/src/main/res/drawable` or as Compose vector assets.
  - Measure layout spacing and create concrete Compose modifiers (padding, sizes) matching the design.
  - Create accessible labels and semantic descriptions for images and tappable elements.
  - Produce Composable previews and story-style examples (if the repository uses them). Include a short design rationale in the commit/PR.

4) Create modules

- Create three modules under `feature/`:
  - `feature/<name>_domain`
  - `feature/<name>_framework`
  - `feature/<name>_ui`
- Each module should have a `build.gradle.kts` (or follow the repo-level convention used in other modules). Apply the appropriate convention plugin from `build-logic`:
  - Domain: `tmdb.kotlin.module.plugin` or similar lightweight plugin.
  - Framework: `tmdb.framework.module.plugin`.
  - UI: `tmdb.ui.module.plugin`.
- Update `settings.gradle.kts` to include the new modules. Follow existing naming and path conventions.
- Add `android.namespace` values consistent with `com.davidluna.tmdb.<module_suffix>` via the project's Constants or module configuration.

5) Define domain interfaces & models

- In `*_domain`:
  - Define data models (pure Kotlin DTOs) and domain-facing interfaces (repository interfaces, use cases).
  - Keep this module free of Android or framework dependencies.
  - Add unit tests for use cases and domain logic.

6) Implement framework details

- In `*_framework`:
  - Implement repository interfaces from domain.
  - Add network DTOs and mappers between network/local models and domain models.
  - Add Hilt modules and bindings (`@Module`, `@InstallIn(SingletonComponent::class)` / `@Provides` / `@Binds`) to expose repository implementations to the app.
  - If Room is needed, add entities/DAOs here and expose repositories.
  - Add integration tests (where appropriate) using `mockwebserver` for network behaviour or in-memory Room DB for persistence.

7) UI & ViewModel

- In `*_ui`:
  - Create `Composable` functions for screens, cards, lists, and reusable components.
  - Create a `@HiltViewModel`-annotated ViewModel to orchestrate state. Expose UI state via `StateFlow` or `LiveData` (prefer `StateFlow` + `UiState` sealed classes/data classes).
  - Ensure ViewModel depends only on `*_domain` interfaces (not framework implementations).
  - Add navigation route constants and integrate with the app-level nav graph where needed (use `hilt-navigation-compose` if present).
  - Add Compose Previews for components and screens.
  - Use provided references (if any) to match visual design and accessibility requirements.

8) Tests

- Unit tests:
  - Test domain use-cases and ViewModel logic using `kotlinx-coroutines-test`, `mockk`, or chosen libs.
  - Use `test_shared` utilities where applicable.
- UI tests:
  - Create Compose UI tests in `*_ui` using Compose testing APIs and `HiltAndroidRule` for injection.
  - If instrumented tests are needed, use `connectedUiTests` aggregator or `./gradlew :<module>:connectedAndroidTest`.
- Add small smoke tests for DI wiring (e.g., ensure repository binding resolves) if sensible.
 
TDD workflow (applies to unit and UI where practical):
- Write a failing test that encodes one small piece of behavior (start with domain/use-case tests).
- Run tests and see the failure (red).
- Implement the minimal amount of production code to make the test pass (green).
- Refactor for clarity and maintainability while keeping tests green.
- Add additional tests incrementally to expand behavior coverage.
- For UI, prefer unit tests for state and logic; use small Compose tests for important visual/interaction invariants and drive UI from tested ViewModel behavior.

9) Local build & manual QA

- Build the project: `./gradlew build`.
- Run unit tests: `./gradlew test`.
- Install debug APK to a device or emulator: `./gradlew :app:installDebug` and manually navigate to the new feature.
- Run UI tests: `./gradlew connectedUiTests`.

10) Commit messages & branches

- Follow a conventional commit-style template. Examples:
  - feat(<module>): add login screen and ViewModel
  - fix(<module>): handle null response for user profile
  - chore(deps): bump retrofit to 2.x
- Keep commits small and focused. Prefer one commit per logical change (module creation, domain API, framework implementation, UI layout, tests).

11) PR creation & checklist

- Create PR from the feature branch into `master`.
- PR title example: `feat(media): add movie-details feature (ABC-123)`.
- PR description should include:
  - Short summary of the change.
  - Screenshots or screen recordings (if UI changed) — attach the design references used.
  - Testing notes and steps to run locally.
  - Any backend/API changes required.
  - Checklist:
    - [ ] Branch created from `master` and up-to-date
    - [ ] Tests added and passing
    - [ ] DI bindings and module boundaries validated
    - [ ] No secrets or API keys committed
    - [ ] Design references used (if provided)
    - [ ] Accessibility checks (content descriptions, focus order)

12) Code review notes

- Verify module boundaries — domain should not depend on framework or Android.
- Verify Hilt modules and scoping are correct.
- Verify no hardcoded keys or secrets.
- Verify composables follow consistent patterns with existing UI modules.
- Verify tests cover edge cases and happy paths.

13) Release notes & changelog

- Add a short changelog entry (or update the project release notes) with the feature summary. Include API changes and migration notes if necessary.

Examples & templates

Branch name examples

- feat/ABC-123-add-login
- feat/media/ABC-456-movie-details
- chore/update-deps

Commit message templates

- feat(<module>): short description
- fix(<module>): short description
- test(<module>): add tests for X

PR template (summary to copy into PR body)

- Title: feat(<scope>): short title (ISSUE-ID)
- Description:
  - Summary:
  - Screenshots / attachments:
  - How to test (local steps):
  - Notes (back-end changes / migration / performance):
- Checklist:
  - [ ] Code builds cleanly
  - [ ] Unit tests pass
  - [ ] UI tests pass
  - [ ] Design references used (if provided)
  - [ ] Accessibility verified

Edge cases & notes

- If new libraries are added, update `gradle/libs.versions.toml` and ask a reviewer to validate the addition.
- Avoid checking in API keys or `google-services.json` replacements — use `local.properties` or CI secrets.
- If a new feature requires significant navigation changes, coordinate with the owner of the app-level navigation graph.

Next steps the agent will ask the user (before making changes)

1. What branch name should I create from `master` for this work? (required)
2. Will you provide design references (images, screenshots, Figma link)? If yes, please attach or paste them now. 

Once the user answers both, the agent will proceed to create the branch and scaffold the modules and code following this workflow.
