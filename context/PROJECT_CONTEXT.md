# TMDB-Android — PROJECT_CONTEXT (Ops Cheat Sheet)

Operational-only context for automated changes (Codex) and developers.
This file is intentionally small to avoid redundant context.

Authoritative rules live in:
- `AGENTS.md` (repo guardrails: architecture/DI/dependencies/git/github)
- `constitution.md` (process/TDD/testing discipline)

---

## Local setup (minimum required)

### local.properties (required)
Create `local.properties` at repo root (never commit it).

Minimum required key:
- `TMDB_API_KEY=<your_key>`

Optional but recommended (testFixtures Kotlin sourceset support):
- `android.experimental.enableTestFixturesKotlinSupport=true`

If release signing is needed, use local.properties only (never commit).

---

## Canonical Gradle commands

### Default verification (fast)
- Run all unit tests:
    - `./gradlew test`

### Build / install debug
- Install debug build:
    - `./gradlew installDebug`

### If changing build-logic (convention plugins)
- Validate build-logic:
    - `./gradlew :build-logic:convention:build`

### Instrumented tests (heavy)
- Instrumented tests must not be run by default.
- Only run if explicitly required by a task/spec.

Examples:
- `./gradlew :app:connectedDebugAndroidTest`
- `./gradlew :feature:auth:auth_ui:connectedDebugAndroidTest`

---

## Secrets & Native config
Some configuration values may be provided via JNI/native wrappers.
Rules:
- never hardcode or invent secrets
- never commit secrets
- if native setup is missing and build fails, STOP and ask for setup details

---

## Common failures & fixes (top)

### Koin DI errors
Symptoms:
- `NoDefinitionFoundException`
- unreachable definitions
  Fix:
- ensure the module is loaded in the Koin app
- confirm the expected wrapper type is provided (ApiKey/BaseUrl/etc.)
- avoid multiple competing bindings for the same type

### Native lib / JNI
Symptoms:
- `UnsatisfiedLinkError`
  Fix:
- verify `System.loadLibrary("native")` has the correct library name
- ensure native binaries exist in the expected build config

### Kotlin testFixtures compilation
Symptom:
- testFixtures Kotlin code fails to compile
  Fix:
- set in local.properties or gradle.properties:
    - `android.experimental.enableTestFixturesKotlinSupport=true`

---

## GitHub operations
All GitHub operations MUST be done using `gh` (issues, PRs, comments, labels).
(See `AGENTS.md` for details.)
