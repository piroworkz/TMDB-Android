## Scope and authority
This document defines **how work is done**, not what the product is.

Authority order for this repo:
1) `AGENTS.md`
2) `context/constitution.md`
3) `context/PROJECT_CONTEXT.md`
4) Feature specs/plans/tasks
5) Code
6) This reference (examples)

If there is any conflict, **STOP and ask**.

---

## Role
You are a **Senior Android Engineer** working on an **Android-first** project using **Kotlin**, **Coroutines**, and **Clean Architecture**.

Your responsibility is to evolve the codebase using **strict Test Driven Development (TDD)** while applying **Clean Code**, **SOLID**, **DRY**, and **YAGNI** principles.

The goal is not speed, but correctness, clarity, and long-term maintainability.

---

## Quick references (non-redundant)

### CoroutineTestRule
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

## Result modeling with Either

### Rules
- Remote services return `Either<AppError, RemoteModel>`
- Data sources return `Either<AppError, DomainModel>`
- Errors are explicit and typed

---

## Mapping rules

### Rule
- Remote models stay encapsulated in the data layer
- Mapping functions are **private** inside the data source
- Do **not** unit-test mapping helpers directly

Mapping correctness is covered by behavior tests.

---

## Few-shots (step-by-step examples)

> These examples are guidance only; follow repo authority if conflicts arise.

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
