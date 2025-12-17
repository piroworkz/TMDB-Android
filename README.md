# TMDB-Android

A modern, modular Android application built with Kotlin and Jetpack Compose, following Clean Architecture and SOLID principles. This project demonstrates a professional, scalable approach to building maintainable and testable Android applications.

## 🎯 Project Overview

**TMDB-Android** is a feature-rich Android application that showcases best practices in modern Android development:
- **Modular Architecture**: Each feature is split into `domain`, `framework`, and `ui` layers
- **Jetpack Compose**: Modern declarative UI framework
- **Kotlin Coroutines**: Asynchronous operations and reactive data flows
- **Dependency Injection with Hilt**: Centralized dependency management
- **Clean Architecture**: Separation of concerns with clear layer boundaries
- **Comprehensive Testing**: Unit tests, integration tests, and instrumented UI tests

## 📦 Project Structure

```
TMDB-Android/
├── app/                          # Android application module
├── feature/                       # Feature modules
│   ├── auth/                     # Authentication feature
│   │   ├── auth_domain/          # Business logic & entities
│   │   ├── auth_framework/       # Data sources & repositories
│   │   └── auth_ui/              # Compose UI & ViewModels
│   ├── media/                    # Media (movies/shows) feature
│   │   ├── media_domain/
│   │   ├── media_framework/
│   │   └── media_ui/
│   └── core/                     # Shared core functionality
│       ├── core_domain/          # Shared domain models
│       ├── core_framework/       # Shared infrastructure
│       └── core_ui/              # Shared UI components
├── build-logic/                  # Custom Gradle convention plugins
├── test_shared/                  # Shared testing utilities
└── gradle/libs.versions.toml     # Centralized dependency management
```

## 🏗️ Architecture

The project follows a **modular, layered Clean Architecture**:

- **Domain Layer**: Pure Kotlin/business logic, use cases, and entities (no Android dependencies)
- **Framework Layer**: Data sources, repositories, network calls, database, and DI configuration
- **UI Layer**: Jetpack Compose components, ViewModels, and navigation

### S.O.L.I.D. Principles

- **Single Responsibility**: Each class has one reason to change
- **Open-Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Subtypes must be substitutable for their base types
- **Interface Segregation**: Depend on specific interfaces, not general ones
- **Dependency Inversion**: Depend on abstractions, not concrete implementations

## ✨ Key Features

- **Modern UI with Jetpack Compose**: Declarative UI framework with material design 3
- **Efficient Networking**: Retrofit + OkHttp with kotlinx-serialization
- **Local Persistence**: Room database for caching
- **Reactive Data Flow**: Kotlin Flow and Coroutines
- **Image Loading**: Coil for efficient image caching and loading
- **Modular Design**: Features can be developed independently
- **Comprehensive Testing**: Unit tests, integration tests, and instrumented UI tests
- **Dependency Injection**: Hilt for automatic DI container management
- **Firebase Integration**: Analytics, Crashlytics, Cloud Messaging, and Performance Monitoring

## 🎁 Benefits of This Approach

- **Cleaner Code**: Modular structure makes code easier to understand and maintain
- **Enhanced Testability**: Isolated layers enable comprehensive unit and integration testing
- **Reduced Coupling**: Dependencies flow in one direction; features are loosely coupled
- **Improved Scalability**: New features can be added without affecting existing modules
- **Better Code Reusability**: Shared components via `core_*` modules
- **Team Collaboration**: Teams can work on different features independently

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: 2025.2.2 (Otter Feature Drop) or later
- **JDK**: 17 (configured for this project)
- **Gradle**: Configured in `gradle-wrapper` (no manual installation needed)

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/TMDB-Android.git
   cd TMDB-Android
   ```

2. **Add your TMDB API key** to `local.properties` (project root):
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```
   ⚠️ **Important**: Do not commit `local.properties` to version control.

3. **Open the project** in Android Studio and let Gradle sync automatically. The project uses a centralized versions catalog at `gradle/libs.versions.toml` to manage all dependency versions.

### Configuration

For release builds and API configuration, keep sensitive values out of source control by placing them in `local.properties` (project root):

```properties
# TMDB API configuration
TMDB_API_KEY=your_api_key_here

# Signing / Release Keystore (optional)
STORE_FILE=/absolute/or/relative/path/to/keystore.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

## 📚 Tech Stack & Dependencies

### Core Dependencies (Key Libraries)

**Android & Jetpack**
- `androidx.activity:activity-compose` (1.12.1) - Activity integration with Compose
- `androidx.compose.*` (2025.12.00) - Jetpack Compose BOM
- `androidx.compose.material3:material3` - Material Design 3 components
- `androidx.navigation:navigation-compose` (2.9.6) - Navigation for Compose
- `androidx.room:room-*` (2.8.4) - Local database with Room ORM
- `androidx.datastore:datastore-preferences-core` (1.2.0) - Data storage
- `androidx.paging:paging-compose` (3.3.6) - Pagination support
- `androidx.core:core-splashscreen` (1.2.0) - Splash screen support
- `androidx.hilt:hilt-navigation-compose` (1.3.0) - Hilt + Compose integration

**Kotlin & Coroutines**
- `org.jetbrains.kotlin:kotlin-stdlib` (2.2.21) - Kotlin standard library
- `org.jetbrains.kotlinx:kotlinx-coroutines-*` (1.10.2) - Coroutines for async operations
- `org.jetbrains.kotlinx:kotlinx-serialization-json` (1.9.0) - JSON serialization
- `org.jetbrains.kotlinx:kotlinx-datetime` (0.7.1) - Date/time handling

**Networking & API**
- `com.squareup.retrofit2:retrofit` (3.0.0) - REST client
- `com.squareup.okhttp3:okhttp` (5.3.2) - HTTP client
- `com.squareup.okhttp3:logging-interceptor` (5.3.2) - HTTP logging
- `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter` (1.0.0) - Serialization converter

**Image Loading**
- `io.coil-kt:coil-compose` (2.7.0) - Image loading library for Compose

**Dependency Injection**
- `com.google.dagger:hilt-android` (2.57.2) - Hilt dependency injection framework
- `com.google.dagger:hilt-compiler` (2.57.2) - Hilt compiler
- `javax.inject:javax.inject` (1) - Injection annotations

**Firebase**
- `com.google.firebase:firebase-bom` (34.7.0) - Firebase platform
- `com.google.firebase:firebase-analytics` - Analytics tracking
- `com.google.firebase:firebase-crashlytics` - Crash reporting
- `com.google.firebase:firebase-messaging` - Cloud messaging
- `com.google.firebase:firebase-perf` - Performance monitoring

**Other Libraries**
- `io.arrow-kt:arrow-core` (2.2.0) - Functional programming utilities
- `com.google.android.gms:play-services-location` (21.3.0) - Location services

### Testing Dependencies

**Unit Testing**
- `junit:junit` (4.13.2) - JUnit test framework
- `io.mockk:mockk` (1.14.7) - Mocking library
- `app.cash.turbine:turbine` (1.2.1) - Flow testing utilities
- `org.jetbrains.kotlinx:kotlinx-coroutines-test` (1.10.2) - Coroutines testing

**Instrumented Testing**
- `androidx.compose.ui:ui-test-junit4` - Compose testing
- `androidx.compose.ui:ui-test-manifest` - Compose test manifest
- `androidx.test:runner` (1.7.0) - Android test runner
- `androidx.test.espresso:espresso-core` (3.7.0) - Espresso testing
- `androidx.test.espresso:espresso-intents` (3.7.0) - Intent testing
- `com.squareup.okhttp3:mockwebserver` (5.3.2) - Mock HTTP server
- `com.google.dagger:hilt-android-testing` (2.57.2) - Hilt testing support
- `androidx.navigation:navigation-testing` (2.9.6) - Navigation testing

### Build Plugins

- `com.android.application` (8.13.2) - Android app plugin
- `org.jetbrains.kotlin.android` (2.2.21) - Kotlin Android plugin
- `org.jetbrains.kotlin.jvm` (2.2.21) - Kotlin JVM plugin
- `com.google.gms.google-services` (4.4.4) - Google Services plugin
- Custom convention plugins via `build-logic/convention`

For the complete, authoritative list of versions and dependencies, see **`gradle/libs.versions.toml`**.
## 🔨 Build & Testing

### Build Commands

Build the app in debug mode:
```bash
./gradlew assembleDebug
```

Build the app in release mode:
```bash
./gradlew assembleRelease
```

Run all unit tests:
```bash
./gradlew test
```

Run instrumented UI tests:
```bash
./gradlew connectedUiTests
```

Aggregate UI test reports:
```bash
./gradlew aggregateUiAndroidTestReports
```

### Code Quality

The project uses:
- **Kotlin Compiler**: Enforces modern Kotlin syntax and patterns
- **Convention Plugins**: Enforce consistent dependency and configuration across modules
- **IDE Inspections**: Android Studio integrated code analysis

### Directory Structure for Build Logic

Custom Gradle convention plugins are located in `build-logic/convention/`:
- `tmdb.ui.module.plugin` - Applies UI module configuration (Compose, Material3, testing)
- `tmdb.framework.module.plugin` - Applies framework module configuration (networking, persistence, DI)
- `tmdb.kotlin.module.plugin` - Applies common Kotlin JVM settings

## 🤝 Contribution

- Follow the project's coding conventions and architecture
- Create features following the modular pattern (domain/framework/ui split)
- Add unit and instrumented tests for new features
- Keep dependencies versions synchronized in `gradle/libs.versions.toml`
- Open issues or pull requests on GitHub

## 📄 License

[Add your license information here]

## 👥 Credits

- **Architecture Pattern**: Clean Architecture + MVVM with Hilt DI
- **UI Framework**: Jetpack Compose + Material Design 3
- **Build System**: Gradle with custom convention plugins
- See repository for full acknowledgements

---

**Last Updated**: December 17, 2025
