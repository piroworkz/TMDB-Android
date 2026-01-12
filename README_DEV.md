# TMDB-Android — Developer Guide

A comprehensive guide for developers working on the **TMDB-Android** project. This document covers architecture details, development conventions, setup instructions, and common workflows.

**Last Updated**: January 11, 2026

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
├── *_data/             # Data layer (repositories, data sources, DI)
└── *_ui/               # Presentation layer (Compose, ViewModels)
```

### Dependency Flow

```
UI Layer
  ↓ (depends on)
Domain Layer
  ↑ (depends on)
Data Layer
  ↓
External libraries (Retrofit, Room, etc.)
```

**Rule**: `ui` → `domain` ← `data`. Never import from `data` into `ui` directly.

### Current Features

1. **auth** - Authentication and login flow
    - `auth_domain/` - Login use cases (contracts) and authentication models
    - `auth_data/` - Authentication repository and API integration
    - `auth_ui/` - Login screen and authentication UI

2. **media** - Movies/Shows browsing and details
    - `media_domain/` - Media models and use cases (contracts)
    - `media_data/` - Media repository and TMDB API integration
    - `media_ui/` - Media listing, search, and detail screens

3. **core** - Shared functionality across all features
    - `core_domain/` - Shared domain models (Result types, common entities)
    - `core_data/` - Shared infrastructure (network config, database)
    - `core_ui/` - Shared Compose components, themes, utilities

### Design Patterns Used

- **MVVM** (Model-View-ViewModel) with ViewModel for UI state management
- **Use Case as fun interface** - Single abstract method interfaces for business logic
- **Repository/DataSource Pattern** - Repositories implement use case interfaces directly
- **Dependency Injection** with Koin for lightweight, Kotlin-first DI
- **Arrow Either** for type-safe error handling (Either<AppError, T>)
- **Sealed Classes** for UI events and states

---

## Development Environment

### Required Tools

| Tool           | Version                       | Notes            |
|----------------|-------------------------------|------------------|
| Android Studio | Ladybug Feature Drop (2024.2.2)+ | IDE              |
| JDK            | 17                            | Project target   |
| Gradle         | 8.13.2                        | Wrapper included |
| Kotlin         | 2.3.0                         | Language         |
| Android SDK    | 34+                           | Compile target   |

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
    - Example: `feature/auth/auth_ui`, `feature/media/media_data`
- Package names: `com.davidluna.tmdb.{feature}_{layer}`
    - Example: `com.davidluna.tmdb.auth_ui`, `com.davidluna.tmdb.media_data`

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

- **`tmdb.android.application`** - Android application configuration
- **`tmdb.ui.module.plugin`** - UI modules (Compose, Material3, testing libraries)
- **`tmdb.framework.module.plugin`** - Data modules (networking, Room, Koin)
- **`tmdb.room.module.plugin`** - Room database configuration
- **`tmdb.kotlin.module.plugin`** - Kotlin/JVM modules (standard lib, coroutines)
- **`tmdb.test.shared.plugin`** - Shared test utilities configuration

**Usage in module `build.gradle.kts`**:

```kotlin
plugins {
    alias(libs.plugins.uiModuleConventionPlugin)  // Applies UI conventions
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
package com.davidluna.tmdb.auth_ui.view.login

// 2. Imports (grouped and sorted)
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

// 3. Class/function declaration
@Composable
fun LoginScreen(viewModel: LoginViewModel = koinViewModel()) {
    // Implementation
}
```

### Package Structure

```
src/main/kotlin/com/davidluna/tmdb/
├── auth_ui/                   # UI Layer
│   ├── di/                   # Koin modules for UI
│   ├── presenter/             # ViewModels and Events (by feature)
│   │   ├── login/            # LoginViewModel, LoginEvent
│   │   └── splash/           # SplashViewModel, etc.
│   ├── view/                  # Compose UI screens (by feature)
│   │   ├── login/            # LoginScreen.kt
│   │   │   └── composables/  # Reusable composables for login
│   │   └── splash/
│   └── navigation/            # Navigation routes
├── auth_data/                 # Data Layer
│   ├── di/                   # Koin modules for data
│   ├── framework/
│   │   ├── remote/           # API services and remote models
│   │   │   ├── AuthenticationApi.kt
│   │   │   └── model/        # RemoteTokenResponse, RemoteSession, etc.
│   │   └── local/            # Local database
│   │       └── database/     # DAOs and entities (RoomSession, RoomUserAccount)
│   ├── repositories/          # Repository implementations
│   └── utils/                # Shared helpers
└── auth_domain/               # Domain Layer
    ├── entities/              # Domain models (LoginRequest, Session, etc.)
    └── usecases/              # Use case interfaces (fun interface)
```

**Key Patterns**:
- **Presenters**: Feature-based folders inside `presenter/` (e.g., `presenter/login/`)
- **Views**: Feature-based folders inside `view/` matching presenter structure
- **Use Cases**: Defined as `fun interface` in domain layer
- **Repositories**: Implement use case interfaces in data `repositories/`
- **Remote Models**: Use `Remote*` prefix (e.g., `RemoteTokenResponse`)
- **Room Entities**: Use `Room*` prefix (e.g., `RoomSession`)

### Compose Best Practices

```kotlin
// ✅ GOOD: Event-driven architecture with sealed interface
sealed interface LoginEvent {
    data class LoginButtonClicked(val username: String, val password: String) : LoginEvent
    data class SetPassword(val password: String?) : LoginEvent
    data class SetUsername(val username: String?) : LoginEvent
    data object GuestButtonClicked : LoginEvent
    data object ResetAppError : LoginEvent
}

// ✅ GOOD: State as data class in ViewModel
class LoginViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val openSession: OpenSession,
    private val validateInput: ValidateInput,
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
        val password: String = String(),
        val usernameError: String? = null,
        val passwordError: String? = null,
        val isLoggedIn: Boolean = false,
    )

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.ResetAppError -> _state.update { it.copy(appError = null) }
            LoginEvent.GuestButtonClicked -> guestLogin()
            is LoginEvent.LoginButtonClicked -> login(event.username, event.password)
            is LoginEvent.SetPassword -> setPassword(password = event.password)
            is LoginEvent.SetUsername -> setUsername(username = event.username)
        }
    }

    private fun login(username: String, password: String): Unit = launchOnIO {
        if (validateLoginForm(username, password)) {
            val result = openSession.open(LoginMethod.AuthCredentials(username, password))
            _state.update { s -> s.copy(appError = result, isLoggedIn = result == null) }
        }
    }

    private fun launchOnIO(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            try {
                _state.update { it.copy(isLoading = true) }
                action()
            } catch (appError: AppError) {
                _state.update { it.copy(isLoading = false, appError = appError) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, appError = e.toAppError()) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}

// ✅ GOOD: Screen with ViewModel from Koin, delegates to stateless composable
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            navigate()
        }
    }

    LoginScreen(
        appError = state.appError,
        isLoading = state.isLoading,
        password = state.password,
        passwordError = state.passwordError,
        username = state.username,
        usernameError = state.usernameError,
        onEvent = { viewModel.onEvent(it) }
    )
}

// ✅ GOOD: Stateless, testable content composable
@Composable
fun LoginScreen(
    appError: AppError?,
    isLoading: Boolean,
    password: String,
    passwordError: String?,
    username: String,
    usernameError: String?,
    onEvent: (LoginEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.margins.xLarge)
        ) {
            UsernameTextField(
                value = username,
                fieldErrorMessage = usernameError,
                onValueChange = { onEvent(LoginEvent.SetUsername(it)) }
            )
            PasswordTextField(
                value = password,
                fieldErrorMessage = passwordError,
                onValueChange = { onEvent(LoginEvent.SetPassword(it)) }
            )
            Button(
                onClick = { onEvent(LoginEvent.LoginButtonClicked(username, password)) },
                enabled = !isLoading
            ) {
                Text(text = stringResource(R.string.btn_login))
            }
            TextButton(
                onClick = { onEvent(LoginEvent.GuestButtonClicked) },
                enabled = !isLoading
            ) {
                Text(text = stringResource(R.string.btn_login_as_guest))
            }
        }
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (appError != null) {
            ErrorDialogView(appError = appError) { onEvent(LoginEvent.ResetAppError) }
        }
    }
}

// ❌ AVOID: Creating ViewModels manually
@Composable
fun BadScreen() {
    val viewModel = LoginViewModel()  // Wrong! Use koinViewModel()
}

// ❌ AVOID: State duplication
@Composable
fun AnotherBadScreen(email: String) {
    var localEmail by remember { mutableStateOf(email) }  // Duplicates state!
}
```

### Koin Dependency Injection

```kotlin
// ✅ ViewModel wired via Koin
class LoginViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val openSession: OpenSession,
    private val validateInput: ValidateInput,
) : ViewModel() {
    // Implementation
}

// ✅ Koin module definition in *_ui/di/ using viewModelOf
val authPresentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SplashViewModel)
}

// ✅ Koin module definition in *_data/di/ using singleOf + bind
val authDataModule = module {
    singleOf(::TextInputValidator) bind ValidateInput::class
    singleOf(::AccountRepository) bind ObserveUserAccount::class
    singleOf(::AuthenticationRepository) bind OpenSession::class
    singleOf(::AuthenticationRepository) bind CloseSession::class
    singleOf(::AuthenticationRepository) bind ValidateSession::class
    singleOf(::AuthenticationRepository) bind ObserveSession::class
}

// ✅ Koin module for Retrofit API services
val authFrameworkRemoteModule = module {
    singleOf(::provideAuthenticationService)
    singleOf(::provideUserAccountService)
}

private fun provideAuthenticationService(retrofit: Retrofit): AuthenticationApi =
    retrofit.create(AuthenticationApi::class.java)

// ✅ Use case defined as interface in domain (not fun interface when multiple methods)
interface OpenSession {
    suspend fun open(method: LoginMethod): AppError?
}

interface ObserveSession {
    val session: Flow<Session?>
}

// ✅ Use case defined as fun interface in domain (single method)
fun interface GetCatalogVideos : suspend (Catalog, Int) -> Either<AppError, List<Video>>

// ✅ Repository implements multiple use case contracts
class AuthenticationRepository(
    private val authAPI: AuthenticationApi,
    private val sessionDao: SessionDao,
    private val accountDetailsRepository: AccountDetailsRepository
) : OpenSession, CloseSession, ValidateSession, ObserveSession {

    override val session: Flow<Session?>
        get() = sessionDao.getSession().map { it?.toDomain() }

    override suspend fun open(method: LoginMethod): AppError? = if (method is AuthCredentials) {
        withCredentials(method)
    } else {
        asGuest()
    }

    private suspend fun withCredentials(authCredentials: AuthCredentials): AppError? =
        tryCatchSuspend {
            val token = authAPI.createRequestToken().getOrElse { throw it.toAppError() }
            val tokenAuthorization = authAPI.authorizeToken(token.toRemote(authCredentials))
                .getOrElse { throw it.toAppError() }
            val session = authAPI.createSessionId(tokenAuthorization.toLoginRequest())
                .getOrElse { throw it.toAppError() }
            sessionDao.insertSession(session.toLocalStorage())
            accountDetailsRepository.fetch()?.let { throw it }
        }.leftOrNull()

    // Mapping functions are private inside the repository
    private fun RemoteSessionIdResponse.toLocalStorage(): RoomSession = RoomSession(
        sessionId = sessionId,
        isGuest = false,
        expiresAt = null
    )

    private fun RoomSession.toDomain(): Session = Session(
        sessionId = sessionId,
        isGuest = isGuest,
        expiresAt = expiresAt
    )
}

// ✅ Usage in Compose with koinViewModel
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    navigate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ...
}

// ❌ AVOID: Creating instances manually
class BadViewModel {
    private val repository = AuthenticationRepository()  // Hard to test!
}
```

### Error Handling with Arrow Either

```kotlin
// ✅ GOOD: Functional error handling with tryCatchSuspend
// Helper function in core_domain
suspend fun <T> tryCatchSuspend(block: suspend () -> T): Either<AppError, T> =
    try {
        block().right()
    } catch (appError: AppError) {
        appError.left()
    } catch (e: Throwable) {
        e.toAppError().left()
    }

// ✅ GOOD: Use case as interface returning AppError?
interface OpenSession {
    suspend fun open(method: LoginMethod): AppError?
}

// ✅ GOOD: Use case as fun interface returning Either
fun interface GetCatalogVideos : suspend (Catalog, Int) -> Either<AppError, List<Video>>

// ✅ GOOD: Repository implementation with tryCatchSuspend
class AuthenticationRepository(...) : OpenSession, CloseSession {

    override suspend fun open(method: LoginMethod): AppError? = if (method is AuthCredentials) {
        withCredentials(method)
    } else {
        asGuest()
    }

    private suspend fun withCredentials(authCredentials: AuthCredentials): AppError? =
        tryCatchSuspend {
            val token = authAPI.createRequestToken().getOrElse { throw it.toAppError() }
            val tokenAuthorization = authAPI.authorizeToken(token.toRemote(authCredentials))
                .getOrElse { throw it.toAppError() }
            val session = authAPI.createSessionId(tokenAuthorization.toLoginRequest())
                .getOrElse { throw it.toAppError() }
            sessionDao.insertSession(session.toLocalStorage())
            accountDetailsRepository.fetch()?.let { throw it }
        }.leftOrNull()  // Returns AppError? (null on success)
}

// ✅ GOOD: Repository returning Either<AppError, T>
class CatalogVideosRepository(
    private val local: MediaVideosDao,
    private val remote: RemoteMediaService,
    private val isCacheExpired: IsCacheExpired,
) : GetCatalogVideos {

    override suspend operator fun invoke(
        catalog: Catalog,
        mediaId: Int,
    ): Either<AppError, List<Video>> = tryCatchSuspend {
        val localVideos = local.getVideo(mediaId)
        if (localVideos.isNotEmpty() && !isCacheExpired(localVideos.firstOrNull()?.savedTimeMillis)) {
            localVideos.map { it.toDomain() }
        } else {
            val remoteVideos = fetchRemoteVideos(catalog.toEndpointPath(mediaId), mediaId)
            local.cacheVideos(remoteVideos, isCacheExpired).map { it.toDomain() }
        }
    }
}

// ✅ GOOD: Usage in ViewModel with AppError?
private fun login(username: String, password: String): Unit = launchOnIO {
    if (validateLoginForm(username, password)) {
        val result = openSession.open(LoginMethod.AuthCredentials(username, password))
        _state.update { s -> s.copy(appError = result, isLoggedIn = result == null) }
    }
}

// ✅ GOOD: Usage in ViewModel with Either
viewModelScope.launch(ioDispatcher) {
    getCatalogVideos(catalog, mediaId)
        .fold(
            ifLeft = { error -> _state.update { it.copy(appError = error, isLoading = false) } },
            ifRight = { videos -> _state.update { it.copy(videos = videos, isLoading = false) } }
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

fun Throwable.toAppError(): AppError = AppError(
    code = AppErrorCode.NOT_FOUND,
    description = this.message ?: "Unknown error",
    type = cause
)

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
// ✅ GOOD: Entity with Room* prefix naming convention
@Entity(tableName = "sessions")
data class RoomSession(
    @PrimaryKey val sessionId: String,
    val isGuest: Boolean,
    val expiresAt: String?
)

@Entity(tableName = "videos")
data class RoomVideo(
    @PrimaryKey val id: String,
    val key: String,
    val mediaId: Int,
    val savedTimeMillis: Long
)

// ✅ GOOD: DAO interface
@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions LIMIT 1")
    fun getSession(): Flow<RoomSession?>

    @Query("SELECT EXISTS(SELECT 1 FROM sessions)")
    suspend fun hasSession(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RoomSession): Long

    @Query("DELETE FROM sessions")
    suspend fun deleteSession(): Int
}

// ✅ GOOD: DAO for videos with cache management
@Dao
interface MediaVideosDao {
    @Query("SELECT * FROM videos WHERE mediaId = :mediaId")
    suspend fun getVideo(mediaId: Int): List<RoomVideo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<RoomVideo>)

    @Query("DELETE FROM videos WHERE mediaId = :mediaId")
    suspend fun deleteByMediaId(mediaId: Int)

    @Transaction
    suspend fun cacheVideos(videos: List<RoomVideo>, shouldClear: Boolean): List<RoomVideo> {
        if (shouldClear && videos.isNotEmpty()) {
            deleteByMediaId(videos.first().mediaId)
        }
        insertAll(videos)
        return videos
    }
}

// ✅ GOOD: Mapping functions are PRIVATE inside repository
class AuthenticationRepository(...) {
    private fun RoomSession.toDomain(): Session = Session(
        sessionId = sessionId,
        isGuest = isGuest,
        expiresAt = expiresAt
    )

    private fun RemoteGuestSession.toLocalStorage(): RoomSession = RoomSession(
        sessionId = guestSessionId,
        isGuest = success,
        expiresAt = expiresAt
    )
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
    private lateinit var openSession: OpenSession

    @MockK
    private lateinit var validateInput: ValidateInput

    private lateinit var viewModel: LoginViewModel

    @Test
    fun `GIVEN valid credentials WHEN login button clicked THEN isLoggedIn is true`() = runTest {
        // Arrange
        every { validateInput(any(), any()) } returns null
        coEvery { openSession.open(any()) } returns null  // null = success

        viewModel = buildViewModel()

        // Act
        viewModel.onEvent(LoginEvent.SetUsername("user"))
        viewModel.onEvent(LoginEvent.SetPassword("password"))
        viewModel.onEvent(LoginEvent.LoginButtonClicked("user", "password"))
        advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.appError)
            assertTrue(state.isLoggedIn)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `GIVEN invalid credentials WHEN login button clicked THEN error state is emitted`() = runTest {
        // Arrange
        val appError = AppError(AppErrorCode.BAD_REQUEST, "Invalid credentials")
        every { validateInput(any(), any()) } returns null
        coEvery { openSession.open(any()) } returns appError

        viewModel = buildViewModel()

        // Act
        viewModel.onEvent(LoginEvent.LoginButtonClicked("user", "wrong"))
        advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertNotNull(state.appError)
            assertEquals(appError, state.appError)
            assertFalse(state.isLoggedIn)
        }
    }

    @Test
    fun `GIVEN guest login WHEN guest button clicked THEN isLoggedIn is true`() = runTest {
        // Arrange
        coEvery { openSession.open(LoginMethod.AsGuest) } returns null

        viewModel = buildViewModel()

        // Act
        viewModel.onEvent(LoginEvent.GuestButtonClicked)
        advanceUntilIdle()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertNull(state.appError)
            assertTrue(state.isLoggedIn)
        }
    }

    private fun buildViewModel() = LoginViewModel(
        ioDispatcher = coroutineTestRule.testDispatcher,
        openSession = openSession,
        validateInput = validateInput
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
        setContentWithState()

        onAllNodes(isEditable()).assertCountEquals(2)
        onNodeWithText("Login").assertExists().assertIsEnabled()
        onNodeWithText("Login as Guest").assertExists().assertIsEnabled()
    }

    @Test
    fun loadingState_showsProgressAndDisablesButtons() = composeTestRule.run {
        setContentWithState(isLoading = true)

        onNode(isCircularProgressIndicator()).assertExists()
        onNode(hasText("Login") and hasClickAction()).assertIsNotEnabled()
        onNode(hasText("Login as Guest") and hasClickAction()).assertIsNotEnabled()
    }

    @Test
    fun appError_showsErrorDialog() = composeTestRule.run {
        val error = AppError(
            code = AppErrorCode.LOCAL_ERROR,
            description = "Unexpected error"
        )
        setContentWithState(appError = error)

        onNodeWithText("Unexpected error").assertExists()
    }

    @Test
    fun loginButtonClick_dispatchesLoginEvent() = composeTestRule.run {
        var lastEvent: LoginEvent? = null
        setContentWithState(onEvent = { lastEvent = it })

        onAllNodes(isEditable())[0].performTextInput("user@mail.com")
        onAllNodes(isEditable())[1].performTextInput("12345678")
        onNodeWithText("Login").performClick()

        assertTrue(lastEvent is LoginEvent.LoginButtonClicked)
        val loginEvent = lastEvent as LoginEvent.LoginButtonClicked
        assertEquals("user@mail.com", loginEvent.username)
        assertEquals("12345678", loginEvent.password)
    }

    @Test
    fun guestButtonClick_dispatchesGuestEvent() = composeTestRule.run {
        var lastEvent: LoginEvent? = null
        setContentWithState(onEvent = { lastEvent = it })

        onNodeWithText("Login as Guest").performClick()

        assertTrue(lastEvent is LoginEvent.GuestButtonClicked)
    }

    private fun ComposeContentTestRule.setContentWithState(
        appError: AppError? = null,
        isLoading: Boolean = false,
        password: String = "",
        passwordError: String? = null,
        username: String = "",
        usernameError: String? = null,
        onEvent: (LoginEvent) -> Unit = {}
    ) {
        setContent {
            TmdbTheme {
                LoginScreen(
                    appError = appError,
                    isLoading = isLoading,
                    password = password,
                    passwordError = passwordError,
                    username = username,
                    usernameError = usernameError,
                    onEvent = onEvent
                )
            }
        }
    }
}
```

### Mocking API Calls

In unit tests, API services that return `Either` are mocked using MockK:

```kotlin
class AuthenticationRepositoryTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var authAPI: AuthenticationApi

    @MockK
    private lateinit var sessionDao: SessionDao

    @MockK
    private lateinit var accountDetailsRepository: AccountDetailsRepository

    private lateinit var repository: AuthenticationRepository

    @Before
    fun setup() {
        repository = AuthenticationRepository(authAPI, sessionDao, accountDetailsRepository)
    }

    @Test
    fun `GIVEN valid credentials WHEN open is called THEN session is saved and null returned`() = runTest {
        // Arrange
        val token = RemoteTokenResponse(success = true, requestToken = "valid_token", expiresAt = null)
        val session = RemoteSessionIdResponse(success = true, sessionId = "session123")
        val credentials = LoginMethod.AuthCredentials("user", "pass")

        coEvery { authAPI.createRequestToken() } returns token.right()
        coEvery { authAPI.authorizeToken(any()) } returns token.right()
        coEvery { authAPI.createSessionId(any()) } returns session.right()
        coEvery { sessionDao.insertSession(any()) } returns 1L
        coEvery { accountDetailsRepository.fetch() } returns null

        // Act
        val result = repository.open(credentials)

        // Assert
        assertNull(result)
        coVerify { sessionDao.insertSession(any()) }
    }

    @Test
    fun `GIVEN remote error WHEN open is called THEN AppError is returned`() = runTest {
        // Arrange
        val remoteError = RemoteError(statusCode = 401, statusMessage = "Unauthorized")
        val credentials = LoginMethod.AuthCredentials("user", "pass")

        coEvery { authAPI.createRequestToken() } returns remoteError.left()

        // Act
        val result = repository.open(credentials)

        // Assert
        assertNotNull(result)
        assertEquals(AppErrorCode.NOT_FOUND, result?.code)
    }

    @Test
    fun `GIVEN guest login WHEN open is called THEN guest session is saved`() = runTest {
        // Arrange
        val guestSession = RemoteGuestSession(
            success = true,
            guestSessionId = "guest123",
            expiresAt = "2026-01-12 00:00:00 UTC"
        )

        coEvery { authAPI.createGuestSession() } returns guestSession.right()
        coEvery { sessionDao.insertSession(any()) } returns 1L

        // Act
        val result = repository.open(LoginMethod.AsGuest)

        // Assert
        assertNull(result)
        coVerify { 
            sessionDao.insertSession(match { it.isGuest && it.sessionId == "guest123" })
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
   mkdir -p feature/newfeature/{newfeature_domain,newfeature_data,newfeature_ui}/src/{main,test,androidTest}/kotlin
   ```

2. **Create `build.gradle.kts` for each layer**:

   **Domain** (`newfeature_domain/newfeature_domain.gradle.kts`):
   ```kotlin
   plugins {
       alias(libs.plugins.kotlinModuleConventionPlugin)
   }
   
   dependencies {
       implementation(projects.feature.core.coreDomain)
       implementation(libs.arrowCore)
       implementation(libs.pagingJVM)
   }
   ```

   **Data** (`newfeature_data/newfeature_data.gradle.kts`):
   ```kotlin
   plugins {
       alias(libs.plugins.frameworkModuleConventionPlugin)
   }
   
   dependencies {
       implementation(projects.feature.newfeature.newfeatureDomain)
       implementation(projects.feature.core.coreData)
   }
   ```

   **UI** (`newfeature_ui/newfeature_ui.gradle.kts`):
   ```kotlin
   plugins {
       alias(libs.plugins.uiModuleConventionPlugin)
   }
   
   dependencies {
       implementation(projects.feature.newfeature.newfeatureDomain)
       implementation(projects.feature.core.coreUi)
   }
   ```

3. **Add to `settings.gradle.kts`**:
   ```kotlin
   include(":feature:newfeature:newfeature_domain")
   include(":feature:newfeature:newfeature_data")
   include(":feature:newfeature:newfeature_ui")
   ```

4. **Update `app/app.gradle.kts`**:
   ```kotlin
   dependencies {
       implementation(projects.feature.newfeature.newfeatureDomain)
       implementation(projects.feature.newfeature.newfeatureData)
       implementation(projects.feature.newfeature.newfeatureUi)
   }
   ```

5. **Create package structure**:
   ```
   newfeature_domain/src/main/kotlin/com/davidluna/tmdb/newfeature_domain/
   ├── entities/          # Domain models
   └── usecases/          # fun interface use cases
   
   newfeature_data/src/main/kotlin/com/davidluna/tmdb/newfeature_data/
   ├── di/                # Koin modules
   ├── framework/
   │   ├── remote/        # API services and models
   │   └── local/         # Room entities and DAOs
   ├── repositories/      # Use case implementations
   └── utils/             # Helpers
   
   newfeature_ui/src/main/kotlin/com/davidluna/tmdb/newfeature_ui/
   ├── di/                # Koin modules for ViewModels
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

1. **Define Remote model in data layer** (`framework/remote/model/`):
   ```kotlin
   // feature/media/media_data/src/main/kotlin/.../framework/remote/model/RemoteVideo.kt
   @Serializable
   data class RemoteVideo(
       @SerialName("id") val id: String?,
       @SerialName("key") val key: String?,
       @SerialName("site") val site: String?,
       @SerialName("type") val type: String?
   )

   @Serializable
   data class RemoteVideosResponse(
       @SerialName("id") val id: Int?,
       @SerialName("results") val results: List<RemoteVideo>
   )
   ```

2. **Add API service method** (`framework/remote/`):
   ```kotlin
   // RemoteMediaService.kt
   interface RemoteMediaService {
       @GET("{endpoint}/videos")
       suspend fun getVideos(@Path("endpoint", encoded = true) endpoint: String): Either<RemoteError, RemoteVideosResponse>
   }
   ```

3. **Define use case in domain layer** (`usecases/`):
   ```kotlin
   // feature/media/media_domain/src/main/kotlin/.../usecases/GetCatalogVideos.kt
   fun interface GetCatalogVideos : suspend (Catalog, Int) -> Either<AppError, List<Video>>
   ```

4. **Create domain entity** (`entities/`):
   ```kotlin
   // feature/media/media_domain/src/main/kotlin/.../entities/details/Video.kt
   data class Video(
       val id: String,
       val key: String
   )
   ```

5. **Implement use case in data layer** (`repositories/`):
   ```kotlin
   // feature/media/media_data/src/main/kotlin/.../repositories/CatalogVideosRepository.kt
   class CatalogVideosRepository(
       private val local: MediaVideosDao,
       private val remote: RemoteMediaService,
       private val isCacheExpired: IsCacheExpired,
   ) : GetCatalogVideos {

       override suspend operator fun invoke(
           catalog: Catalog,
           mediaId: Int,
       ): Either<AppError, List<Video>> = tryCatchSuspend {
           val localVideos = local.getVideo(mediaId)
           val isCacheExpired = isCacheExpired(localVideos.firstOrNull()?.savedTimeMillis)
           if (localVideos.isNotEmpty() && !isCacheExpired) {
               localVideos.map { it.toDomain() }
           } else {
               val endpoint = catalog.toEndpointPath(mediaId)
               val remoteVideos = fetchRemoteVideos(endpoint, mediaId)
               local.cacheVideos(remoteVideos, isCacheExpired).map { it.toDomain() }
           }
       }

       private suspend fun fetchRemoteVideos(endpoint: String, mediaId: Int): List<RoomVideo> =
           remote.getVideos(endpoint).fold(
               ifLeft = { emptyList() },
               ifRight = { response ->
                   response.results.mapNotNull {
                       if (it.site?.lowercase() == "youtube" && it.type?.lowercase() == "trailer") {
                           it.toLocalStorage(mediaId)
                       } else null
                   }
               }
           )

       // Mapping functions are PRIVATE
       private fun RemoteVideo.toLocalStorage(mediaId: Int): RoomVideo = RoomVideo(
           id = id.orEmpty(),
           key = key.orEmpty(),
           mediaId = mediaId,
           savedTimeMillis = System.currentTimeMillis()
       )

       private fun RoomVideo.toDomain(): Video = Video(id = id, key = key)
   }
   ```

6. **Bind in Koin module** (`di/`):
   ```kotlin
   val mediaDataModule = module {
       singleOf(::CatalogVideosRepository) bind GetCatalogVideos::class
   }
   ```

7. **Use in ViewModel**:
   ```kotlin
   class VideoPlayerViewModel(
       private val getCatalogVideos: GetCatalogVideos,
       private val ioDispatcher: CoroutineDispatcher,
   ) : ViewModel() {

       fun loadVideos(catalog: Catalog, mediaId: Int) {
           viewModelScope.launch(ioDispatcher) {
               _state.update { it.copy(isLoading = true) }
               getCatalogVideos(catalog, mediaId)
                   .fold(
                       ifLeft = { error -> _state.update { it.copy(appError = error, isLoading = false) } },
                       ifRight = { videos -> _state.update { it.copy(videos = videos, isLoading = false) } }
                   )
           }
       }
   }
   ```

### Adding a New Room Entity and Query

1. **Define entity in data layer** (`framework/local/database/entities/`):
   ```kotlin
   @Entity(tableName = "videos")
   data class RoomVideo(
       @PrimaryKey val id: String,
       val key: String,
       val mediaId: Int,
       val savedTimeMillis: Long
   )
   ```

2. **Create DAO** (`framework/local/database/dao/`):
   ```kotlin
   @Dao
   interface MediaVideosDao {
       @Query("SELECT * FROM videos WHERE mediaId = :mediaId")
       suspend fun getVideo(mediaId: Int): List<RoomVideo>

       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insertAll(videos: List<RoomVideo>)

       @Query("DELETE FROM videos WHERE mediaId = :mediaId")
       suspend fun deleteByMediaId(mediaId: Int)

       @Transaction
       suspend fun cacheVideos(videos: List<RoomVideo>, shouldClear: Boolean): List<RoomVideo> {
           if (shouldClear && videos.isNotEmpty()) {
               deleteByMediaId(videos.first().mediaId)
           }
           insertAll(videos)
           return videos
       }
   }
   ```

3. **Add to database**:
   ```kotlin
   @Database(
       entities = [RoomVideo::class, RoomSession::class],
       version = 1,
       exportSchema = true
   )
   abstract class TmdbDatabase : RoomDatabase() {
       abstract fun mediaVideosDao(): MediaVideosDao
       abstract fun sessionDao(): SessionDao
   }
   ```

4. **Define use case in domain** (`usecases/`):
   ```kotlin
   // For returning Either<AppError, T>
   fun interface GetCatalogVideos : suspend (Catalog, Int) -> Either<AppError, List<Video>>

   // For returning Flow<T>
   interface ObserveSession {
       val session: Flow<Session?>
   }
   ```

5. **Implement use case in repository with private mappers** (`repositories/`):
   ```kotlin
   class CatalogVideosRepository(
       private val local: MediaVideosDao,
       private val remote: RemoteMediaService,
       private val isCacheExpired: IsCacheExpired,
   ) : GetCatalogVideos {

       override suspend operator fun invoke(
           catalog: Catalog,
           mediaId: Int,
       ): Either<AppError, List<Video>> = tryCatchSuspend {
           val localVideos = local.getVideo(mediaId)
           val expired = isCacheExpired(localVideos.firstOrNull()?.savedTimeMillis)
           if (localVideos.isNotEmpty() && !expired) {
               localVideos.map { it.toDomain() }
           } else {
               val remoteVideos = fetchRemoteVideos(catalog.toEndpointPath(mediaId), mediaId)
               local.cacheVideos(remoteVideos, expired).map { it.toDomain() }
           }
       }

       // Mapping functions are PRIVATE inside the repository
       private fun RoomVideo.toDomain(): Video = Video(id = id, key = key)

       private fun RemoteVideo.toLocalStorage(mediaId: Int): RoomVideo = RoomVideo(
           id = id.orEmpty(),
           key = key.orEmpty(),
           mediaId = mediaId,
           savedTimeMillis = System.currentTimeMillis()
       )
   }
   ```

6. **Bind in Koin module** (`di/`):
   ```kotlin
   val mediaDataModule = module {
       // DAO from database
       single { get<TmdbDatabase>().mediaVideosDao() }
       
       // Repository bound to use case interface
       singleOf(::CatalogVideosRepository) bind GetCatalogVideos::class
   }
   ```

7. **Use in ViewModel**:
   ```kotlin
   class VideoPlayerViewModel(
       private val getCatalogVideos: GetCatalogVideos,
       private val ioDispatcher: CoroutineDispatcher,
   ) : ViewModel() {

       private val _state = MutableStateFlow(State())
       val state = _state.stateIn(
           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(5_000),
           initialValue = State()
       )

       @Stable
       data class State(
           val videos: List<Video> = emptyList(),
           val isLoading: Boolean = false,
           val appError: AppError? = null
       )

       fun loadVideos(catalog: Catalog, mediaId: Int) {
           viewModelScope.launch(ioDispatcher) {
               _state.update { it.copy(isLoading = true) }
               getCatalogVideos(catalog, mediaId)
                   .fold(
                       ifLeft = { error -> _state.update { it.copy(appError = error, isLoading = false) } },
                       ifRight = { videos -> _state.update { it.copy(videos = videos, isLoading = false) } }
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

#### 2. Koin Runtime Errors

**Solution**:
- Ensure all dependencies are declared in Koin modules
- Check that modules are registered in the application's `startKoin { }` block
- Verify that `get()` calls match the expected types
- Use `singleOf(::Class) bind Interface::class` for binding implementations
- Clean and rebuild: `./gradlew clean :app:build`

#### 3. Compose Preview Not Working

**Solution**:
- Ensure composable has `@Preview` annotation
- Check Android Studio version (should be Ladybug Feature Drop 2024.2.2+)
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

**Best practices followed in this project**:
- Use `key` parameter in `LazyVerticalGrid` items to help Compose identify items
- Prefer stateless composables that receive state as parameters
- Use `collectAsStateWithLifecycle()` for StateFlow collection
- Avoid creating lambdas inside composition - pass them as parameters from parent

### Network Optimization

**Patterns used in this project**:
- Cache-first strategy: Check local database before making API calls
- Use `IsCacheExpired` to determine when to refresh data
- Pagination with Paging 3 library for large datasets

```kotlin
// Pattern from CatalogVideosRepository
override suspend operator fun invoke(
    catalog: Catalog,
    mediaId: Int,
): Either<AppError, List<Video>> = tryCatchSuspend {
    val localVideos = local.getVideo(mediaId)
    val isCacheExpired = isCacheExpired(localVideos.firstOrNull()?.savedTimeMillis)
    if (localVideos.isNotEmpty() && !isCacheExpired) {
        localVideos.map { it.toDomain() }
    } else {
        val remoteVideos = fetchRemoteVideos(endpoint, mediaId)
        local.cacheVideos(remoteVideos, isCacheExpired).map { it.toDomain() }
    }
}
```

### Memory Optimization

**Patterns used in this project**:
- Coroutines in `viewModelScope` are automatically cancelled when ViewModel is cleared
- Use `SharingStarted.WhileSubscribed(5_000)` to stop collecting when UI is not visible
- Prefer `stateIn()` over collecting in init block

```kotlin
// Pattern from LoginViewModel
val state = _state.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = State()
)
```

### Database Optimization

**Patterns used in this project**:
- Use `@Transaction` for operations that need atomicity
- Batch insert operations with `insertAll()`
- Return `Long` from insert to verify success

```kotlin
// Pattern from MediaVideosDao
@Transaction
suspend fun cacheVideos(videos: List<RoomVideo>, shouldClear: Boolean): List<RoomVideo> {
    if (shouldClear && videos.isNotEmpty()) {
        deleteByMediaId(videos.first().mediaId)
    }
    insertAll(videos)
    return videos
}
```

---

## Additional Resources

- [Android Developers Guide](https://developer.android.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Koin Dependency Injection](https://insert-koin.io/docs/quickstart/android)
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
