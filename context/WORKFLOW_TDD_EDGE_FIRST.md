# WORKFLOW_TDD_EDGE_FIRST

## Purpose
Define the default, end-to-end TDD execution flow for full feature work. This is not mandatory for small refactors or minor bug fixes.

## When to apply
Use this workflow for complete features that span data/domain/UI layers.

## TDD Edge-First Flow (default)
1) **Repository (data edge)**
   - Start with repository unit tests using mocks.
   - If applicable, define behavior against the remote data source first.
   - Implement the minimum production code to pass tests.

2) **Local data source**
   - Add/update local data source behavior after remote behavior is covered.
   - Tests stay unit-level with mocks (Retrofit service interfaces / Room DAO interfaces).

3) **Move production code into architecture hierarchy**
   - After behavior is validated, place production code in final package/module locations per Clean Architecture.

4) **Domain contracts + modeling**
   - Define domain interfaces (use case contracts) and entities/value objects.
   - No tests required for pure interfaces/entities unless they contain behavior.

5) **Presentation (UI layer)**
   - Implement ViewModel and UI state mapping with unit tests.
   - Add Compose unit tests to drive UI creation and state rendering.
   - Instrumented tests only if explicitly requested.

6) **Integration (final phase)**
   - Integration tests are JVM tests (not instrumented) and use spies to wire multiple components.
   - Use them to validate behavior across layers (still not full E2E/UI).

## Notes
- Always follow Red → Green → Refactor for behavioral changes.
- Avoid adding dependencies without explicit approval.
- Keep changes within the feature scope defined by the spec.
- When implementing code or tests, follow the repo skills:
  - `context/skills/skill-android-tdd` for edge-first TDD and boundaries.
  - `context/skills/skill-unit-tests` for unit tests with MockK.
  - `context/skills/skill-integration-tests` when integration tests are included.
