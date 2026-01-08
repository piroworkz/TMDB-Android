# TMDB-Android — Developer Guide

A comprehensive guide for developers working on the **TMDB-Android** project. This document covers architecture details, development conventions, setup instructions, and common workflows.

**Last Updated**: December 17, 2025

---

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Project Architecture](#project-architecture)
3. [Development Environment](#development-environment)
4. [Module Structure & Dependencies](#module-structure--dependencies)
5. [Build System & Gradle](#build-system--gradle)
6. [Coding Conventions](#coding-conventions)
7. [Testing Strategy](#testing-strategy)
8. [Common Development Tasks](#common-development-tasks)
9. [Debugging & Troubleshooting](#debugging--troubleshooting)
10. [Performance Tips](#performance-tips)
11. [Additional Resources](#additional-resources)
12. [Getting Help](#getting-help)

---

## Quick Start

### 1. Initial Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/TMDB-Android.git
cd TMDB-Android

# Create local.properties with your API key
echo "TMDB_API_KEY=your_api_key_here" > local.properties

# Open in Android Studio
# Let Gradle sync automatically
```

### 2. Run the App

```bash
# Build and run on connected device/emulator
./gradlew installDebug

# Or use Android Studio: Run → Run 'app'
```

### 3. Run Tests

```bash
# Unit tests
./gradlew test

# Instrumented UI tests
./gradlew connectedUiTests
```

---

## Project Architecture

### Modular Clean Architecture

The project is organized into **features**, each with three layers:

```
Feature Module Structure:
├── *_domain/           # Business logic (pure Kotlin, no Android dependencies)
├── *_framework/        # Data layer (repositories, data sources, DI)
└── *_ui/               # Presentation layer (Compose, ViewModels)
```

### Dependency Flow

```
UI Layer
  ↓ (depends on)
Framework Layer
  ↓ (depends on)
Domain Layer
  ↓ (no dependencies)
External libraries (Retrofit, Room, etc.)
```

**Rule**: Dependencies always flow downward. Never import from UI into Framework or Domain.

### Current Features

1. **auth** - Authentication and login flow
    - `auth_domain/` - Login use cases and authentication models
    - `auth_framework/` - Authentication repository and API integration
    - `auth_ui/` - Login screen and authentication UI

2. **media** - Movies/Shows browsing and details
    - `media_domain/` - Media models and use cases
    - `media_framework/` - Media repository and TMDB API integration
    - `media_ui/` - Media listing, search, and detail screens

3. **core** - Shared functionality across all features
    - `core_domain/` - Shared domain models (Result types, common entities)
    - `core_framework/` - Shared infrastructure (network config, database)
    - `core_ui/` - Shared Compose components, themes, utilities

### Design Patterns Used

- **MVP-inspired** (Presenter-View) with ViewModel as Presenter for UI state management
- **Use Case as fun interface** - Single abstract method interfaces for business logic
- **Repository/DataSource Pattern** - Repositories implement use case interfaces directly
- **Dependency Injection** with Hilt for loose coupling
- **Arrow Either** for type-safe error handling (Either<AppError, T>)
- **Sealed Classes** for UI events and states

---

## Development Environment

### Required Tools

| Tool           | Version           | Notes            |
|----------------|-------------------|------------------|
| Android Studio | 2025.2.2+ (Otter) | IDE              |
| JDK            | 17                | Project target   |
| Gradle         | 8.13.2            | Wrapper included |
| Kotlin         | 2.2.21            | Language         |
| Android SDK    | 34+               | Compile target   |

### IDE Setup

#### Android Studio Settings

1. **Code Style**
    - Settings → Editor → Code Style → Kotlin
    - Enable "Optimize imports on the fly"
    - Enable "Add unambiguous imports on the fly"

2. **Kotlin Compiler**
    - Settings → Languages & Frameworks → Kotlin Compiler
    - Ensure "Report warnings as errors" is checked for CI consistency

3. **Run Configurations**
    - Pre-configured in `.run/` folder (if present)
    - Add your emulator or device for testing

#### Recommended Plugins

- **Kotlin** (bundled)
- **Compose Preview** (bundled)
- **Database Inspector** (bundled)
- **Hilt** (optional, for better DI support)

### Environment Variables

Set these in your shell profile (`.zshrc` or `.bash_profile`):

```bash
export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
export ANDROID_HOME=$ANDROID_SDK_ROOT
export PATH="$ANDROID_SDK_ROOT/tools:$ANDROID_SDK_ROOT/platform-tools:$PATH"
```

---

## Module Structure & Dependencies

### Module Naming Convention

- Feature modules: `feature/{feature_name}/{feature_name}_{layer}`
    - Example: `feature/auth/auth_ui`, `feature/media/media_framework`
- Package names: `com.davidluna.tmdb.{feature}.{layer}`
    - Example: `com.davidluna.tmdb.auth.ui`, `com.davidluna.tmdb.media.framework`

### Dependency Management

All dependencies are centralized in **`gradle/libs.versions.toml`**:

```toml
# Example structure:
[versions]
kotlin = "2.2.21"
retrofit = "3.0.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }

[plugins]
androidApplication = { id = "com.android.application", version.ref = "agp" }
```

**To add a new dependency**:

1. Add version to `[versions]`
2. Add library to `[libraries]`
3. Reference in module `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.retrofit)
   }
   ```

### Convention Plugins

Located in `build-logic/convention/`, these enforce consistent configurations:

- **`tmdb.ui.module.plugin`** - UI modules (Compose, Material3, testing libraries)
- **`tmdb.framework.module.plugin`** - Framework modules (networking, Room, Hilt)
- **`tmdb.kotlin.module.plugin`** - Kotlin/JVM modules (standard lib, coroutines)

**Usage in module `build.gradle.kts`**:

```kotlin
plugins {
    alias(libs.plugins.architectCodersUiModule)  // Applies UI conventions
}

dependencies {
    // Conventions already include Compose, Material3, JUnit, MockK, etc.
    implementation(libs.retrofit)  // Add only feature-specific deps
}
```

---

## Build System & Gradle

### Build Variants

```bash
# Debug build (default, includes debug symbols and profiling)
./gradlew assembleDebug

# Release build (optimized, minified with ProGuard/R8)
./gradlew assembleRelease

# Debug APK for testing
./gradlew installDebug
```

### Gradle Tasks

#### Testing Tasks

```bash
# Unit tests
./gradlew test
./gradlew :feature:auth:auth_domain:test

# Instrumented UI tests
./gradlew connectedUiTests
./gradlew :feature:auth:auth_ui:connectedDebugAndroidTest

# Aggregate test reports
./gradlew aggregateUiAndroidTestReports
```

#### Build Tasks

```bash
# Clean build
./gradlew clean

# Verify dependencies
./gradlew dependencies

# Check for dependency vulnerabilities
./gradlew dependencyCheck
```

#### Gradle Configuration

**Key files**:
- `Tmdb2024.gradle.kts` - Root project configuration and shared tasks
- `build-logic/build.gradle.kts` - Build logic module
- `app/app.gradle.kts` - App module configuration
- `feature/*/build.gradle.kts` - Feature module configurations

### Kotlin testFixtures support (Kotlin sourcesets)

- Kotlin `testFixtures` source sets only compile when `android.experimental.enableTestFixturesKotlinSupport=true` is set.
- Keep this property enabled in `gradle.properties` (preferred for team-wide default) or `local.properties` if you cannot commit it.
- Current state: it is already present in `local.properties`. If you move it to `gradle.properties`, delete the local copy to avoid confusion.

```properties
android.experimental.enableTestFixturesKotlinSupport=true
```

---

## Coding Conventions

### Kotlin Style Guide

**Naming**:
- Classes, interfaces: `PascalCase`
    - Example: `LoginViewModel`, `MediaRepository`
- Functions, variables: `camelCase`
    - Example: `getUserData()`, `userName`
- Constants: `UPPER_SNAKE_CASE`
    - Example: `const val API_TIMEOUT = 30000L`

**File Organization**:

```kotlin
// 1. Package declaration
package com.davidluna.tmdb.auth.ui

// 2. Imports (grouped and sorted)
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

// 3. Class/function declaration
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    // Implementation
}
```

### Package Structure

```
src/main/kotlin/com/davidluna/tmdb/
├── auth_ui/                   # UI Layer
│   ├── presenter/             # ViewModels and Events (by feature)
│   │   ├── login/            # LoginViewModel, LoginEvent
│   │   └── splash/           # SplashViewModel, etc.
│   ├── view/                  # Compose UI screens (by feature)
│   │   ├── login/            # LoginScreen.kt
│   │   │   └── composables/  # Reusable composables for login
│   │   └── splash/
│   └── navigation/            # Navigation routes
├── auth_framework/            # Framework Layer
│   ├── data/
│   │   ├── remote/           # API services and remote models
│   │   │   ├── RemoteAuthenticationService.kt
│   │   │   └── model/        # RemoteTokenResponse, RemoteSession, etc.
│   │   ├── local/            # Local database
│   │   │   └── database/     # DAOs and entities (RoomSession, RoomUserAccount)
│   │   └── sources/          # Repository/DataSource implementations
│   └── di/                   # Hilt modules
└── auth_domain/               # Domain Layer
    ├── entities/              # Domain models (LoginRequest, Session, etc.)
    └── usecases/              # Use case interfaces (fun interface)
```

**Key Patterns**:
- **Presenters**: Feature-based folders inside `presenter/` (e.g., `presenter/login/`)
- **Views**: Feature-based folders inside `view/` matching presenter structure
- **Use Cases**: Defined as `fun interface` in domain layer
- **Repositories/Sources**: Implement use case interfaces in framework `data/sources/`
- **Remote Models**: Use `Remote` prefix (e.g., `RemoteTokenResponse`)
- **Room Entities**: Use `Room` prefix (e.g., `RoomSession`)

### Compose Best Practices

```kotlin
// ✅ GOOD: Event-driven architecture with sealed interface
sealed interface LoginEvent {
    data class SetUsername(val username: String?) : LoginEvent
    data class SetPassword(val password: String?) : LoginEvent
    data object LoginButtonClicked : LoginEvent
    data object GuestButtonClicked : LoginEvent
    data object ResetAppError : LoginEvent
}

// ✅ GOOD: State as data class in ViewModel
@HiltViewModel
class LoginViewModel (
    private val loginUser: LoginWithCredentials,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _state = MutableStateFlow(State())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = State()
    )
    
    @Stable
    data class State(
        val isLoading: Boolean = false,
        val appError: AppError? = null,
        val username: String = String(),
        val password: String = String()
    )
    
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SetUsername -> _state.update { it.copy(username = event.username ?: "") }
            is LoginEvent.SetPassword -> _state.update { it.copy(password = event.password ?: "") }
            is LoginEvent.LoginButtonClicked -> performLogin()
            // ...
        }
    }
}

// ✅ GOOD: Screen delegates to stateless Content composable
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LoginContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

// ✅ GOOD: Stateless, testable content composable
@Composable
fun LoginContent(
    state: LoginViewModel.State,
    onEvent: (LoginEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = state.username,
            onValueChange = { onEvent(LoginEvent.SetUsername(it)) }
        )
        TextField(
            value = state.password,
            onValueChange = { onEvent(LoginEvent.SetPassword(it)) }
        )
        Button(
            onClick = { onEvent(LoginEvent.LoginButtonClicked) },
            enabled = !state.isLoading
        ) {
            Text("Login")
        }
    }
}

// ❌ AVOID: Creating ViewModels manually
@Composable
fun BadScreen() {
    val viewModel = LoginViewModel()  // Wrong! Use hiltViewModel()
}

// ❌ AVOID: State duplication
@Composable
fun AnotherBadScreen(email: String) {
    var localEmail by remember { mutableStateOf(email) }  // Duplicates state!
}
```

### Hilt Dependency Injection

```kotlin
// ✅ ViewModel with Hilt (uses fun interface use cases)
@HiltViewModel
class LoginViewModel (
    private val loginUser: LoginWithCredentials,
    private val loginGuest: LoginAsGuest,
    private val validateInput: ValidateInputUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    // Implementation
}

// ✅ Use case defined as fun interface in domain
fun interface LoginWithCredentials : suspend (LoginRequest) -> Either<AppError, Unit>

// ✅ Repository implements use case in framework
class RegisteredUserAuthenticationRepository (
    private val remote: RemoteAuthenticationService,
    private val local: SessionDao
) : LoginWithCredentials {
    override suspend fun invoke(request: LoginRequest): Either<AppError, Unit> = tryCatch {
        val token = createTokenRequest()
        val authorization = authorizeToken(token, request)
        val session = createSession(authorization)
        local.insertSession(session.toLocalStorage())
    }
}

// ❌ AVOID: Creating instances manually
class BadViewModel {
    private val repository = RegisteredUserAuthenticationRepository()  // Hard to test!
}
```

### Error Handling with Arrow Either

```kotlin
// ✅ GOOD: Functional error handling with Either
// Domain: Use case as fun interface
fun interface LoginWithCredentials : suspend (LoginRequest) -> Either<AppError, Unit>

// Framework: Implementation
class RegisteredUserAuthenticationRepository (
    private val remote: RemoteAuthenticationService,
    private val local: SessionDao
) : LoginWithCredentials {
    override suspend fun invoke(request: LoginRequest): Either<AppError, Unit> = tryCatch {
        val token = createTokenRequest()
        val authorization = authorizeToken(token, request)
        val session = createSession(authorization)
        local.insertSession(session.toLocalStorage())
    }
}

// Usage in ViewModel
viewModelScope.launch(ioDispatcher) {
    loginUser(loginRequest)
        .fold(
            ifLeft = { error -> _state.update { it.copy(appError = error, isLoading = false) } },
            ifRight = { _state.update { it.copy(isLoading = false) } }
        )
}

// ✅ AppError definition
data class AppError(
    val code: AppErrorCode,
    val description: String,
    val type: Throwable? = null,
) : Throwable()

enum class AppErrorCode {
    SERVER,
    NOT_FOUND,
    BAD_REQUEST,
    LOCAL_ERROR
}

// ❌ AVOID: try-catch for normal flows
suspend fun badLogin(): User {
    try {
        return authRepository.login(email, password)
    } catch (e: Exception) {
        // Catching exceptions as flow control
        throw e
    }
}
```

### Room Database

```kotlin
// ✅ GOOD: Proper entity and DAO structure
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}

// ✅ GOOD: Expose Flow for reactive updates
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>
}
```

---

## Testing Strategy

### Testing Pyramid

```
       / \
      /   \  E2E / Integration Tests
     /     \     (few, slow)
    /-------\
   /         \  UI/Instrumented Tests
  /           \   (moderate)
 /-------------\
/               \ Unit Tests
/                 (many, fast)
/___________________\
```

### Unit Testing

**Location**: `src/test/`
**Tools**: JUnit4, MockK, Turbine, Arrow

```kotlin
class LoginViewModelTest {
    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)
    
    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()
    
    @MockK
    private lateinit var loginWithCredentials: LoginWithCredentials
    
    @MockK
    private lateinit var loginAsGuest: LoginAsGuest
    
    @MockK
    private lateinit var validateInput: ValidateInputUseCase
    
    private lateinit var viewModel: LoginViewModel
    
    @Test
    fun `GIVEN valid credentials WHEN login button clicked THEN success state is emitted`() = runTest {
        // Arrange
        val loginRequest = LoginRequest("user", "password")
        coEvery { validateInput(any(), any()) } returns null
        coEvery { loginWithCredentials(loginRequest) } returns Unit.right()
        
        viewModel = buildViewModel()
        
        // Act
        viewModel.onEvent(LoginEvent.SetUsername("user"))
        viewModel.onEvent(LoginEvent.SetPassword("password"))
        viewModel.onEvent(LoginEvent.LoginButtonClicked)
        advanceUntilIdle()
        
        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.appError)
            assertEquals(false, state.isLoading)
        }
    }
    
    @Test
    fun `GIVEN invalid credentials WHEN login button clicked THEN error state is emitted`() = runTest {
        // Arrange
        val appError = AppError(AppErrorCode.BAD_REQUEST, "Invalid credentials")
        coEvery { validateInput(any(), any()) } returns null
        coEvery { loginWithCredentials(any()) } returns appError.left()
        
        viewModel = buildViewModel()
        
        // Act
        viewModel.onEvent(LoginEvent.SetUsername("user"))
        viewModel.onEvent(LoginEvent.SetPassword("wrong"))
        viewModel.onEvent(LoginEvent.LoginButtonClicked)
        advanceUntilIdle()
        
        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.appError)
            assertEquals(appError, state.appError)
        }
    }
    
    private fun buildViewModel() = LoginViewModel(
        loginUser = loginWithCredentials,
        loginGuest = loginAsGuest,
        validateInput = validateInput,
        ioDispatcher = coroutineTestRule.testDispatcher
    )
}
```

### Instrumented UI Testing

**Location**: `src/androidTest/`
**Tools**: Compose testing, JUnit4, semantics matchers

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun initialRender_showsFieldsAndButtons() = composeTestRule.run {
        setContentWithState(LoginViewModel.State())
        
        onAllNodes(isEditable()).assertCountEquals(2)
        onNodeWithText("Login").assertExists().assertIsEnabled()
        onNodeWithText("Login as Guest").assertExists().assertIsEnabled()
        onNode(hasTestTag("ProgressIndicator")).assertDoesNotExist()
    }
    
    @Test
    fun loadingState_showsProgressAndDisablesButtons() = composeTestRule.run {
        setContentWithState(LoginViewModel.State(isLoading = true))
        
        onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.0F, 0.0F..0.0F, 0))).assertExists()
        onNode(hasText("Login") and hasClickAction()).assertIsNotEnabled()
        onNode(hasText("Login as Guest") and hasClickAction()).assertIsNotEnabled()
    }
    
    @Test
    fun appError_showsErrorDialog() = composeTestRule.run {
        val error = AppError(
            code = AppErrorCode.LOCAL_ERROR,
            description = "Unexpected error"
        )
        setContentWithState(LoginViewModel.State(appError = error))
        
        onNodeWithText("Unexpected error").assertExists()
    }
    
    @Test
    fun loginButtonClick_dispatchesLoginEvent() = composeTestRule.run {
        var lastEvent: LoginEvent? = null
        setContentWithState(LoginViewModel.State(), onEvent = { lastEvent = it })
        
        onAllNodes(isEditable())[0].performTextInput("davidluna@mail.com")
        onAllNodes(isEditable())[1].performTextInput("12345678")
        onNodeWithText("Login").performClick()
        
        assert(lastEvent is LoginEvent.LoginButtonClicked)
    }
    
    private fun ComposeContentTestRule.setContentWithState(
        state: LoginViewModel.State,
        onEvent: (LoginEvent) -> Unit = {}
    ) {
        setContent {
            TmdbTheme {
                LoginContent(state = state, onEvent = onEvent)
            }
        }
    }
}
```

### Mocking API Calls

In unit tests, API services that return `Either` are mocked using MockK:

```kotlin
class RegisteredUserAuthenticationRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)
    
    @MockK
    private lateinit var remote: RemoteAuthenticationService
    
    @MockK
    private lateinit var local: SessionDao
    
    private lateinit var repository: RegisteredUserAuthenticationRepository
    
    @Before
    fun setup() {
        repository = RegisteredUserAuthenticationRepository(remote, local)
    }
    
    @Test
    fun `GIVEN valid token WHEN invoke is called THEN success is returned`() = runTest {
        // Arrange
        val token = RemoteTokenResponse(success = true, token = "valid_token")
        val session = RemoteSessionIdResponse(sessionId = "session123")
        
        coEvery { remote.createRequestToken() } returns token.right()
        coEvery { remote.authorizeToken(any()) } returns token.right()
        coEvery { remote.createSessionId(any()) } returns session.right()
        coEvery { local.insertSession(any()) } returns Unit
        
        // Act
        val result = repository(LoginRequest("user", "pass"))
        
        // Assert
        assertTrue(result.isRight())
    }
    
    @Test
    fun `GIVEN remote error WHEN invoke is called THEN error is returned`() = runTest {
        // Arrange
        val remoteError = RemoteError(statusCode = 401, statusMessage = "Unauthorized")
        coEvery { remote.createRequestToken() } returns remoteError.left()
        
        // Act
        val result = repository(LoginRequest("user", "pass"))
        
        // Assert
        assertTrue(result.isLeft())
        result.onLeft { error -> 
            assertEquals(AppErrorCode.BAD_REQUEST, error.code)
        }
    }
}
```

For E2E tests, MockWebServer is used with a custom rule. See `app/src/androidTest` for examples.

### Run Tests

```bash
# All unit tests
./gradlew test

# Specific test class
./gradlew test --tests LoginViewModelTest

# All instrumented tests
./gradlew connectedUiTests

# Specific instrumented test
./gradlew :feature:auth:auth_ui:connectedDebugAndroidTest
```

---

## Common Development Tasks

### Adding a New Feature

1. **Create feature directory structure**:
   ```bash
   mkdir -p feature/newfeature/{newfeature_domain,newfeature_framework,newfeature_ui}/src/{main,test,androidTest}/kotlin
   ```

2. **Create `build.gradle.kts` for each layer**:

   **Domain** (`newfeature_domain/build.gradle.kts`):
   ```kotlin
   plugins {
       alias(libs.plugins.architectCodersKotlinModule)
   }
   
   dependencies {
       implementation(projects.feature.core.coreDomain)
       implementation(libs.arrowCore)
       implementation(libs.pagingJVM)
   }
   ```

   **Framework** (`newfeature_framework/build.gradle.kts`):
   ```kotlin
   plugins {
       alias(libs.plugins.architectCodersFrameworkModule)
   }
   
   dependencies {
       implementation(projects.feature.newfeature.newfeatureDomain)
       implementation(projects.feature.core.coreData)
   }
   ```

   **UI** (`newfeature_ui/build.gradle.kts`):
   ```kotlin
   plugins {
       alias(libs.plugins.architectCodersUiModule)
   }
   
   dependencies {
       implementation(projects.feature.newfeature.newfeatureDomain)
       implementation(projects.feature.newfeature.newfeatureFramework)
       implementation(projects.feature.core.coreUi)
   }
   ```

3. **Add to `settings.gradle.kts`**:
   ```kotlin
   include(":feature:newfeature:newfeature_domain")
   include(":feature:newfeature:newfeature_framework")
   include(":feature:newfeature:newfeature_ui")
   ```

4. **Update `app/app.gradle.kts`**:
   ```kotlin
   dependencies {
       implementation(projects.feature.newfeature.newfeatureDomain)
       implementation(projects.feature.newfeature.newfeatureFramework)
       implementation(projects.feature.newfeature.newfeatureUi)
   }
   ```

5. **Create package structure**:
   ```
   newfeature_domain/src/main/kotlin/com/davidluna/tmdb/newfeature_domain/
   ├── entities/          # Domain models
   └── usecases/          # fun interface use cases
   
   newfeature_framework/src/main/kotlin/com/davidluna/tmdb/newfeature_framework/
   ├── data/
   │   ├── remote/        # API services and models
   │   ├── local/         # Room entities and DAOs
   │   └── sources/       # Use case implementations
   └── di/                # Hilt modules
   
   newfeature_ui/src/main/kotlin/com/davidluna/tmdb/newfeature_ui/
   ├── presenter/         # ViewModels and Events (by screen)
   ├── view/              # Compose screens (by screen)
   └── navigation/        # Navigation routes
   ```

6. **Define navigation in UI module**:
   ```kotlin
   // navigation/NewFeatureNavigation.kt
   @Serializable
   sealed interface NewFeatureDestination {
       @Serializable
       data object MainScreen : NewFeatureDestination
   }
   ```

### Adding a New API Endpoint

1. **Define Remote model in framework layer** (`data/remote/model/`):
   ```kotlin
   // feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/remote/model/RemoteMovie.kt
   @Serializable
   data class RemoteMovie(
       @SerialName("id")
       val id: Int?,
       @SerialName("title")
       val title: String?,
       @SerialName("overview")
       val overview: String?
   )
   ```

2. **Add API service method** (`data/remote/services/`):
   ```kotlin
   // RemoteMediaService.kt
   interface RemoteMediaService {
       @GET("movie/{id}")
       suspend fun getMovie(@Path("id") movieId: Int): Either<RemoteError, RemoteMovie>
   }
   ```

3. **Define use case in domain layer** (`usecases/`):
   ```kotlin
   // feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/usecases/GetMovieDetailsUseCase.kt
   fun interface GetMovieDetailsUseCase : suspend (Int) -> Either<AppError, MovieDetails>
   ```

4. **Create domain entity** (`entities/`):
   ```kotlin
   // feature/media/media_domain/src/main/kotlin/com/davidluna/tmdb/media_domain/entities/MovieDetails.kt
   data class MovieDetails(
       val id: Int,
       val title: String,
       val overview: String
   )
   ```

5. **Implement use case in framework** (`data/sources/ or data/repositories/`):
   ```kotlin
   // feature/media/media_framework/src/main/kotlin/com/davidluna/tmdb/media_framework/data/repositories/MovieDetailsRepository.kt
   class MovieDetailsRepository (
       private val api: RemoteMediaService
   ) : GetMovieDetailsUseCase {
       override suspend fun invoke(id: Int): Either<AppError, MovieDetails> =
           api.getMovie(id)
               .mapLeft { it.toAppError() }
               .map { it.toDomain() }
   }
   ```

6. **Create mapping extension** (`data/remote/model/` or separate mapper folder):
   ```kotlin
   fun RemoteMovie.toDomain(): MovieDetails =
       MovieDetails(
           id = this.id ?: 0,
           title = this.title ?: "",
           overview = this.overview ?: ""
       )
   ```

7. **Bind in Hilt module** (`di/`):
   ```kotlin
   @Module
   @InstallIn(ViewModelComponent::class)
   abstract class MediaUseCaseModule {
       @Binds
       abstract fun bindGetMovieDetails(
           repository: MovieDetailsRepository
       ): GetMovieDetailsUseCase
   }
   ```

8. **Use in ViewModel**:
   ```kotlin
   @HiltViewModel
   class MovieDetailViewModel (
       private val getMovieDetails: GetMovieDetailsUseCase,
       private val ioDispatcher: CoroutineDispatcher
   ) : ViewModel() {
       fun loadMovie(id: Int) {
           viewModelScope.launch(ioDispatcher) {
               getMovieDetails(id)
                   .fold(
                       ifLeft = { error -> _state.update { it.copy(error = error) } },
                       ifRight = { movie -> _state.update { it.copy(selectedMovie = movie) } }
                   )
           }
       }
   }
   ```

### Adding a New Room Entity and Query

1. **Define entity in framework layer** (`data/local/database/entities/`):
   ```kotlin
   @Entity(tableName = "movies")
   data class RoomMovie(
       @PrimaryKey val id: Int,
       val title: String,
       val releaseDate: String
   )
   ```

2. **Create DAO** (`data/local/database/dao/`):
   ```kotlin
   @Dao
   interface MovieDao {
       @Query("SELECT * FROM movies WHERE id = :movieId")
       suspend fun getMovie(movieId: Int): RoomMovie?
       
       @Query("SELECT * FROM movies ORDER BY releaseDate DESC")
       fun observeAllMovies(): Flow<List<RoomMovie>>
       
       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insertMovie(movie: RoomMovie)
       
       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insertAll(movies: List<RoomMovie>)
   }
   ```

3. **Add to database**:
   ```kotlin
   @Database(
       entities = [RoomMovie::class],
       version = 1,
       exportSchema = false
   )
   abstract class TmdbDatabase : RoomDatabase() {
       abstract fun movieDao(): MovieDao
   }
   ```

4. **Define use case in domain** (`usecases/`):
   ```kotlin
   // Observe movies flow
   fun interface ObserveMoviesUseCase : () -> Flow<List<Movie>>
   
   // Or with error handling
   fun interface GetMovieByIdUseCase : suspend (Int) -> Either<AppError, Movie>
   ```

5. **Create mapping extension** (`data/local/mapper/` or in entity file):
   ```kotlin
   fun RoomMovie.toDomain(): Movie =
       Movie(
           id = this.id,
           title = this.title,
           releaseDate = this.releaseDate
       )
   
   fun Movie.toRoom(): RoomMovie =
       RoomMovie(
           id = this.id,
           title = this.title,
           releaseDate = this.releaseDate
       )
   ```

6. **Implement use case in repository** (`data/sources/ or data/repositories/`):
   ```kotlin
   class MovieRepository (
       private val movieDao: MovieDao
   ) : ObserveMoviesUseCase, GetMovieByIdUseCase {
       
       override fun invoke(): Flow<List<Movie>> =
           movieDao.observeAllMovies()
               .map { entities -> entities.map { it.toDomain() } }
       
       override suspend fun invoke(id: Int): Either<AppError, Movie> = tryCatch {
           val roomMovie = movieDao.getMovie(id)
               ?: error("Movie not found")
           roomMovie.toDomain()
       }
   }
   ```

7. **Use in ViewModel**:
   ```kotlin
   @HiltViewModel
   class MoviesViewModel (
       private val observeMovies: ObserveMoviesUseCase,
       private val getMovie: GetMovieByIdUseCase
   ) : ViewModel() {
       val movies = observeMovies()
           .stateIn(
               scope = viewModelScope,
               started = SharingStarted.WhileSubscribed(5_000),
               initialValue = emptyList()
           )
       
       fun loadMovieDetails(id: Int) {
           viewModelScope.launch {
               getMovie(id)
                   .fold(
                       ifLeft = { error -> _state.update { it.copy(error = error) } },
                       ifRight = { movie -> _state.update { it.copy(selectedMovie = movie) } }
                   )
           }
       }
   }
   ```

---

## Debugging & Troubleshooting

### Common Issues

#### 1. Gradle Sync Fails

**Solution**:
```bash
# Clean and resync
./gradlew clean
# In Android Studio: File → Sync Now

# Or manually clear cache
rm -rf ~/.gradle/caches
./gradlew clean && ./gradlew build
```

#### 2. Hilt Compilation Errors

**Solution**:
- Ensure `@HiltViewModel` is on ViewModel class
- Check that all injected dependencies have Hilt modules or @Provides
- Clean and rebuild: `./gradlew clean :app:build`

#### 3. Compose Preview Not Working

**Solution**:
- Ensure composable has `@Preview` annotation
- Check Android Studio version (should be 2025.2.2+)
- Invalidate caches: File → Invalidate Caches → Restart

#### 4. Tests Failing with "Could not instantiate"

**Solution**:
- Add `@RunWith(AndroidJUnit4::class)` to instrumented tests
- Use `InstantTaskExecutorRule` for livedata/stateflow tests
- Ensure test fixtures are properly set up in `test_shared`

### Debugging Tools

#### Logcat

```bash
# View logs in real-time
adb logcat

# Filter by tag
adb logcat | grep "TMDB"

# Clear logs
adb logcat -c
```

#### Android Studio Debugger

```kotlin
// Set breakpoints by clicking on line numbers
// Right-click breakpoint for conditions

// Use Debug Console for live expressions
// Step over (F10), Step into (F11), Continue (F5)
```

#### Database Inspector

- In Android Studio: View → Tool Windows → Database Inspector
- Browse Room database tables at runtime
- Run SQL queries directly

#### Network Inspector

- In Android Studio: View → Tool Windows → Network Inspector
- Monitor all HTTP requests/responses
- View request/response bodies

---

## Performance Tips

### Compose Performance

```kotlin
// ✅ GOOD: Minimize recompositions
@Composable
fun MovieList(
    movies: List<Movie>,  // Stable type
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(movies, key = { it.id }) { movie ->
            MovieItem(movie = movie)
        }
    }
}

// ❌ AVOID: Creating lambdas in composition
@Composable
fun BadMovieList(movies: List<Movie>) {
    LazyColumn {
        items(movies) { movie ->
            MovieItem(
                movie = movie,
                onClick = { showDetail(movie.id) }  // Creates new lambda every composition!
            )
        }
    }
}

// ✅ GOOD: Use rememberCallback for stable lambdas
@Composable
fun GoodMovieList(
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    val handleClick = remember(onMovieClick) { { id: Int -> onMovieClick(id) } }
    LazyColumn {
        items(movies, key = { it.id }) { movie ->
            MovieItem(movie = movie, onClick = { handleClick(movie.id) })
        }
    }
}
```

### Network Optimization

```kotlin
// ✅ GOOD: Cache responses and use etag
@Provides
@Singleton
fun provideHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .cache(Cache(context.cacheDir, 10 * 1024 * 1024))  // 10 MB
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()
}

// ✅ GOOD: Pagination to reduce memory usage
@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY releaseDate DESC LIMIT :limit OFFSET :offset")
    suspend fun getMoviesPaged(limit: Int, offset: Int): List<MovieEntity>
}
```

### Memory Optimization

```kotlin
// ✅ GOOD: Cancel coroutines in ViewModel
class MovieDetailViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    override fun onCleared() {
        super.onCleared()
        // Coroutines in viewModelScope are automatically cancelled
    }
}

// ✅ GOOD: Use weak references for listeners
private val listeners = mutableSetOf<WeakReference<MyListener>>()

// ❌ AVOID: Leaking context
val intentFilter = IntentFilter(ACTION_BATTERY_CHANGED)
context.registerReceiver(receiver, intentFilter)  // Will leak if not unregistered
```

### Database Optimization

```kotlin
// ✅ GOOD: Use indexes for frequently queried fields
@Entity(
    tableName = "movies",
    indices = [Index("userId"), Index("releaseDate")]
)
data class MovieEntity(
    @PrimaryKey val id: Int,
    val userId: String,
    val releaseDate: LocalDateTime
)

// ✅ GOOD: Batch operations
@Dao
interface MovieDao {
    @Insert
    suspend fun insertAll(movies: List<MovieEntity>)  // Single transaction
}
```

---

## Additional Resources

- [Android Developers Guide](https://developer.android.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Retrofit & OkHttp](https://square.github.io/retrofit/)
- [Arrow Kotlin](https://arrow-kt.io/)

---

## Getting Help

- **Check existing issues** on GitHub before creating new ones
- **Ask in team channels** for quick questions
- **Create detailed bug reports** with:
    - Android version
    - Device/emulator info
    - Steps to reproduce
    - Expected vs. actual behavior
    - Relevant logs

---

**Happy coding! 🎉**
