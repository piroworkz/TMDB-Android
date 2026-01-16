# PROJECT_CONTEXT.md

## 1. Overview

**Project name:** Tmdb2024  
**Description:** An Android application for The Movie Database (TMDB), built with Kotlin and Jetpack Compose.  
**Package namespace:** `com.davidluna.tmdb`

**SDK versions:**
- Compile SDK: 36
- Target SDK: 35
- Min SDK: 28
- Java version: 17

---

## 2. Repository Layout (Top-level directories)

| Directory       | Purpose                                                                 |
|-----------------|-------------------------------------------------------------------------|
| `app/`          | Main Android application module                                         |
| `build-logic/`  | Gradle convention plugins (composite build)                             |
| `context/`      | Project documentation (CONSTITUTION.md, templates, etc.)                |
| `feature/`      | Feature modules organized by vertical slice (auth, media, core)         |
| `gradle/`       | Gradle wrapper and version catalog (`libs.versions.toml`)               |
| `test_shared/`  | Shared test utilities and fixtures for unit tests                       |

---

## 3. Modules

### Module list

| Module                             | Type            | Convention Plugin                    | Purpose                                       |
|------------------------------------|-----------------|--------------------------------------|-----------------------------------------------|
| `:app`                             | Application     | `tmdb.android.application`           | App entry point, DI setup, main navigation    |
| `:feature:auth:auth_ui`            | Android Library | `tmdb.ui.module.plugin`              | Auth UI layer (Compose screens, ViewModels)   |
| `:feature:auth:auth_domain`        | Kotlin (JVM)    | `tmdb.kotlin.module.plugin`          | Auth domain layer (entities, use cases, interfaces) |
| `:feature:auth:auth_data`          | Android Library | `tmdb.room.module.plugin`            | Auth data layer (repositories, Room, network) |
| `:feature:media:media_ui`          | Android Library | `tmdb.ui.module.plugin`              | Media UI layer (Compose screens, ViewModels)  |
| `:feature:media:media_domain`      | Kotlin (JVM)    | `tmdb.kotlin.module.plugin`          | Media domain layer (entities, use cases, interfaces) |
| `:feature:media:media_data`        | Android Library | `tmdb.room.module.plugin`            | Media data layer (repositories, Room, Paging) |
| `:feature:core:core_ui`            | Android Library | `tmdb.ui.module.plugin`              | Shared UI components, themes, composables     |
| `:feature:core:core_domain`        | Kotlin (JVM)    | `tmdb.kotlin.module.plugin`          | Shared domain entities and use cases          |
| `:feature:core:core_data`          | Android Library | `tmdb.framework.module.plugin`       | Shared data: DataStore, Firebase, network     |
| `:test_shared`                     | Kotlin (JVM)    | `tmdb.test.shared.plugin`            | Shared test utilities, fakes, JSON fixtures   |

### Build file naming
Each module uses a custom build file name: `<module-name>.gradle.kts` (e.g., `app.gradle.kts`, `core_domain.gradle.kts`).

---

## 4. Architecture & Layering

The project follows a **Clean Architecture** approach with vertical feature slicing:

```
feature/
  └── <feature>/
       ├── <feature>_domain/   ← Pure Kotlin (JVM). Use cases, entities, repository interfaces.
       ├── <feature>_data/     ← Android Library. Repository implementations, network, database.
       └── <feature>_ui/       ← Android Library. Compose UI, ViewModels, navigation.
```

**Dependency rules (as enforced by module structure):**
- `*_domain` modules are pure Kotlin—no Android framework dependencies.
- `*_ui` modules depend on `*_domain` but NOT on `*_data`.
- `*_data` modules depend on `*_domain` for interface contracts.
- `app` aggregates all feature modules and wires DI.

**Core modules (`feature/core/*`) provide:**
- Shared entities and use cases (`core_domain`)
- Shared data infrastructure: DataStore, Firebase, location, network client (`core_data`)
- Shared Compose components and theming (`core_ui`)

---

## 5. Tooling & Build System

### Gradle setup
- **Gradle wrapper:** Yes (`gradlew`, `gradlew.bat`)
- **Kotlin DSL:** All build scripts use `.gradle.kts`
- **Composite build:** `build-logic/` included via `pluginManagement { includeBuild("build-logic") }`
- **Typesafe project accessors:** Enabled (`projects.feature.core.coreDomain`, etc.)

### Version catalog
Located at `gradle/libs.versions.toml`. Defines:
- All dependency versions
- Library aliases (e.g., `libs.composeBom`, `libs.koinBom`, `libs.roomRuntime`)
- Plugin aliases (e.g., `libs.plugins.androidApplication`, `libs.plugins.kotlinAndroid`)
- Convention plugin aliases (e.g., `libs.plugins.uiModuleConventionPlugin`)

### Convention plugins (`build-logic/convention/`)
| Plugin ID                          | Implementation Class                         | Purpose                                     |
|------------------------------------|----------------------------------------------|---------------------------------------------|
| `tmdb.android.application`         | `AndroidApplicationConventionPlugin`         | App module: SDK config, Compose, Koin, HTTP |
| `tmdb.framework.module.plugin`     | `FrameworkModuleConventionPlugin`            | Android data modules: HTTP, Koin, testing   |
| `tmdb.ui.module.plugin`            | `UiModuleConventionPlugin`                   | UI modules: Compose, Koin, testing          |
| `tmdb.kotlin.module.plugin`        | `KotlinModuleConventionPlugin`               | Pure Kotlin/JVM domain modules              |
| `tmdb.room.module.plugin`          | `RoomModuleConvention`                       | Room database modules: Room, Koin, HTTP     |
| `tmdb.test.shared.plugin`          | `TestSharedConventionPlugin`                 | Test shared module: JUnit, MockK, coroutines|

### Key dependencies (from `gradle/libs.versions.toml`)
- **Compose BOM:** 2026.01.00
- **Kotlin:** 2.3.0
- **Koin BOM:** 4.1.1
- **Room:** 2.8.4
- **Retrofit:** 3.0.0
- **OkHttp:** 5.3.2
- **Arrow Core:** 2.2.1.1
- **Paging:** 3.3.6
- **Firebase BOM:** 34.8.0
- **Kotlinx Coroutines:** 1.10.2

### Lint / Static analysis / Formatters
**Not found.** No detekt, ktlint, or spotless configuration detected in the repository.

---

## 6. Dependency Injection

**Framework:** Koin

**Setup location:** `app/src/main/kotlin/com/davidluna/tmdb/app/di/StartDI.kt`

**Initialization:** Called from `App.onCreate()` via `startDi()` extension function.

**Module organization:**
- Each feature layer defines Koin modules in its `di/` package.
- Modules are aggregated in `StartDI.kt` and passed to `startKoin { modules(...) }`.
- ViewModels are registered using `viewModelOf(::ViewModel)`.

---

## 7. Testing

### Unit tests
- **Location:** `src/test/kotlin/`
- **Test dependencies:** JUnit 4, MockK, Kotlinx Coroutines Test, Turbine
- **Shared fixtures:** `test_shared` module provides:
    - JSON fixtures (`src/main/resources/raw/*.json`)
    - Common test utilities (`src/main/kotlin/`)
- **Test fixtures (Gradle):** Enabled in `auth_data` and `media_data` via `testFixtures { enable = true }`

### Instrumented / UI tests
- **Location:** `src/androidTest/kotlin/`
- **Test runner:** `androidx.test.runner.AndroidJUnitRunner`
- **Options:** `clearPackageData = true`, `animationsDisabled = true`

### Root-level test tasks
Defined in `Tmdb2024.gradle.kts`:
- `connectedUiTests` — Runs `connectedDebugAndroidTest` for all UI modules and `app`
- `aggregateUiAndroidTestReports` — Collects all instrumented test reports to `build/reports/connectedUiTests/`

### Instrumented tests runtime note
Instrumented/UI tests may be slow to execute. Guidelines for when agents should run them are documented in `AGENTS.md`.

---

## 8. Local Development

### Prerequisites
- Android SDK (path configured via `local.properties`)
- JDK 17
- NDK (for native secrets via JNI)

### Build the project
```bash
./gradlew assembleDebug
```

### Run unit tests (all modules)
```bash
./gradlew test
```

### Run unit tests (specific module)
```bash
./gradlew :feature:media:media_data:test
```

### Run instrumented tests (slow, opt-in)
```bash
./gradlew connectedUiTests
```

### Aggregate instrumented test reports
```bash
./gradlew aggregateUiAndroidTestReports
```
Reports output: `build/reports/connectedUiTests/`

---

## 9. Configuration & Secrets

### Secrets injection mechanism (observed)
The repository integrates secrets/config via a native/JNI approach and Gradle properties.

Observed components:
1. Gradle properties (e.g., `gradle.properties` or `local.properties`) providing values such as API key and base URL
2. NDK / native build integration (via `externalNativeBuild` or convention plugin configuration)
3. JNI native code exposing values to Kotlin (e.g., `app/src/main/jni/`)

If any part of this mechanism changes, update this section to match the repository state.

### Required properties for build
Define in `gradle.properties` or `~/.gradle/gradle.properties`:
```properties
MY_API_KEY=<your-tmdb-api-key>
BASE_URL=<tmdb-base-url>
```

### Signing config (release builds)
Expected release signing properties (observed in repository config/code):
- `KEY_ALIAS`
- `KEY_PASSWORD`
- `STORE_FILE`
- `STORE_PASSWORD`

### Files NOT to commit
- `local.properties` (SDK path, secrets)
- API keys, tokens, credentials

---

## 10. Common Commands

| Task                                | Command                                          |
|-------------------------------------|--------------------------------------------------|
| Build debug APK                     | `./gradlew assembleDebug`                        |
| Build release APK                   | `./gradlew assembleRelease`                      |
| Run all unit tests                  | `./gradlew test`                                 |
| Run unit tests for a module         | `./gradlew :<module-path>:test`                  |
| Run instrumented tests (slow)       | `./gradlew connectedUiTests`                     |
| Aggregate instrumented test reports | `./gradlew aggregateUiAndroidTestReports`        |
| Clean build                         | `./gradlew clean`                                |
| List available tasks                | `./gradlew tasks`                                |

---

## 11. References

- **`context/CONSTITUTION.md`** — Non-negotiable repository-wide rules for development
- **`AGENTS.md`** — Operating protocol for automated agents, TDD policy, Git/PR conventions

---

## 12. Conflicts / Questions

None detected.
