# PROJECT_CONTEXT.md

**Project Name:** TMDB-Android  
**Language:** Kotlin  
**Platform:** Android  
**Minimum API:** TBD (check `build-logic`)  
**Target API:** TBD (check `build-logic`)

---

## 1. Project Structure Overview

The TMDB-Android project follows a **multi-module, clean architecture** pattern with clear separation of concerns. The modular structure is organized as follows:

```
TMDB-Android/
├── app/                          # Main application module
├── feature/                       # Feature modules
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
├── gradle/                        # Gradle wrapper and version catalog
├── contexts/                      # Documentation and context files
└── settings.gradle.kts            # Gradle settings with module inclusions
```

### Module Types

- **Domain (`*_domain`)**: Pure Kotlin modules containing business logic, use cases, entities, and repository interfaces. Zero Android framework dependencies.
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

## 2. Architecture & Design Patterns

### 2.1 Clean Architecture

The project implements **Clean Architecture** across three layers:

1. **Domain Layer** (`*_domain`)
   - Business rules and pure Kotlin use cases
   - Entities, value objects, and repository interfaces
   - Use cases prefer `fun interface` when a single public method exists
   - Uses `Arrow`'s `Either<Error, Success>` for type-safe error handling
   - No dependencies on Android or data-specific frameworks

2. **Framework Layer** (`*_framework`)
   - Local and remote data sources
   - Repository implementations (when multiple data sources or multiple use cases share coordination)
   - Handles data persistence (Room) and network requests (Retrofit)
   - Depends on Domain; uses dependency injection (Hilt)

3. **UI Layer** (`*_ui`)
   - Jetpack Compose UI and presentation (screens, components, ViewModels)
   - State management via `StateFlow`
   - Depends on Domain for use cases

### 2.2 Key Design Principles

- **Unidirectional Dependency**: UI → Domain ← Framework (domain is independent)
- **TDD First**: Always work with tests driving implementation; add or update tests before changes
- **Prefer composition** over inheritance; avoid base classes
- **Clean Code & SOLID**: Keep small, cohesive types with single responsibilities
- **Use Cases Pattern**:
  - `fun interface` with a single `invoke()` method when a repository/data source exposes exactly one public operation
  - If a repository supports multiple public operations, it can implement multiple use case interfaces as needed
  - If only one data source is required and no repository adds value, the data source may implement the use case interface directly
- **Type-Safe Error Handling**: `Arrow`'s `Either<L, R>` for explicit error propagation
- **Reactive Programming**: Coroutines and Flow for asynchronous operations
- **Dependency Injection**: Hilt for object lifecycle and dependency management
- **Type-Safe Navigation**: `kotlinx.serialization` for passing complex arguments in Compose navigation

---

## 3. Code Style & Conventions

### 3.1 Language & Idioms

- **Primary Language**: Kotlin (exclusive)
- **Null Safety**: Leverages Kotlin's null safety features; prefer non-null types
- **Coroutines**: Used for all asynchronous operations
- **Flow & StateFlow**: For reactive state management and observable data streams
- **Extension Functions**: Encouraged for domain-agnostic utilities
- **Higher-Order Functions**: Preferred for callbacks and composition
- **Sealed Classes**: Used for type-safe ADTs (Algebraic Data Types) and result handling

### 3.2 Naming Conventions

- **Classes**: PascalCase (e.g., `UserRepository`, `LoginScreen`)
- **Functions & Variables**: camelCase (e.g., `getUserById()`, `isLoading`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
- **Packages**: lowercase with dots (e.g., `com.davidluna.tmdb.auth.domain`)
- **Composables**: PascalCase, often suffixed with `Screen` or `Dialog` (e.g., `LoginScreen`, `UserDialog`)
- **ViewModels**: Suffixed with `ViewModel` (e.g., `LoginViewModel`)

### 3.3 File Organization

- One public class/interface per file
- File name matches the public declaration (e.g., `UserRepository.kt` contains `interface UserRepository`)
- Imports are organized (alphabetically, with blank lines separating groups)
- Use `@Suppress` annotations sparingly and with documentation

### 3.4 Module Naming

- Domain modules: `{feature}_domain`
- Framework modules: `{feature}_framework`
- UI modules: `{feature}_ui`
- Core modules: `core_{layer}`

### 3.5 Compose Conventions

- Composables follow the `@Composable` annotation convention
- Preview functions: Suffixed with `Preview` and annotated with `@Preview`
- State hoisting: Lift state to the lowest common parent
- Use `remember` and `mutableStateOf` for local state, `StateFlow` for shared state
- Material Design 3 components with Color and Shape system

### 3.6 Code Formatting

- Indentation: 4 spaces
- Line length: Keep under 120 characters (soft limit)
- Blank lines: Separate logical sections within functions and classes
- Comments: Use meaningful, concise comments; avoid obvious statements
- Documentation: Public APIs should have KDoc comments

---

## 4. Dependencies & Stack

### 4.1 Gradle & Build Tools

| Component                   | Version      | Details                           |
|-----------------------------|--------------|-----------------------------------|
| AGP (Android Gradle Plugin) | 8.13.2       | Official Android build system     |
| Kotlin                      | 2.2.21       | Language and stdlib               |
| KSP                         | 2.2.21-2.0.4 | Symbol processing for annotations |
| Room                        | 2.8.4        | ORM database framework            |

### 4.2 UI & Jetpack

| Library             | Version          | Purpose                      |
|---------------------|------------------|------------------------------|
| Jetpack Compose BOM | 2025.12.00       | UI toolkit                   |
| Material 3          | Latest (via BOM) | Design system                |
| Navigation Compose  | 2.9.6            | Type-safe navigation         |
| Activity Compose    | 1.12.1           | Activity-Compose integration |
| Coil Compose        | 2.7.0            | Image loading library        |

### 4.3 Data & Networking

| Library               | Version | Purpose                |
|-----------------------|---------|------------------------|
| Retrofit              | 3.0.0   | HTTP client & REST API |
| OkHttp                | 5.3.2   | HTTP networking        |
| Kotlinx Serialization | 1.9.0   | JSON serialization     |
| Room (Database)       | 2.8.4   | Local persistence      |
| DataStore             | 1.2.0   | Secure preferences     |

### 4.4 Dependency Injection & DI Frameworks

| Library                 | Version | Purpose                        |
|-------------------------|---------|--------------------------------|
| Hilt                    | 2.57.2  | Dependency injection           |
| Hilt Navigation Compose | 1.3.0   | ViewModel injection in Compose |
| javax.inject            | 1       | Injection annotations          |

### 4.5 Functional & Error Handling

| Library | Version | Purpose                                 |
|---------|---------|-----------------------------------------|
| Arrow   | 2.2.0   | Functional programming (Either, Option) |

### 4.6 Asynchronous Programming

| Library            | Version | Purpose              |
|--------------------|---------|----------------------|
| Kotlinx Coroutines | 1.10.2  | Async/await and Flow |
| Kotlinx DateTime   | 0.7.1   | Date/time operations |

### 4.7 Testing

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

### 4.8 Other Dependencies

| Library                | Version       | Purpose                |
|------------------------|---------------|------------------------|
| Core Splashscreen      | 1.2.0         | Splash screen API      |
| Biometric              | 1.2.0-alpha05 | Fingerprint/Face auth  |
| Play Services Location | 21.3.0        | Location services      |
| Firebase BOM           | 34.7.0        | Firebase suite         |
| Google Services Plugin | 4.4.4         | Firebase configuration |

### 4.9 Version Catalog

All dependencies are managed in `gradle/libs.versions.toml`. This centralized approach:
- Ensures version consistency across modules
- Simplifies dependency updates
- Provides IDE auto-completion in `build.gradle.kts` files
- Uses the alias syntax (e.g., `alias(libs.plugins.hiltPlugin)`)

---

## 5. Build System & Convention Plugins

### 5.1 Convention Plugins

The `build-logic/convention` directory contains custom Gradle plugins that standardize module configurations:

| Plugin                           | Apply Method                      | Purpose                                     |
|----------------------------------|-----------------------------------|---------------------------------------------|
| **tmdb.android.application**     | `:app` only                       | Configures the main application module      |
| **tmdb.ui.module.plugin**        | UI modules (`*_ui`)               | Applies Compose, Hilt, testing dependencies |
| **tmdb.framework.module.plugin** | Framework modules (`*_framework`) | Applies Retrofit, Hilt, Room, testing setup |
| **tmdb.kotlin.module.plugin**    | Domain modules (`*_domain`)       | Pure Kotlin setup with testing              |
| **tmdb.room.module.plugin**      | Framework modules with Room       | Room ORM configuration                      |
| **tmdb.test.shared.plugin**      | `test_shared` module              | Testing utilities and mock setup            |

### 5.2 Plugin Application

Plugins are applied via `alias()` in module-specific `{moduleName}.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.uiModuleConventionPlugin)
}
```

### 5.3 Common Convention Functions

Helper functions in `build-logic/convention/src/main/kotlin/com/davidluna/tmdb/convention/`:

- `implementation()`: Add compile-time dependencies
- `testImplementation()`: Add test-only dependencies
- `androidTestImplementation()`: Add instrumented test dependencies
- `ksp()`: Add KSP annotation processor dependencies
- `composeUiBundle`: Bundle of Compose UI libraries
- `unitTestingBundle`: JUnit, MockK, Turbine, etc.
- `androidTestingBundle`: Espresso, Hilt Test, etc.

### 5.4 When to Use Convention Plugins vs Module-Specific Dependencies

**Use Convention Plugins when**:
- The dependency is needed by **all or most modules** of a certain type (e.g., all UI modules need Hilt)
- The dependency is part of the standard architecture (e.g., Arrow for domain, Retrofit for framework)
- You want to ensure consistency across modules

**Use Module-Specific Dependencies when**:
- The dependency is unique to one feature (e.g., Biometric authentication only in `auth_ui`)
- The dependency is experimental or temporary
- The dependency is feature-specific (e.g., a payment SDK only in `payment_ui`)

**Example**:

```kotlin
// In build-logic/convention/.../UiModuleConventionPlugin.kt
// Common dependencies for ALL UI modules
private fun Project.dependencies() {
    dependencies {
        implementation(libs.hiltAndroid)
        implementation(libs.arrowCore)
        composeUiBundle
        unitTestingBundle
    }
}
```

```kotlin
// In feature/auth/auth_ui/auth_ui.gradle.kts
// Feature-specific dependency
dependencies {
    implementation(libs.biometric) // Only auth_ui needs this
    implementation(projects.feature.auth.authDomain)
}
```

---

## 6. Testing Strategy

### 6.1 Testing Approach

The project follows **Test-Driven Development (TDD)**: tests always drive implementation. Write or update tests before code changes to capture desired behavior.

### 6.2 Unit Tests

Expectations (align with patterns in existing tests):
- ViewModels: verify state reducers, side-effects, and navigation triggers (patterned after the detail ViewModel tests)
- Repositories: assert contract with data sources and error propagation (as in catalog repository tests)
- Data sources: validate mapping and persistence behaviors (mirroring selected catalog data source tests)
- Compose components: assert semantics, accessibility labels, and callbacks (e.g., text fields and app bars) — these live in `src/androidTest`

**Location**: `src/test/` for pure unit tests; Compose UI component tests in `src/androidTest/`

**Libraries**: JUnit 4, MockK, Turbine (for Flow), Coroutines Test

**Scope**:
- Domain: Use cases, entities, business logic
- UI: ViewModels, state management
- Framework: Repositories, data sources (mocked)

**Dependencies**:
- UI modules often depend on framework module's `testFixtures` for mock repositories and fake implementations
- Framework modules may use `test_shared` for common test utilities

**Example Pattern**:
```kotlin
class LoginUseCaseTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @Test
    fun loginWithValidCredentials_ReturnsSuccess() = runTest {
        // Given
        val mockRepository = mockk<LoginRepository>()
        val useCase = LoginUseCase(mockRepository)
        
        // When
        val result = useCase.invoke(validCredentials)
        
        // Then
        assertTrue(result.isRight())
    }
}
```

**Using Test Fixtures**:
```kotlin
class LoginViewModelTest {
    private val fakeRepository = FakeLoginRepository() // from testFixtures
    
    @Test
    fun loginSuccess_UpdatesStateCorrectly() = runTest {
        val useCase = LoginUseCase(fakeRepository)
        val viewModel = LoginViewModel(useCase)
        
        viewModel.login(credentials)
        
        assertEquals(UiState.Success, viewModel.state.value)
    }
}
```

### 6.3 Integration Tests

- Live in the presentation layer within UI modules; cover ViewModel ↔ repository/data source flows (see login/splash integration styles)
- Prefer spies/fakes from framework `testFixtures` to observe interactions (e.g., DAO spies for session handling)

### 6.4 UI Integration Tests (Compose)

- Treat Compose UI as integration: drive screens through user flows and assert state/rendering (login/splash screen tests as reference)

### 6.5 Test Fixtures

- Home for shared fakes/spies/builders per module; UI modules consume framework fixtures for realistic doubles

### 6.6 Test Utilities

- Project-level shared rules, runners, and helpers live here; keep assertions/helpers reusable and small

### 6.7 End-to-End Tests

- All E2E instrumented tests reside in `TmdbAppEndToEndTest`; keep to happy paths, short, and fast
- Add new scenarios to that class only; avoid creating additional E2E classes

---

## 7. Execution Commands

### 7.1 Building

```bash
# Build the entire project (debug variant)
./gradlew build

# Build only the app module
./gradlew :app:build

# Build a specific feature module
./gradlew :feature:auth:auth_ui:build

# Build release variant
./gradlew :app:assembleRelease

# Build and output debug APK
./gradlew :app:assembleDebug
```

### 7.2 Testing

```bash
# Run all unit tests across the project
./gradlew test

# Run unit tests for a specific module
./gradlew :feature:auth:auth_domain:test

# Run instrumented (Android) tests
./gradlew connectedAndroidTest

# Run UI tests for specific modules
./gradlew connectedUiTests

# Aggregate all UI test reports
./gradlew aggregateUiAndroidTestReports

# Run tests with coverage (if configured)
./gradlew test --info
```

### 7.3 Gradle Sync & Validation

```bash
# Sync project with Gradle files (recommended after build config changes)
./gradlew sync

# Check for dependency updates
./gradlew dependencyUpdates

# Validate Gradle wrapper
./gradlew wrapper --gradle-version=<version>
```

### 7.4 Cleaning

```bash
# Clean build artifacts
./gradlew clean

# Clean and build from scratch
./gradlew clean build

# Clean specific module
./gradlew :feature:auth:auth_ui:clean
```

### 7.5 Linting & Analysis (if configured)

```bash
# Run Lint checks (Android Lint)
./gradlew lint

# Run Lint on specific module
./gradlew :app:lint
```

### 7.6 Useful IDE Commands (Android Studio)

- **Sync Project with Gradle Files**: Recommended after any `build.gradle.kts` changes
- **Invalidate Caches / Restart**: If Gradle sync issues persist
- **Build → Make Project**: Incremental build
- **Build → Generate APK**: UI for debug APK generation

---

## 8. Key Implementation Details

### 8.1 Use Cases Pattern

Use cases can be implemented in two ways depending on their dependencies and responsibilities:

**Option 1: Fun Interface (when requiring dependency injection)**

Used when the use case delegates to repositories or other injected services:

```kotlin
fun interface GetMoviesUseCase {
    suspend operator fun invoke(): Either<Error, List<Movie>>
}

class GetMoviesUseCaseImpl(
    private val movieRepository: MovieRepository
) : GetMoviesUseCase {
    override suspend fun invoke(): Either<Error, List<Movie>> {
        return movieRepository.getPopularMovies()
    }
}
```

**Option 2: Interface + Implementation (when encapsulating pure business logic)**

Used for use cases that contain business logic without requiring external data sources:

```kotlin
interface ValidateMovieUseCase {
    fun invoke(movie: Movie): Either<Error, ValidMovie>
}

class ValidateMovieUseCaseImpl : ValidateMovieUseCase {
    override fun invoke(movie: Movie): Either<Error, ValidMovie> {
        return if (movie.title.isNotBlank()) {
            Either.Right(ValidMovie(movie))
        } else {
            Either.Left(ValidationError.EmptyTitle)
        }
    }
}
```

The choice between these approaches depends on:
- Whether the use case needs dependency injection
- Whether it interfaces with data sources (repositories, data stores, etc.)
- The complexity and reusability of the business logic

### 8.2 Error Handling with Arrow

Domain layer errors are modeled as sealed classes:

```kotlin
sealed interface MovieError : Error {
    data class NetworkError(val message: String) : MovieError
    data class NotFoundError(val id: Int) : MovieError
}
```

Use cases return `Either<Error, Success>`:

```kotlin
return movieRepository.getMovie(id)
    .mapLeft { MovieError.NotFoundError(id) }
```

### 8.3 Dependency Injection with Hilt

Modules are registered in `@Module` classes with `@Provides` or `@Binds`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {
    @Binds
    fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase
}
```

Injection in ViewModels:
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() { ... }
```

### 8.4 StateFlow for UI State

ViewModels expose state as `StateFlow`:

```kotlin
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()
}
```

### 8.5 Type-Safe Navigation

Navigation with Compose using `kotlinx.serialization`:

```kotlin
@Serializable
data class MovieDetailRoute(val movieId: Int)

// Navigation
navController.navigate(MovieDetailRoute(movieId))

// Screen
@Composable
fun MovieDetailScreen(movieId: Int) { ... }
```

### 8.6 Room Database Setup

Entities with Room annotations:
```kotlin
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String
)
```

DAO:
```kotlin
@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>
}
```

Database:
```kotlin
@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
```

---

## 9. Firebase & Analytics

The project integrates Firebase for:
- **Analytics**: Event tracking and user behavior
- **Crashlytics**: Crash reporting and stability monitoring
- **Performance Monitoring**: App performance metrics
- **Cloud Messaging**: Push notifications

Configuration is managed via `google-services.json` (app-specific) and the Gradle Google Services plugin.

---

## 10. Important Notes for AI Agents & Developers

### 10.1 Before Making Changes

1. **Understand the Module Layer**: Know which layer (domain, framework, UI) your change affects
2. **Check Dependencies**: Domain modules must NOT depend on Android or data frameworks
3. **Follow Conventions**: Use convention plugins; avoid manual dependency management
4. **Review Tests**: Check existing tests to understand the pattern

### 10.2 Adding New Dependencies

1. Add entry to `gradle/libs.versions.toml` with version
2. Add library alias in the same file
3. **If the dependency will be used in multiple modules**:
   - Add it to the appropriate convention plugin in `build-logic/convention/`
   - Example: Common dependencies for all UI modules go in `UiModuleConventionPlugin`
4. **If the dependency is specific to one module only**:
   - Use `alias()` directly in that module's `{moduleName}.gradle.kts` file
5. Never hardcode versions directly in module files

### 10.3 Creating New Modules

1. Create directory structure: `feature/{name}/{name}_domain`, `{name}_framework`, `{name}_ui`
2. Register in `settings.gradle.kts` (e.g., `:feature:auth:auth_domain`)
3. Create Gradle build file named exactly as the module: `{module_name}.gradle.kts`
   - Example: For module `auth_domain` → create `auth_domain.gradle.kts`
   - Example: For module `media_ui` → create `media_ui.gradle.kts`
4. Apply the appropriate convention plugin based on module type:
   - Domain: `alias(libs.plugins.kotlinModuleConventionPlugin)`
   - Framework: `alias(libs.plugins.frameworkModuleConventionPlugin)` or `roomModuleConventionPlugin`
   - UI: `alias(libs.plugins.uiModuleConventionPlugin)`
5. Follow the layered structure: domain → framework → UI

### 10.4 Testing Guidelines

1. Write tests in `src/test/` for unit tests
2. Write tests in `src/androidTest/` for instrumented tests
3. Use convention bundles (`unitTestingBundle`, `androidTestingBundle`)
4. Mock external dependencies (Retrofit, Room, Hilt)
5. Test use cases in isolation; mock repositories

### 10.5 Code Review Checklist

- [ ] No Android dependencies in domain modules
- [ ] Error handling uses `Either<Error, Success>` pattern
- [ ] Dependencies are declared in version catalog
- [ ] Tests follow TDD patterns
- [ ] Compose state is properly hoisted
- [ ] Naming conventions are followed
- [ ] No hardcoded strings (use resources)
- [ ] Comments are meaningful and concise

### 10.6 Debugging Tips

- **Gradle Sync Issues**: Run `./gradlew sync` or invalidate caches in Android Studio
- **KSP Compilation Errors**: Check Hilt annotation processing; ensure `@Inject` or `@Provides` are correct
- **Test Failures**: Check for coroutine scope issues; use `runTest` for suspend functions
- **Navigation Issues**: Verify `@Serializable` annotations and route definitions
- **Compose Preview Errors**: Ensure all Composable parameters have defaults or are provided in preview

---

## 11. Project-Specific Information

### 11.1 Root Gradle File: `Tmdb2024.gradle.kts`

This file:
- Declares all plugins (not applied, just available)
- Defines custom tasks like `connectedUiTests` and `aggregateUiAndroidTestReports`
- Manages plugin versions via aliases

### 11.2 App Module: `app/app.gradle.kts`

The main application module:
- Applies `architectCodersAndroidApplication` plugin
- Depends on all feature modules
- Integrates Firebase (via `google-services.json`)

### 11.3 Namespace Convention

Namespaces follow: `com.davidluna.tmdb.{module_name}`
Example: `auth_ui` → `com.davidluna.tmdb.auth_ui`

### 11.4 ProGuard Rules

ProGuard configuration in `app/proguard-rules.pro`:
- Protects Hilt-generated classes
- Preserves Retrofit interfaces
- Keeps Serializable classes

---

## 12. Glossary

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

## 13. Quick Reference Links

- **Kotlin Docs**: https://kotlinlang.org/docs/
- **Jetpack Compose**: https://developer.android.com/develop/ui/compose
- **Hilt Documentation**: https://dagger.dev/hilt/
- **Room Persistence Library**: https://developer.android.com/training/data-storage/room
- **Retrofit**: https://square.github.io/retrofit/
- **Arrow**: https://arrow-kt.io/
- **Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
- **Firebase**: https://firebase.google.com/docs

---

**Document Version**: 1.0  
**Last Updated**: December 29, 2025  
**Maintained By**: David Luna
