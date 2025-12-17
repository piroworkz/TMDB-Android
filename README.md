
# TMDB2024

A Modern, Scalable Android Application

TMDB 2024 is a cutting-edge Android application built with Kotlin and Jetpack Compose, adhering to Clean Architecture and SOLID principles. This project showcases a robust approach to building maintainable, scalable, and testable Android applications using best-in-class practices.


Why Kotlin and Clean Architecture?


## Kotlin

A modern, concise language that compiles to Java bytecode. It offers features like null safety, higher-order functions, coroutines, and extension functions, leading to cleaner and more expressive code.
## Clean Architecture:

An architectural approach that separates concerns into distinct layers, promoting independence, testability, and maintainability. SOLID principles guide this separation of responsibilities.
        
### S.O.L.I.D.

- **Single Responsibility Principle:** A class should have one, and only one, reason to change.
- **Open-Closed Principle:** Entities should be open for extension, but closed for modification.
- **Liskov Substitution Principle:** Objects in a superclass should be replaceable with objects of its - subclasses without altering the correctness of the program.   
- **Interface Segregation Principle:** Clients should not be forced to depend on interfaces that they - do not use.
- **Dependency Inversion Principle:** Depend on abstractions, not concretions.


## Key Features

-    Modern UI with Jetpack Compose: Delivers a sleek and responsive user interface using Jetpack -Compose.
-    Efficient Data Serialization with Protobuf: Leverages Google Protocol Buffers for fast and -efficient data serialization and deserialization.
-    Secure Network Communication: Ensures secure data transfer with Retrofit and OkHttp.
-    Advanced State Management and Asynchronous Handling: Employs Kotlin Coroutines for seamless -asynchronous operations and data flow.
-    Clean Architecture Adherence: Strictly follows Clean Architecture principles for better code -organization and testability.
-    Comprehensive Testing: Includes a robust suite of unit and integration tests to guarantee code quality. (Instrumented testing coming soon)


## Benefits of This Approach

Cleaner, more maintainable code: Facilitates long-term code understanding and modification.
Enhanced testability: Enables writing more isolated and reliable unit tests.
Reduced coupling: Minimizes dependencies between different code parts.
Improved scalability: Allows for adding new features without significantly impacting existing cod.
Greater code reusability: Promotes the creation of reusable components.
## Getting Started

- **Add your TMDB API key to `local.properties`** (project root) as:

```
TMDB_API_KEY=your_api_key_here
```

Do not commit `local.properties` to version control.

### Prerequisites

- Android Studio Otter | 2025.2.1 or later
- JDK 1.8 (or the JDK configured for your environment/Gradle plugin)

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ArchitectCoders2024.git
   cd ArchitectCoders2024
   ```

2. Add your TMDB API key to `local.properties` as shown above.

3. Open the project in Android Studio and let Gradle sync. The project uses a centralized versions catalog at `gradle/libs.versions.toml` to manage dependency versions.

### Configuration — Signing keystore & Base URL

For release builds you'll usually need a signing keystore and a base API URL. Keep these values out of source control by placing them in `local.properties` (project root). Example entries:

```
# TMDB API key
MY_API_KEY=your_api_key_here

# Base URL for API requests (defaults to TMDB v3)
BASE_URL=https://api.themoviedb.org/

# Signing / release keystore (absolute or relative path)
STORE_FILE=/absolute/or/relative/path/to/keystore.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### AndroidX
- `androidx.navigation:navigation-compose` - Navigation for Jetpack Compose.
- `androidx.fragment:fragment-ktx` - Kotlin extensions for Fragment APIs.
- `androidx.biometric:biometric` - Biometric authentication support.

### Kotlin
- `org.jetbrains.kotlin:kotlin-stdlib` - Kotlin standard library.
- `org.jetbrains.kotlinx:kotlinx-coroutines-core` - Concurrency using coroutines.
- `org.jetbrains.kotlinx:kotlinx-serialization-json` - Kotlinx Serialization for JSON.

### Jetpack Compose
- Compose BOM (managed in the versions catalog)
- `androidx.activity:activity-compose` - Compose integration with Activities.
- `androidx.compose.ui:ui` - Core Compose UI.
- `androidx.compose.material3:material3` - Material Design 3 components.
- `androidx.compose.animation:animation` - Compose animation APIs.
- `io.coil-kt:coil-compose` - Image loading in Compose.

### Networking
- `com.squareup.retrofit2:retrofit` - REST client.
- `com.squareup.okhttp3:okhttp` - HTTP client.
- `com.squareup.okhttp3:logging-interceptor` - Logging interceptor for OkHttp.
- `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter` - Kotlinx-serialization converter for Retrofit.

### Protobuf
- `com.google.protobuf:protobuf-javalite` and `protobuf-kotlin-lite` - Protobuf (lite) implementations used for efficient serialization.

### Arrow
- `io.arrow-kt:arrow-core` - Functional programming utilities for Kotlin.

### Paging
- `androidx.paging:paging-compose` and `androidx.paging:paging-runtime` - Paging library with Compose integration.

### Hilt / DI
- `com.google.dagger:hilt-android` - Dependency Injection framework.
- `androidx.hilt:hilt-navigation-compose` - Hilt integration for Compose navigation.

### Google Services & Material
- `com.google.android.gms:play-services-location` - Location APIs.
- `com.google.android.material:material` - Material components.

### Piroworkz (project utilities)
- `com.piroworkz:compose-android-permissions` - Permission utilities for Compose.
- `com.piroworkz:versions-catalog` - Version-catalog helper used by the build logic.

### Testing
- Unit testing: `junit:junit`, `io.mockk:mockk`, `app.cash.turbine:turbine`, `com.google.truth:truth`.
- Instrumentation/Compose testing: `androidx.compose.ui:ui-test-junit4`, `com.squareup.okhttp3:mockwebserver`, `androidx.test.espresso:espresso-intents`.

For a complete, authoritative list of libraries and their exact versions, see `gradle/libs.versions.toml`.


## Contribution

- Follow the project's coding conventions and architecture.
- Add tests for new features.
- Open issues or PRs on GitHub.

## Credits

- Project scaffold and utilities by Piroworkz
- See repository for full acknowledgements and license.
