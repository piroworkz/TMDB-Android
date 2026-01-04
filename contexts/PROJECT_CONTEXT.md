# PROJECT_CONTEXT.md

**Project Name:** TMDB-Android  
**Language:** Kotlin  
**Platform:** Android  
**Minimum API:** TBD (check `build-logic`)  
**Target API:** TBD (check `build-logic`)

---

## How to Use This Doc
- Scan the TOC, jump to the section you need.
- Follow naming in §3; follow patterns in §7.
- Build/test commands live in §6/§7 for quick copy/paste.

## Table of Contents
1. Project Overview
2. Architecture & Principles
3. Naming Conventions
4. Code Style & Compose
5. Dependencies & Stack
6. Testing Strategy
7. Build & Tooling
8. Implementation Patterns
9. Practices & Guidance
10. Glossary
11. Quick Links

---

## 1. Project Overview

The TMDB-Android project follows a **multi-module, clean architecture** pattern with clear separation of concerns. The modular structure is organized as follows:

```
TMDB-Android/
├── app/                          # Main application module
├── feature/                      # Feature modules
│   ├── auth/
│   │   ├── auth_domain/          # Business logic (pure Kotlin)
│   │   ├── auth_framework/       # Data layer (Room, Retrofit, etc.)
│   │   └── auth_ui/              # UI layer (Jetpack Compose)
│   ├── media/
│   │   ├── media_domain/
│   │   ├── media_framework/
│   │   └── media_ui/
│   └── core/
│       ├── core_domain/
│       ├── core_framework/
│       └── core_ui/
├── build-logic/                  # Convention plugins for Gradle
│   └── convention/
├── test_shared/                  # Shared testing utilities and mocks
├── gradle/                       # Gradle wrapper and version catalog
├── contexts/                     # Documentation and context files
└── settings.gradle.kts           # Gradle settings with module inclusions
```

### Module Types

- **Domain (`*_domain`)**: Pure Kotlin modules containing business rules, use case interfaces, entities, and value objects. Zero Android framework dependencies.
- **Framework (`*_framework`)**: Android framework implementations including data sources, repositories, Room databases, Retrofit services, and persistence logic.
- **UI (`*_ui`)**: Jetpack Compose UI implementations, ViewModels, screen composables, and navigation.
- **Core**: Reusable components shared across all features (utilities, common UI components).

### Module Registry

Modules are registered in `settings.gradle.kts`:

```kotlin
include(
    ":app",
    ":feature:auth:auth_ui",
    ":feature:auth:auth_domain",
    ":feature:auth:auth_framework",
    ":feature:media:media_ui",
    ":feature:media:media_domain",
    ":feature:media:media_framework",
    ":feature:core:core_ui",
    ":feature:core:core_domain",
    ":feature:core:core_framework",
    ":test_shared"
)
```

---

## 2. Architecture & Principles

### 2.1 Clean Architecture

1. **Domain Layer** (`*_domain`)
   - Business rules and pure Kotlin use cases
   - Entities, value objects, and use case interfaces
   - Use cases are interfaces (prefer `fun interface` when a single public method exists)
   - Uses `Arrow`'s `Either<Error, Success>` for type-safe error handling
   - No dependencies on Android or data-specific frameworks

2. **Framework Layer** (`*_framework`)
   - Local and remote data sources
   - Repositories implement use case interfaces from domain (no `Impl` suffix)
   - DataSources handle specific data operations (local DB, remote API)
   - Handles data persistence (Room) and network requests (Retrofit)
   - Depends on Domain; uses dependency injection (Hilt)

3. **UI Layer** (`*_ui`)
   - Jetpack Compose UI and presentation (screens, components, ViewModels)
   - State management via `StateFlow`
   - Depends on Domain for use cases

### 2.2 Key Design Principles

- **Unidirectional Dependency**: UI → Domain ← Framework (domain is independent)
- **TDD First**: Add/update tests before changes; use Red-Green-Refactor as the mandatory loop for behavior changes
- **Prefer composition** over inheritance; avoid base classes
- **Type-Safe Error Handling**: `Arrow`'s `Either<L, R>` for explicit error propagation
- **Reactive Programming**: Coroutines and Flow for asynchronous operations
- **Dependency Injection**: Hilt for object lifecycle and dependency management
- **Type-Safe Navigation**: `kotlinx.serialization` for passing complex arguments in Compose navigation

---

## 3. Naming Conventions

### 3.1 General
- **Classes**: PascalCase (`UserRepository`, `LoginScreen`)
- **Functions & Variables**: camelCase (`getUserById()`, `isLoading`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_ATTEMPTS`)
- **Packages**: lowercase with dots (`com.davidluna.tmdb.auth.domain`)
- **Composables**: PascalCase, often suffixed with `Screen` or `Dialog` (`LoginScreen`, `UserDialog`)
- **ViewModels**: Suffixed with `ViewModel` (`LoginViewModel`)

### 3.2 Layer-Specific Names (Framework/UI)
- **Repositories**: `{Feature}Repository` (e.g., `FavoritesRepository`, `MediaRepository`); no `Impl` suffix.
- **Data Sources**: `{Source}{Feature}DataSource` (e.g., `RemoteMediaDataSource`, `LocalFavoritesDataSource`); no `Impl` suffix.
- **Use Cases**: Verb-first when possible (`ToggleFavorite`, `GetMovies`); no `UseCase` suffix..

### 3.3 Test Doubles
- **Fake Values**: Prefix with `fake` (`fakeMedia`, `fakeUser`, `fakeMovieFavorite()`).
- **Spies**: Suffix with `Spy` (`FavoritesRepositorySpy`, `MediaDaoSpy`).
- Avoid `FakeRepository` / `MockRepository`; prefer `{Name}Spy`.

### 3.4 Modules
- Domain modules: `{feature}_domain`
- Framework modules: `{feature}_framework`
- UI modules: `{feature}_ui`
- Core modules: `core_{layer}`

---

## 4. Code Style & Compose

### 4.1 Language & Idioms
- Kotlin-only; leverage null-safety.
- Coroutines for async work; Flow/StateFlow for streams.
- Extension functions for utilities; sealed classes for ADTs.

### 4.2 Formatting & Organization
- Indentation: 4 spaces; line length ~120 chars (soft).
- One public class/interface per file; file name matches declaration.
- Organized imports; sparing `@Suppress` with rationale.
- No comments (inline/block/KDoc); code must be self-explanatory.

### 4.3 Compose Conventions
- Preview functions suffixed with `Preview` and annotated with `@Preview`.
- State hoisting to lowest common parent.
- `remember`/`mutableStateOf` for local state; `StateFlow` for shared state.
- Composables describe UI only; move logic/state into a StateHolder (ViewModel or state holder class).
- Material 3 components with Color and Shape system.

---

## 5. Dependencies & Stack

### 5.1 Gradle & Build Tools

| Component                   | Version      | Details                           |
|-----------------------------|--------------|-----------------------------------|
| AGP (Android Gradle Plugin) | 8.13.2       | Official Android build system     |
| Kotlin                      | 2.2.21       | Language and stdlib               |
| KSP                         | 2.2.21-2.0.4 | Symbol processing for annotations |
| Room                        | 2.8.4        | ORM database framework            |

### 5.2 UI & Jetpack

| Library             | Version          | Purpose                      |
|---------------------|------------------|------------------------------|
| Jetpack Compose BOM | 2025.12.00       | UI toolkit                   |
| Material 3          | Latest (via BOM) | Design system                |
| Navigation Compose  | 2.9.6            | Type-safe navigation         |
| Activity Compose    | 1.12.1           | Activity-Compose integration |
| Coil Compose        | 2.7.0            | Image loading library        |

### 5.3 Data & Networking

| Library               | Version | Purpose                |
|-----------------------|---------|------------------------|
| Retrofit              | 3.0.0   | HTTP client & REST API |
| OkHttp                | 5.3.2   | HTTP networking        |
| Kotlinx Serialization | 1.9.0   | JSON serialization     |
| Room (Database)       | 2.8.4   | Local persistence      |
| DataStore             | 1.2.0   | Secure preferences     |

### 5.4 Dependency Injection & DI Frameworks

| Library                 | Version | Purpose                        |
|-------------------------|---------|--------------------------------|
| Hilt                    | 2.57.2  | Dependency injection           |
| Hilt Navigation Compose | 1.3.0   | ViewModel injection in Compose |
| javax.inject            | 1       | Injection annotations          |

### 5.5 Functional & Error Handling

| Library | Version | Purpose                                 |
|---------|---------|-----------------------------------------|
| Arrow   | 2.2.0   | Functional programming (Either, Option) |

### 5.6 Asynchronous Programming

| Library            | Version | Purpose              |
|--------------------|---------|----------------------|
| Kotlinx Coroutines | 1.10.2  | Async/await and Flow |
| Kotlinx DateTime   | 0.7.1   | Date/time operations |

### 5.7 Testing

| Library              | Version          | Scope       | Purpose                     |
|----------------------|------------------|-------------|-----------------------------|
| JUnit                | 4.13.2           | Unit        | Test framework              |
| MockK                | 1.14.7           | Unit        | Mocking library             |
| Turbine              | 1.2.1            | Unit        | Flow testing                |
| Coroutines Test      | 1.10.2           | Unit        | Coroutine testing utilities |
| Compose Runtime Test | 1.10.0           | Unit        | Compose state testing       |
| Paging Testing       | 3.3.6            | Unit        | Paging library testing      |
| Navigation Testing   | 2.9.6            | Integration | Navigation testing          |
| Hilt Android Testing | 2.57.2           | Integration | Hilt test support           |
| MockWebServer        | 5.3.2            | Integration | Mock HTTP server            |
| Espresso             | 3.7.0            | UI          | UI testing framework        |
| Compose UI Test      | Latest (via BOM) | UI          | Compose UI testing          |
| Android Test Core    | 1.7.0            | UI          | Core test utilities         |
| Android Runner       | 1.7.0            | UI          | Test runner                 |

### 5.8 Other Dependencies

| Library                | Version       | Purpose                |
|------------------------|---------------|------------------------|
| Core Splashscreen      | 1.2.0         | Splash screen API      |
| Biometric              | 1.2.0-alpha05 | Fingerprint/Face auth  |
| Play Services Location | 21.3.0        | Location services      |
| Firebase BOM           | 34.7.0        | Firebase suite         |
| Google Services Plugin | 4.4.4         | Firebase configuration |

### 5.9 Version Catalog

All dependencies are managed in `gradle/libs.versions.toml` for consistency and IDE completion. Use `alias(libs.*)` in Gradle files.

---

## 6. Testing Strategy

### 6.1 Approach
- **TDD for behavior**: Write/update tests before implementing code with logic or state transformations (ViewModels, use case implementations, repositories, data sources, complex Composables). Red-Green-Refactor is required for behavioral changes.
- **No tests for structure**: Pure data models (entities, DTOs, value objects), use case interfaces (contracts only), DI configuration, constants/enums, and exploration/inventory tasks do not require tests.
- Choose test type based on scope: unit for isolation, integration for flows, Compose UI for rendering/interaction, E2E for happy paths.

### 6.2 Unit Tests
- ViewModels: verify state reducers, side-effects, navigation triggers.
- Repositories: assert contracts with data sources and error propagation.
- Data sources: validate mapping and persistence behaviors.
- Compose components (unit style): semantics and callbacks in `src/androidTest`.

Key patterns:
- Use `@MockK` for dependencies; `MockKRule(order = 0)` and `CoroutineTestRule(order = 1)`.
- `coEvery`/`every` for setup; `coVerify`/`verify` for assertions.
- GIVEN-WHEN-THEN structure; use `runTest` for suspend code.

### 6.3 Integration Tests
- Real implementations + spies from `testFixtures`; avoid MockK inside the flow.
- Good for ViewModel + Use Case + Repository stacks and persistence.

### 6.4 Compose UI Tests (`src/androidTest`)
- Test Composables directly; pass `uiState` and callbacks.
- Use semantic matchers (`onNodeWithText`, `onNodeWithContentDescription`).

### 6.5 Test Doubles
- **Unit**: mocks only (`@MockK`).
- **Integration**: spies/fakes from `testFixtures`.
- **Compose UI**: mock ViewModel, real Composables.

### 6.6 Quick Reference

| Test Type | Location | Framework | Pattern | Coverage |
|-----------|----------|-----------|---------|----------|
| Unit - Domain | `src/test/` | JUnit4 + MockK + Turbine | `@MockK` dependencies | ≥85% |
| Unit - Framework | `src/test/` | JUnit4 + MockK | `@MockK` DAOs | ≥80% |
| Unit - ViewModel | `src/test/` | JUnit4 + MockK + Turbine | `@MockK` use cases | ≥70% |
| Integration - ViewModel | `src/test/` | JUnit4 + Spies | Real impl + spies | Optional |
| UI - Compose | `src/androidTest/` | Compose Test + MockK | Mock VM, semantic checks | ≥60% |
| E2E | `app/src/androidTest/` | Hilt + MockWebServer | Happy paths | Selective |

---

## 7. Build & Tooling

### 7.1 Convention Plugins

| Plugin                           | Apply Method                      | Purpose                                     |
|----------------------------------|-----------------------------------|---------------------------------------------|
| **tmdb.android.application**     | `:app` only                       | Configures the main application module      |
| **tmdb.ui.module.plugin**        | UI modules (`*_ui`)               | Applies Compose, Hilt, testing dependencies |
| **tmdb.framework.module.plugin** | Framework modules (`*_framework`) | Applies Retrofit, Hilt, Room, testing setup |
| **tmdb.kotlin.module.plugin**    | Domain modules (`*_domain`)       | Pure Kotlin setup with testing              |
| **tmdb.room.module.plugin**      | Framework modules with Room       | Room ORM configuration                      |
| **tmdb.test.shared.plugin**      | `test_shared` module              | Testing utilities and mock setup            |

Apply via `alias(libs.plugins.*)` inside module `*.gradle.kts` files.

### 7.2 Build Commands

```bash
./gradlew build                    # Build entire project (debug)
./gradlew :app:build               # Build app module
./gradlew :feature:auth:auth_ui:build  # Build a feature module
./gradlew :app:assembleRelease     # Release APK
./gradlew :app:assembleDebug       # Debug APK
```

### 7.3 Testing Commands

```bash
./gradlew test                         # All unit tests
./gradlew :feature:auth:auth_domain:test  # Module unit tests
./gradlew connectedAndroidTest         # Instrumented tests
./gradlew connectedUiTests             # UI tests aggregate
./gradlew aggregateUiAndroidTestReports  # UI test reports
./gradlew test --info                  # Unit tests with extra info
```

### 7.4 Gradle Sync & Validation

```bash
./gradlew sync
./gradlew dependencyUpdates
./gradlew wrapper --gradle-version=<version>
```

### 7.5 Cleaning

```bash
./gradlew clean
./gradlew clean build
./gradlew :feature:auth:auth_ui:clean
```

### 7.6 Linting & Analysis (if configured)

```bash
./gradlew lint
./gradlew :app:lint
```

---

## 8. Implementation Patterns

### 8.1 Use Cases

**Fun Interface (with DI)**
```kotlin
fun interface GetMedia: () -> Either<Error, List<Media>>
```

**Interface + Implementation (pure business logic no data dependency)**
```kotlin
interface IsMovieValid: (Movie) -> Boolean

class ValidateMovie : IsMovieValid {
    override fun invoke(media: Media): Boolean =
        media.title.isNotBlank()
}
```

### 8.2 Layer Responsibilities
- Prefer a Repository when coordinating multiple data sources or shared state.
- Skip Repository when a single data source suffices; let the data source implement the use case.
- Repositories and DataSources stay concrete (no `Impl` suffix); interfaces live in domain.
- A repository may implement multiple use case interfaces if it owns the operations.

### 8.3 Error Handling with Arrow
- Model errors with the shared `AppError` type (`feature/core/core_domain/entities/AppError.kt`), which wraps an `AppErrorCode` enum (`SERVER`, `NOT_FOUND`, `BAD_REQUEST`, `LOCAL_ERROR`).
- Use `Either<AppError, Success>` from use cases; map/propagate explicitly.

### 8.4 Dependency Injection with Hilt
- Declare modules with `@Module` + `@InstallIn`; use `@Binds` for interfaces and `@Provides` for factories.
- ViewModels annotated with `@HiltViewModel` and injected constructor params.

### 8.5 StateFlow for UI State
- Expose immutable `StateFlow` from ViewModels; keep mutable state private.

### 8.6 Type-Safe Navigation
- Use `@Serializable` routes with `kotlinx.serialization` and Compose navigation.

### 8.7 Room Setup (Framework Layer)
- Entities annotated with `@Entity`; DAOs with `@Dao` and typed queries.
- Databases extend `RoomDatabase`; expose DAOs.

### 8.8 Firebase & Analytics
- Firebase (Analytics, Crashlytics, Performance, FCM) configured via `google-services.json` and Google Services plugin in `app`.

---

## 9. Practices & Guidance

### 9.1 Before Making Changes
- Identify layer/module impact; domain stays Android-free.
- Follow convention plugins; avoid manual dependency drift.
- Review relevant tests; extend or add before coding behavioral logic (ViewModels, use cases with implementation, repositories, data sources); follow Red-Green-Refactor for behavior changes.
- Skip tests for pure structures (entities, interfaces, DI config, constants).

### 9.2 Adding Dependencies
- Add version + alias in `gradle/libs.versions.toml`.
- If shared by module type, add to convention plugin; otherwise, declare in that module.
- Never hardcode versions in module Gradle files.

### 9.3 Creating Modules
- Layout: `feature/{name}/{name}_domain`, `{name}_framework`, `{name}_ui`.
- Register in `settings.gradle.kts` with exact names.
- Apply correct convention plugin per layer.

### 9.4 Testing Guidelines (quick)
- Unit: mocks only; isolate component.
- Integration: real impl + spies; avoid mocks.
- Compose UI: mock ViewModel; real Composables.

### 9.5 Code Review Checklist
- No Android deps in domain.
- Error handling via `Either<Error, Success>`.
- Dependencies declared via version catalog.
- Compose state hoisted; navigation type-safe.
- No hardcoded strings; resources used.

### 9.6 Debugging Tips
- Gradle sync issues → `./gradlew sync` or IDE cache invalidation.
- KSP errors → check `@Inject`/`@Provides` correctness.
- Navigation issues → verify `@Serializable` and route definitions.
- Compose previews → ensure default params or provided values.

---

## 10. Glossary

| Term          | Definition                                                    |
|---------------|---------------------------------------------------------------|
| **ADT**       | Algebraic Data Type (sealed classes, enums)                   |
| **AGP**       | Android Gradle Plugin                                         |
| **Arrow**     | Functional programming library for Kotlin                     |
| **Compose**   | Jetpack Compose declarative UI framework                      |
| **DAL**       | Data Access Layer (repositories, data sources)                |
| **Either**    | Arrow type for error handling (Left = Error, Right = Success) |
| **Hilt**      | Dependency injection framework by Google                      |
| **KSP**       | Kotlin Symbol Processing (annotation processing)              |
| **ORM**       | Object-Relational Mapping (Room)                              |
| **StateFlow** | Hot Flow with current state (collectible)                     |
| **TDD**       | Test-Driven Development                                       |
| **Use Case**  | Business logic function in domain layer                       |
| **ViewModel** | Lifecycle-aware state holder in Compose                       |

---

## 11. Quick Links

- Kotlin Docs: https://kotlinlang.org/docs/
- Jetpack Compose: https://developer.android.com/develop/ui/compose
- Hilt Documentation: https://dagger.dev/hilt/
- Room Persistence Library: https://developer.android.com/training/data-storage/room
- Retrofit: https://square.github.io/retrofit/
- Arrow: https://arrow-kt.io/
- Coroutines: https://kotlinlang.org/docs/coroutines-overview.html
- Firebase: https://firebase.google.com/docs

---

**Document Version**: 1.0  
**Last Updated**: December 29, 2025  
**Maintained By**: David Luna
