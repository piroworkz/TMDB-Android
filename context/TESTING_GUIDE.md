# TESTING_GUIDE.md

All mandatory testing rules are defined in `context/CONSTITUTION.md`.

## Purpose
This document captures the testing conventions and patterns used in this repository.
It is descriptive (how tests are written today), not prescriptive beyond the
rules defined in `context/CONSTITUTION.md`.

---

## 1. Test Types and Intent
Use the existing patterns in each layer/module and prefer adding tests that
match the style already present in the codebase.

### Unit tests (JVM)
- Focus on a single unit (use case, view model, repository behavior).
- Use mocks for dependencies to isolate the unit.
- Verify state updates and error handling explicitly.
- Example style: Given/When/Then naming and explicit side‑effect checks.

### Integration tests (JVM)
- Use spy implementations to exercise multiple collaborators.
- Prefer real implementations where possible, with spies controlling failure paths.
- Verify state updates, error propagation, and interaction flows.

### Compose UI tests (instrumented)
- **Isolation tests**: individual composables (inputs, buttons, simple components).
- **Integration tests**: screen-level behavior (user flows, event dispatching, error
  and loading UI states).

---

## 2. Naming and Structure
- Use descriptive test names with a GIVEN/WHEN/THEN structure.
- Prefer short, focused tests over large scenario tests.
- Keep setup minimal; extract only what is repeated.

---

## 3. Common Tooling (Observed)
- **Mocks**: MockK
- **Flows**: Turbine for state/flow testing
- **Coroutines**: `CoroutineTestRule` + `runTest`
- **Compose UI**: `createComposeRule()` and standard Compose testing APIs
- **Shared fixtures**: Gradle `testFixtures` to share spies and fakes across modules/layers

---

## 4. Behavioral Coverage Expectations
When changing or adding code, tests should cover:
- Happy path
- Edge cases (invalid input, empty states)
- Error paths (exceptions, API failures, database errors)

For UI (Compose) tests:
- Initial render state
- Loading state
- Error state
- User interactions and event dispatch

---

## 5. Practical Guidance
- Keep tests close to the module under test (`src/test` or `src/androidTest`).
- For Compose UI, prefer isolated composable tests before screen integration tests,
  mirroring the existing pattern in the auth feature.
- Maintain parity with the existing testing style and helper usage in each module.
- When a test helper is needed across modules (e.g., spies or fake values), prefer
  placing it in a module's `testFixtures` and reusing it from dependents.

---

## 6. Reference Examples (Existing Tests)
Use these as style references when writing tests (do not duplicate them):
- `feature/auth/auth_data/src/test/kotlin/com/davidluna/tmdb/auth_data/repositories/OpenSessionTests.kt`
- `feature/auth/auth_ui/src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginViewModelTest.kt`
- `feature/auth/auth_ui/src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginIntegrationTest.kt`
- `feature/auth/auth_ui/src/androidTest/kotlin/com/davidluna/tmdb/auth_ui/view/login/composables/PasswordTextFieldTest.kt`
- `feature/auth/auth_ui/src/androidTest/kotlin/com/davidluna/tmdb/auth_ui/view/login/LoginScreenTest.kt`
