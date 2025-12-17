# TMDB-Android — Project Context

This document summarizes the repository layout, conventions, architecture, dependencies and common build/run commands for the TMDB-Android project.

Date: 2025-12-17

---

## 1) Project structure (high level)

Top-level (relevant files / directories):

- `settings.gradle.kts` — root project configuration, includes all modules.
- `Tmdb2024.gradle.kts` — shared Gradle tasks for the multi-module project.
- `gradle/libs.versions.toml` — dependency versions catalog.
- `build-logic/` — custom convention plugins and shared build logic (defines project-wide conventions and bundles).
- `app/` — Android application module.
  - `app.gradle.kts` — module build file.
  - `src/main` — application source (see `App.kt`).
  - `google-services.json` — Firebase configuration (present but not included in this document).
- `feature/` — feature modules, split into domain/framework/ui for each feature
  - `feature/auth/` (auth_ui, auth_domain, auth_framework)
  - `feature/media/` (media_ui, media_domain, media_framework)
  - `feature/core/`  (core_ui, core_domain, core_framework)
- `test_shared/` — shared test utilities and test fixtures.
- `gradle/` — Gradle wrapper and versions catalog used by the project.

Notes:
- The project follows a modularized structure: each feature is split into `_domain`, `_framework`, and `_ui` modules.
- `build-logic/convention` contains custom Gradle plugins used across the modules (UI, framework, Kotlin, room, etc.). These apply dependencies bundles and common settings.

## 2) Code conventions and style

Conventions enforced by the project (inferred from `build-logic` and code):

- Package naming: All packages follow `com.davidluna.tmdb` base namespace. The base namespace is defined at `build-logic/convention/src/main/kotlin/com/davidluna/tmdb/convention/constants/Constants.kt` as `com.davidluna.tmdb`.

- Module namespaces: modules set `android.namespace` to `${Constants.NAMESPACE}.<module_suffix>` (for example `...auth_ui`).

- Kotlin/Java target: Java 17 compatibility is configured for JVM modules. Kotlin jvm target is set to 17 in the Kotlin convention plugin.

- File and class naming:
  - Kotlin classes use UpperCamelCase for types.
  - Files typically match the single top-level class or composable they contain (common Kotlin convention).

- Resources / XML conventions:
  - Compose-first UI: project uses Jetpack Compose (so standard XML view naming is minimal). When XML is used, follow Android resource naming conventions (lowercase_underscore).

- Dependency and plugin management:
  - Versions and libraries are declared centrally in `gradle/libs.versions.toml` and referenced through the Gradle catalog (`libs` in convention plugins).
  - `build-logic` provides convention plugins (`tmdb.ui.module.plugin`, `tmdb.framework.module.plugin`, etc.) that apply recommended plugins and dependency bundles.

- Testing conventions:
  - Test fixtures and shared test utilities are provided via `test_shared` module and convention bundles include unit & android testing dependencies.

- Linting / formatting:
  - No explicit mentions of ktlint, detekt, or spotless were found in the repository. The project relies on convention plugins for consistent dependency and JVM settings; if formatting/lint rules exist they may be applied by IDE or CI outside this repository or via Android Studio defaults.

## 3) Architecture

Inferred architecture from module layout and code:

- Modular, layered feature architecture:
  - Each feature (auth, media, core) is split into three modules: `*_domain`, `*_framework`, `*_ui`.
    - `*_domain` — contains domain-level models and use cases (pure business logic).
    - `*_framework` — contains implementation details such as data sources, repositories, network, local DB and DI bindings.
    - `*_ui` — Compose UI components, presenters/viewmodels and navigation related to the feature.

- Patterns & libraries:
  - Dependency Injection: Hilt is used (see `@HiltAndroidApp` in `app/src/main/.../App.kt`, Hilt-related dependencies in `build-logic`).
  - UI: Jetpack Compose is used throughout (`@Composable` annotations in `feature/*/*_ui`).
  - ViewModels / State: ViewModels are used and Hilt-powered (`HiltViewModel` kept in proguard rules). ViewModels use Coroutine dispatchers injected (see `LoginViewModel`).
  - Coroutines & Flow: Kotlin Coroutines are used in core and framework modules for async work.
  - Networking: Retrofit + OkHttp are included (plus a kotlinx-serialization converter) and configured in convention bundles.
  - Persistence: Room dependency and room plugin exists in the catalog; framework modules likely provide Room implementations.
  - Navigation: Jetpack Navigation for Compose (`navigation-compose`) is included.
  - Image loading: Coil Compose is used for images.
  - Testing: project includes common testing libraries (JUnit4, MockK, Turbine, androidx test runner, mockwebserver, Compose testing libs).

- Additional details:
  - `App` calls `InstallNotificationChannels` via DI on `onCreate()` — small app-level inizialization logic.
  - Build-logic convention plugins bundle common libraries and testing dependencies so module build files stay minimal.

Overall architecture: Modular + MVVM-ish (ViewModels + UseCases + Repositories) with Clean-ish separation of domain/framework/ui.

## 4) Dependencies (key libraries & where versions live)

Primary dependency management:
- All versions are declared in `gradle/libs.versions.toml` and used via `libs` catalog.
- Convention plugins in `build-logic/convention` reference the catalog.

Key libraries (non-exhaustive):

- Android / Jetpack
  - androidx.core:core-ktx
  - androidx.activity:activity-compose
  - androidx.compose (UI, material3, navigation, tooling)
  - androidx.paging (paging-compose)
  - androidx.datastore (preferences)
  - androidx.room (room-runtime / ktx / compiler)
  - androidx.hilt:hilt-navigation-compose

- Dependency Injection
  - Dagger Hilt (hilt-android, hilt-compiler)

- Networking
  - Retrofit + okhttp3 + logging-interceptor
  - kotlinx-serialization converter for Retrofit

- Image Loading
  - Coil (coil-compose)

- Coroutines / Kotlin
  - kotlinx-coroutines-core
  - kotlinx-serialization-json
  - kotlinx-datetime

- Testing
  - junit, mockk, coroutines-test, turbine, mockwebserver
  - AndroidX test runner, rules, espresso, compose testing libs
  - Hilt testing (hilt-android-testing)

Where to look:
- `gradle/libs.versions.toml` — full list of versions and library coordinates.
- `build-logic/convention` — bundles show which libraries are applied to which module types.

## 5) Build & run commands

Common Gradle commands (run from project root):

- Clean:

```bash
./gradlew clean
```

- Assemble debug APK for app:

```bash
./gradlew :app:assembleDebug
```

- Build the whole project:

```bash
./gradlew build
```

- Run unit tests (all modules):

```bash
./gradlew test
```

- Run instrumented Android tests for modules that end with `ui` or `app` (task aggregator provided):

```bash
./gradlew connectedUiTests
```

- Collect all UI Android test reports:

```bash
./gradlew aggregateUiAndroidTestReports
```

- Install the debug APK to a connected device (example):

```bash
./gradlew :app:installDebug
```

Notes / environment setup:
- `local.properties` should contain the Android SDK location. The repository includes `google-services.json` in `app/` — if you rebuild or run Firebase features, ensure it is valid and not exposing private keys.
- `build-logic` defines constants (including `API_KEY`, `BASE_URL`, and keystore placeholders) in `Constants.kt`. These are placeholders; the real API key and signing credentials should be placed in secure locations (e.g., environment variables, CI secrets, or a `local.properties` file excluded from VCS). Do not commit secrets.
- If you need Firebase functionality, ensure `google-services.json` is present and configured for your Firebase project.

## 6) Assumptions & missing info

- API keys and secrets: `Constants.API_KEY` is set to `MY_API_KEY` in `build-logic`; actual API key is not present in the repo. You must supply it via a secure mechanism (local.properties, CI env vars or Gradle properties). Do not check real keys into source control.
- Signing config: `Constants` contains keystore placeholders. No real signing keystore/passwords are in the repo.
- CI configuration: No CI (GitHub Actions / Bitrise / Fastlane) files were found in the repository root — if a CI pipeline exists externally, it is not included.
- Formatter / linting tools: No explicit ktlint/detekt/spotless configuration was discovered. The project may rely on IDE defaults or have CI steps outside the repo.

## 7) Short next steps / recommendations

- Add a `contexts/PROJECT_CONTEXT.md` (this file) to document repository architecture and onboarding steps — done.
- Provide a secure way to inject API keys and keystore credentials: recommend documenting env var names or using `local.properties` with an example `.env.example`/README note.
- Add lint/format CI checks (ktlint, detekt, spotless) if not already enforced elsewhere.
- Consider adding a small `README.md` at the root with quick-start steps (install SDK, emulator, how to run the app and tests).

---
