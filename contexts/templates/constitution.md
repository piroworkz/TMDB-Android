# TMDB-Android Constitution
<!-- Project development constitution derived from PROJECT_CONTEXT.md -->

## Core Principles

### I. Clean Architecture Boundaries
UI depends on Domain; Framework depends on Domain; Domain has zero Android or data-framework dependencies. Keep layers independent and enforce unidirectional dependency flow.

### II. Modular Feature Structure
Features are split into `{feature}_domain`, `{feature}_framework`, and `{feature}_ui` modules. Module naming and registration in `settings.gradle.kts` are mandatory and must follow conventions.

### III. Test-First (Non-Negotiable)
Use TDD. Write or update tests before implementation; follow Red-Green-Refactor. Tests define interfaces, models, and contracts. Prefer unit tests for domain and ViewModels, and Compose UI tests in `src/androidTest`.

### IV. Type-Safe Errors
Use Arrow `Either<Error, Success>` for explicit error handling in domain use cases and repository flows. Prefer sealed error types and map failures explicitly.

### V. Convention-Driven Build
Use version catalog aliases and convention plugins. Do not hardcode versions in module files; add shared dependencies to convention plugins, and feature-specific ones to module Gradle files.

## Additional Constraints

- Kotlin-only codebase with coroutines and Flow/StateFlow for async and state.
- Jetpack Compose with Material 3; follow state hoisting and naming conventions for Composables and ViewModels.
- Hilt is the DI framework; use `@HiltViewModel`, `@Module`, and `@Binds/@Provides` patterns.
- Use `kotlinx.serialization` for type-safe navigation arguments.
- File organization: one public type per file; file name matches public declaration.

## Development Workflow & Quality Gates

- Verify layer boundaries (no Android in domain) before changes.
- Add or update tests first; follow established test patterns and fixtures.
- Apply dependency changes via `gradle/libs.versions.toml` and the appropriate convention plugin.
- Keep code style: 4-space indentation, <120 char soft limit, minimal meaningful comments, KDoc for public APIs.

## Governance
<!-- Constitution supersedes all other practices; amendments require documentation and approval -->

- All changes must comply with this constitution and PROJECT_CONTEXT.md.
- Reviews must check for clean architecture boundaries, TDD adherence, and dependency policy.
- Any deviation requires written justification and agreement with maintainers.
- Use `contexts/PROJECT_CONTEXT.md` as the authoritative runtime guidance.

**Version**: 1.0 | **Ratified**: 2025-12-29 | **Last Amended**: 2025-12-29
