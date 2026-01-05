# PROJECT_CONTEXT.md

**Project Name:** TMDB-Android  
**Language:** Kotlin  
**Platform:** Android  
**Minimum API:** TBD (check `build-logic`)  
**Target API:** TBD (check `build-logic`)

---

## How to Use This Doc
- Scan the TOC, jump to the section you need.
- Follow naming in section 3; follow patterns in section 8.
- Build/test commands live in sections 7/8 for quick copy/paste.
- **Section 6 defines HOW we work (TDD process)** - this takes precedence for development workflow.

## Table of Contents
1. Project Overview
2. Architecture & Principles
3. Naming Conventions
4. Code Style & Compose
5. Dependencies & Stack
6. TDD Workflow & Methodology
7. Testing Strategy
8. Build & Tooling
9. Implementation Patterns
10. Practices & Guidance
11. Glossary
12. Quick Links

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

### Shared UI Resources Policy

All UI resources are **centralized and shared by default**.

- **ALL UI resources** (`strings`, `drawables`, `colors`, `dimens`, `fonts`, `styles`, and any other assets under `res/`) **MUST** be defined in:
  `feature/core/core_ui/src/main/res/`
- Feature modules **MUST NOT** define or own any `res/` resources.
- There are **NO feature-exclusive resources**.
- Any new UI resource introduced for a feature is considered **shared** and must live in `core_ui`.

This rule guarantees a single source of truth for UI assets and prevents duplication or architectural drift.

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

The project follows a three-layer clean architecture:

- **Domain Layer** (`*_domain`): Pure Kotlin business logic with use case interfaces, entities, and error models. Zero Android dependencies.
- **Framework Layer** (`*_framework`): Data sources and repositories implementing domain contracts. Handles Room, Retrofit, and persistence.
- **UI Layer** (`*_ui`): Jetpack Compose UI, ViewModels, and presentation logic.

**Dependency Flow**: UI → Domain ← Framework (domain is independent and pure Kotlin)

### 2.2 Key Design Principles

- **Unidirectional Dependency**: UI → Domain ← Framework (domain is independent)
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

## 6. TDD Workflow & Methodology

### Scope and authority
This document defines **how work is done**, not what the product is.

If there is any conflict between this file and other documentation (specs, plans), **this file takes precedence for process, TDD flow, and testing rules**.

---

### Role
You are a **Senior Android Engineer** working on an **Android-first** project using **Kotlin**, **Coroutines**, and **Clean Architecture**.

Your responsibility is to evolve the codebase using **strict Test Driven Development (TDD)** while applying **Clean Code**, **SOLID**, **DRY**, and **YAGNI** principles.

The goal is not speed, but correctness, clarity, and long-term maintainability.

---

### Non-negotiable rules

1. **TDD is mandatory for behavior**.
2. **Design must emerge from tests**, never upfront.
3. **RED → GREEN → REFACTOR** is mandatory.
4. **RED may be non-compiling**.
5. **No side effects in constructors or `init {}` blocks**.
6. **Fix tooling failures before behavior failures**.
7. **Temporary scaffolding is allowed and expected**.
8. **Delete tests that stop adding value**.
9. **Mock collaborators, not the SUT (once boundaries exist)**.
10. **Move code to final modules/packages only after behavior stabilizes**.

---

### Clean Architecture boundaries (process-oriented)

**Layers**
- **Domain**
  - Business models
  - Use case interfaces
  - Error models
- **Data / Framework**
  - Remote and local data sources
  - Repository implementations
  - Mapping logic (private)
- **UI**
  - ViewModels
  - Compose UI

**Dependency rules**
- Domain depends on nothing
- Data depends on Domain
- UI depends on Domain

These rules are enforced **after** behavior is complete, not before.

---

### What is tested (and what is not)

**MUST be tested**
- Data sources with behavior
- Repositories with coordination logic (including implementations of domain use case contracts)
- ViewModels (state + events)
- Error paths and meaningful edge cases

**MUST NOT be tested**
- Domain use case interfaces (contracts only)
- Pure data models (DTOs, entities without logic)
- Private mapping helpers
- Interfaces / contracts
- DI wiring
- Constants and enums

If a test only validates *structure*, delete it.

---

### Use cases in this project (contracts only)

**Rule**
In this codebase, use cases live in the **Domain layer** as **interfaces (contracts only)**.
They define behavior signatures but contain no logic.

**Where implementations live**
The behavior defined by use case interfaces is implemented in the **Framework/Data layer**, typically as:
- a **Repository**, when coordinating multiple data sources or shared state, or
- a **DataSource**, when a single source is sufficient.

There are **no standalone UseCaseImpl / Interactor classes**.

**Testing implication**
- Do **not** write tests for domain use case interfaces.
- Write tests for the **Repository/DataSource** that implements the contract, covering behavior.

---

### TDD workflow (edge-first)

When starting a new feature:

1. Start from the **data edge** (remote SDK or local DB)
2. Prefer **Remote DataSource first** when remote + local exist
3. Let tests define:
   - the public API
   - collaborators
   - return types
   - error model

Never pre-design these.

---

### RED can mean "does not compile"

**Rule**
A failing compilation **is a valid RED state**.

This is expected when:
- a class does not exist yet
- a method signature is being introduced

Do not bypass this by designing ahead.

---

### Guardrail test: no side effects on creation

Every new component should start with this test.

**Rule**
Object creation must never:
- trigger IO
- start coroutines
- call collaborators

This prevents hidden behavior in constructors and `init {}` blocks.

---

### Tooling RED vs Behavioral RED

**Rule**
If a test fails due to:
- MockK misuse
- coroutine test setup
- dispatcher configuration

**Fix the test first**.

RED must represent **missing behavior**, not broken tooling.

---

### Temporary GREEN is allowed

Sometimes tests must be brought to GREEN to:
- validate the test harness
- unblock the next RED

This does **not** mean behavior is complete.

Scaffolding is expected and short-lived.

---

### Tests may be deleted

**Rule**
As design evolves:
- earlier tests may break
- some tests may stop adding value

Delete tests that:
- only verify call mechanics
- only validated scaffolding
- block refactoring without protecting behavior

Apply **YAGNI to tests**.

---

### Coroutines rules

**Production**
- All IO-facing work must be `suspend`
- No blocking calls

**Testing**
- Use `kotlinx-coroutines-test`
- Use `TestDispatcher` + `TestScope`
- Override `Dispatchers.Main`
- Use `coEvery` / `coVerify` for suspend calls

**CoroutineTestRule**
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestRule : TestWatcher() {

    lateinit var dispatcher: TestDispatcher
        private set

    lateinit var scope: TestScope
        private set

    override fun starting(description: Description) {
        dispatcher = StandardTestDispatcher()
        scope = TestScope(dispatcher)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

---

### Collaborator rule

**Rule**
- Early scaffolding may mock the SUT
- Once a real boundary exists, the **SUT must be real**
- Mock collaborators, not the SUT

Behavior is validated through:
- collaborator interactions
- returned results

---

### Result modeling with Either

**Rules**
- Remote services return `Either<AppError, RemoteModel>`
- Data sources return `Either<AppError, DomainModel>`
- Errors are explicit and typed

---

### Mapping rules

**Rule**
- Remote models stay encapsulated in the data layer
- Mapping functions are **private** inside the data source
- Do **not** unit-test mapping helpers directly

Mapping correctness is covered by behavior tests.

---

### Few-shots (step-by-step examples)

> These examples are authoritative. They define the expected TDD flow.

### Shot 1 — First RED may not compile
```kotlin
class RemoteMediaDataSourceTest {

    @Test
    fun `on sut creation does not have side effects`() {
        val sut = RemoteMediaDataSource()
        verify { sut wasNot called }
    }
}
```

Expected: does not compile.

---

### Shot 2 — Make it compile minimally
```kotlin
class RemoteMediaDataSource()
```

---

### Shot 3 — Fix MockK setup (tooling GREEN)
```kotlin
@get:Rule
val mockkRule = MockKRule(testSubject = this)

@MockK
lateinit var sut: RemoteMediaDataSource

@Test
fun `on sut creation does not have side effects`() {
    verify { sut wasNot called }
}
```

---

### Shot 4 — Introduce behavior (non-compiling RED)
```kotlin
@Test
fun `load should be callable`() {
    sut.load()
}
```

---

### Shot 5 — Make it compile
```kotlin
class RemoteMediaDataSource {
    fun load() {}
}
```

---

### Shot 6 — Temporary GREEN (scaffolding)
```kotlin
@Test
fun `verify load should be called once`() {
    every { sut.load() } just runs

    sut.load()

    verify(exactly = 1) { sut.load() }
}
```

---

### Shot 7 — New behavior introduces return type
```kotlin
@Test
fun `load returns remote media`() {
    every { sut.load() } returns fakeRemoteMediaList

    val result = sut.load()

    assertEquals(fakeRemoteMediaList, result)
}
```

Earlier scaffolding tests may now break or be deleted.

---

### Shot 8 — REFACTOR: introduce service boundary + coroutines
```kotlin
interface RemoteMediaService {
    suspend fun load(): List<RemoteMedia>
}

class RemoteMediaDataSource(
    private val remote: RemoteMediaService
) {
    suspend fun load(): List<RemoteMedia> = remote.load()
}
```

---

### Shot 9 — Introduce Either + domain mapping
```kotlin
interface RemoteMediaService {
    suspend fun load(): Either<AppError, List<RemoteMedia>>
}

class RemoteMediaDataSource(
    private val remote: RemoteMediaService
) {
    suspend fun load(): Either<AppError, List<Media>> = tryCatch {
        val remoteMedia = remote.load().getOrElse { throw it }
        remoteMedia.map { it.toDomain() }
    }

    private fun RemoteMedia.toDomain(): Media = Media(
        id = id,
        title = title,
        posterURL = posterURL
    )
}

sealed class AppError : Throwable()
```

---

### Iteration completion rule

Each iteration must end with:
- all tests GREEN
- cleaner or equal design

Never stop mid-iteration.

---

### Packaging rule

Move code to final modules/packages **only after**:
- happy path is covered
- error path is covered
- optional edge cases are covered

Late moves are cheap. Early moves create churn.

---

### Final heuristic

> If you cannot explain why a test exists, delete it.

TDD is a **design process**, not a testing exercise.

---

## 7. Testing Strategy

### 7.1 Approach
- Follow TDD workflow from section 6 for all behavioral changes.
- Choose test type based on scope: unit for isolation, integration for flows, Compose UI for rendering/interaction, E2E for happy paths.
- Coverage targets: see Quick Reference table below.

### 7.2 Unit Tests
- ViewModels: verify state reducers, side-effects, navigation triggers.
- Repositories: assert contracts with data sources and error propagation.
- Data sources: validate mapping and persistence behaviors.
- Compose components (unit style): semantics and callbacks in `src/androidTest`.

Key patterns:
- Use `@MockK` for dependencies; `MockKRule(order = 0)` and `CoroutineTestRule(order = 1)`.
- `coEvery`/`every` for setup; `coVerify`/`verify` for assertions.
- GIVEN-WHEN-THEN structure; use `runTest` for suspend code.

### 7.3 Integration Tests
- Real implementations + spies from `testFixtures`; avoid MockK inside the flow.
- Good for ViewModel + Use Case + Repository stacks and persistence.

### 7.4 Compose UI Tests (`src/androidTest`)
- Test Composables directly; pass `uiState` and callbacks.
- Use semantic matchers (`onNodeWithText`, `onNodeWithContentDescription`).

### 7.5 Test Doubles
- **Unit**: mocks only (`@MockK`).
- **Integration**: spies/fakes from `testFixtures`.
- **Compose UI**: mock ViewModel, real Composables.

### 7.6 Quick Reference

| Test Type | Location | Framework | Pattern | Coverage |
|-----------|----------|-----------|---------|----------|
| Unit - Domain | `src/test/` | JUnit4 + MockK + Turbine | `@MockK` dependencies | ≥85% |
| Unit - Framework | `src/test/` | JUnit4 + MockK | `@MockK` DAOs | ≥80% |
| Unit - ViewModel | `src/test/` | JUnit4 + MockK + Turbine | `@MockK` use cases | ≥70% |
| Integration - ViewModel | `src/test/` | JUnit4 + Spies | Real impl + spies | Optional |
| UI - Compose | `src/androidTest/` | Compose Test + MockK | Mock VM, semantic checks | ≥60% |
| E2E | `app/src/androidTest/` | Hilt + MockWebServer | Happy paths | Selective |

---

## 8. Build & Tooling

### 8.1 Convention Plugins

| Plugin                           | Apply Method                      | Purpose                                     |
|----------------------------------|-----------------------------------|---------------------------------------------|
| **tmdb.android.application**     | `:app` only                       | Configures the main application module      |
| **tmdb.ui.module.plugin**        | UI modules (`*_ui`)               | Applies Compose, Hilt, testing dependencies |
| **tmdb.framework.module.plugin** | Framework modules (`*_framework`) | Applies Retrofit, Hilt, Room, testing setup |
| **tmdb.kotlin.module.plugin**    | Domain modules (`*_domain`)       | Pure Kotlin setup with testing              |
| **tmdb.room.module.plugin**      | Framework modules with Room       | Room ORM configuration                      |
| **tmdb.test.shared.plugin**      | `test_shared` module              | Testing utilities and mock setup            |

Apply via `alias(libs.plugins.*)` inside module `*.gradle.kts` files.

### 8.2 Build Commands

```bash
./gradlew build                    # Build entire project (debug)
./gradlew :app:build               # Build app module
./gradlew :feature:auth:auth_ui:build  # Build a feature module
./gradlew :app:assembleRelease     # Release APK
./gradlew :app:assembleDebug       # Debug APK
```

### 8.3 Testing Commands

```bash
./gradlew test                         # All unit tests
./gradlew :feature:auth:auth_domain:test  # Module unit tests
./gradlew connectedAndroidTest         # Instrumented tests
./gradlew connectedUiTests             # UI tests aggregate
./gradlew aggregateUiAndroidTestReports  # UI test reports
./gradlew test --info                  # Unit tests with extra info
```

### 8.4 Gradle Sync & Validation

```bash
./gradlew sync
./gradlew dependencyUpdates
./gradlew wrapper --gradle-version=<version>
```

### 8.5 Cleaning

```bash
./gradlew clean
./gradlew clean build
./gradlew :feature:auth:auth_ui:clean
```

### 8.6 Linting & Analysis (if configured)

```bash
./gradlew lint
./gradlew :app:lint
```

---

## 9. Implementation Patterns

### 9.1 Use Cases

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

### 9.2 Layer Responsibilities
- Repositories and DataSources stay concrete (no `Impl` suffix); interfaces live in domain.
- A repository may implement multiple use case interfaces if it owns the operations.

### 9.3 Error Handling with Arrow
- Model errors with the shared `AppError` type (`feature/core/core_domain/entities/AppError.kt`), which wraps an `AppErrorCode` enum (`SERVER`, `NOT_FOUND`, `BAD_REQUEST`, `LOCAL_ERROR`).
- Use `Either<AppError, Success>` from use cases; map/propagate explicitly.

### 9.4 Dependency Injection with Hilt
- Declare modules with `@Module` + `@InstallIn`; use `@Binds` for interfaces and `@Provides` for factories.
- ViewModels annotated with `@HiltViewModel` and injected constructor params.

### 9.5 StateFlow for UI State
- Expose immutable `StateFlow` from ViewModels; keep mutable state private.

### 9.6 Type-Safe Navigation
- Use `@Serializable` routes with `kotlinx.serialization` and Compose navigation.

### 9.7 Room Setup (Framework Layer)
- Entities annotated with `@Entity`; DAOs with `@Dao` and typed queries.
- Databases extend `RoomDatabase`; expose DAOs.

### 9.8 Firebase & Analytics
- Firebase (Analytics, Crashlytics, Performance, FCM) configured via `google-services.json` and Google Services plugin in `app`.

---

## 10. Practices & Guidance

### 10.1 Before Making Changes
- Identify layer/module impact; domain stays Android-free.
- Follow convention plugins; avoid manual dependency drift.
- Follow TDD workflow defined in section 6 for behavioral changes.

### 10.2 Adding Dependencies
- Add version + alias in `gradle/libs.versions.toml`.
- If shared by module type, add to convention plugin; otherwise, declare in that module.
- Never hardcode versions in module Gradle files.

### 10.3 Creating Modules
- Layout: `feature/{name}/{name}_domain`, `{name}_framework`, `{name}_ui`.
- Register in `settings.gradle.kts` with exact names.
- Apply correct convention plugin per layer.

### 10.4 Code Review Checklist
- No Android deps in domain.
- Error handling via `Either<Error, Success>`.
- Dependencies declared via version catalog.
- Compose state hoisted; navigation type-safe.
- No hardcoded strings; resources used.

### 10.5 Debugging Tips
- Gradle sync issues → `./gradlew sync` or IDE cache invalidation.
- KSP errors → check `@Inject`/`@Provides` correctness.
- Navigation issues → verify `@Serializable` and route definitions.
- Compose previews → ensure default params or provided values.

---

## 11. Glossary

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

## 12. Quick Links

- Kotlin Docs: https://kotlinlang.org/docs/
- Jetpack Compose: https://developer.android.com/develop/ui/compose
- Hilt Documentation: https://dagger.dev/hilt/
- Room Persistence Library: https://developer.android.com/training/data-storage/room
- Retrofit: https://square.github.io/retrofit/
- Arrow: https://arrow-kt.io/
- Coroutines: https://kotlinlang.org/docs/coroutines-overview.html
- Firebase: https://firebase.google.com/docs

---

**Document Version**: 2.0  
**Last Updated**: January 5, 2026  
**Maintained By**: David Luna
