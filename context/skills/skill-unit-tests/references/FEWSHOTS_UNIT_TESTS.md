## Scope
Few-shot examples for unit tests using MockK in this repo. These are examples only; follow `AGENTS.md` and `context/constitution.md` if any conflict.

Source files:
- `feature/auth/auth_data/src/test/kotlin/com/davidluna/tmdb/auth_data/repositories/CloseSessionTests.kt`
- `feature/auth/auth_data/src/test/kotlin/com/davidluna/tmdb/auth_data/repositories/OpenSessionTests.kt`
- `feature/auth/auth_data/src/test/kotlin/com/davidluna/tmdb/auth_data/repositories/ObserveSessionTest.kt`

---

## Shot 1 — CloseSessionTests (no side effects)
```kotlin
@Test
fun `GIVEN sut WHEN is created THEN delivers no side effects`() {
    buildSUT()

    verify { authenticationApi wasNot called }
    verify { sessionDao wasNot called }
    verify { accountRepository wasNot called }
}
```

---

## Shot 2 — CloseSessionTests (error path)
```kotlin
@Test
fun `GIVEN session exists in db WHEN deleteSession throws exception THEN close delivers error on left`(): Unit =
    coroutineRule.scope.runTest {
        val sut = buildSUT()
        val expected = SQLiteException().toAppError()

        coEvery { sessionDao.hasSession() } returns true
        coEvery { sessionDao.deleteSession() } throws SQLiteException()

        val actual = sut.close()
        assertEquals(expected, actual)
    }
```

---

## Shot 3 — OpenSessionTests (error path)
```kotlin
@Test
fun `GIVEN authCredentials method WHEN createRequestToken fails THEN open delivers error`() =
    coroutineRule.scope.runTest {
        val sut = buildSUT()
        val loginMethod = fakeAuthCredentials
        val expected = fakeRemoteError.toAppError()

        coEvery { authenticationApi.createRequestToken() } returns fakeRemoteError.left()

        val actual = sut.open(loginMethod)
        assertEquals(expected, actual)
    }
```

---

## Shot 4 — OpenSessionTests (success path)
```kotlin
@Test
fun `GIVEN authCredentials method WHEN login succeeds THEN open delivers null`() =
    coroutineRule.scope.runTest {
        val sut = buildSUT()
        val loginMethod = fakeAuthCredentials

        coEvery { authenticationApi.createRequestToken() } returns fakeRemoteTokenResponse.right()
        coEvery { authenticationApi.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
        coEvery { authenticationApi.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
        coEvery { sessionDao.insertSession(any()) } returns 1
        coEvery { accountRepository.fetch() } returns null

        val actual = sut.open(loginMethod)
        assertNull(actual)
    }
```

---

## Shot 5 — ObserveSessionTest (Flow emits value)
```kotlin
@Test
fun `GIVEN session in db WHEN getSession is called THEN session emits session`() =
    coroutineRule.scope.runTest {
        val sut = buildSUT()
        val expected = fakeDomainSession

        every { sessionDao.getSession() } returns flowOf(fakeRoomSession)

        sut.session.test {
            val actual = awaitItem()
            assertEquals(expected, actual)
            cancelAndIgnoreRemainingEvents()
        }
    }
```

---

## Shot 6 — ObserveSessionTest (Flow emits error)
```kotlin
@Test
fun `GIVEN session in db WHEN getSession throws exception THEN session emits error`(): Unit =
    coroutineRule.scope.runTest {
        val sut = buildSUT()
        val expected = SQLiteException()

        every { sessionDao.getSession() } returns flow { throw expected }

        sut.session.test {
            val actual = awaitError()
            assertEquals(expected, actual)
            cancel()
        }
    }
```
