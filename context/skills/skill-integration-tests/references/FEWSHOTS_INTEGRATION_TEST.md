## Scope
Few-shot examples for integration tests using spies in this repo. These are examples only; follow `AGENTS.md` and `context/constitution.md` if any conflict.

Source files:
- `feature/auth/auth_ui/src/test/kotlin/com/davidluna/tmdb/auth_ui/presenter/login/LoginIntegrationTest.kt`
- `feature/media/media_ui/src/test/kotlin/com/davidluna/tmdb/media_ui/presenter/media/MediaCatalogIntegrationTest.kt`

---

## Shot 1 — LoginIntegrationTest (SUT uses spies)
```kotlin
private fun buildSut(): LoginViewModel {
    authAPI = AuthenticationApiSpy()
    sessionDao = SessionDaoSpy()
    userAccountApi = UserAccountApiSpy()
    accountDao = AccountDaoSpy()
    val accountRepository = AccountRepository(
        userAccountApi = userAccountApi,
        accountDao = accountDao
    )
    val authRepository = AuthenticationRepository(
        authAPI = authAPI,
        sessionDao = sessionDao,
        accountDetailsRepository = accountRepository
    )

    openSession = authRepository
    validateInput = TextInputValidator()

    return LoginViewModel(
        ioDispatcher = coroutineTestRule.dispatcher,
        openSession = openSession,
        validateInput = validateInput
    )
}
```

---

## Shot 2 — LoginIntegrationTest (successful login)
```kotlin
@Test
fun `GIVEN valid credentials WHEN LoginButtonClicked event AND open succeeds THEN delivers isLoggedIn state update`(): Unit =
    coroutineTestRule.scope.runTest {
        val sut = buildSut()
        val loginMethod = LoginMethod.AuthCredentials("username@mail.com", "password")

        sut.onEvent(LoginEvent.LoginButtonClicked(loginMethod.username, loginMethod.password))

        sut.state.test {
            awaitItem()
            val isLoggedIn = awaitItem().isLoggedIn

            assertTrue(isLoggedIn)
            cancelAndIgnoreRemainingEvents()
        }
    }
```

---

## Shot 3 — LoginIntegrationTest (error from spy)
```kotlin
@Test
fun `GIVEN LoginMethod AsGuest WHEN GuestButtonClicked event AND open fails THEN delivers appError state update`(): Unit =
    coroutineTestRule.scope.runTest {
        val sut = buildSut()
        val expected = AppError(
            code = SERVER,
            description = "The resource you requested could not be found.",
            type = null
        )

        sut.onEvent(LoginEvent.GuestButtonClicked)
        authAPI.throwError(true)

        sut.state.test {
            awaitItem()
            val actualState = awaitItem().appError
            assertEquals(expected, actualState)
        }
    }
```

---

## Shot 4 — MediaCatalogIntegrationTest (SUT uses spies)
```kotlin
private fun buildSUT(): MediaCatalogViewModel {
    mediaDao = MediaDaoSpy()
    mediaService = RemoteMediaServiceSpy()
    remoteKeysDao = RemoteKeysDaoSpy()
    isCacheExpired = CachePolicyValidator()

    dataStore = newDataStore(temporaryFolderRule.newFolder())
    val getSelectedMediaCatalog = SelectedCatalogDataSource(dataStore)
    val mediatorFactory: MediaCatalogMediatorFactory = MediaCatalogMediatorFactorySpy(
        mediaDao = mediaDao,
        mediaService = mediaService,
        remoteKeysDao = remoteKeysDao,
        isCacheExpired = isCacheExpired
    )
    val observeMediaCatalogUseCase = MediaCatalogRepository(
        mediaDao = mediaDao,
        mediatorFactory = mediatorFactory
    )
    return MediaCatalogViewModel(
        observeSelectedMediaCatalogUseCase = getSelectedMediaCatalog,
        observeMediaCatalogUseCase = observeMediaCatalogUseCase
    )
}
```

---

## Shot 5 — MediaCatalogIntegrationTest (paging snapshot)
```kotlin
@Test
fun `GIVEN pagerPagingDataFlow WHEN subscriber is added and refresh is called THEN emits paging data`() =
    coroutineTestRule.scope.runTest {
        val expected = fakeMediaList
        val sut = buildSUT()

        val actual: @JvmSuppressWildcards List<Media> =
            sut.pagerPagingDataFlow.asSnapshot { refresh() }

        assertEquals(expected, actual)
    }
```

---

## Shot 6 — MediaCatalogIntegrationTest (state update)
```kotlin
@Test
fun `GIVEN updateLastKnownPosition WHEN called THEN should set lastKnownPosition in state`() =
    coroutineTestRule.scope.runTest {
        val sut = buildSUT()
        val expected = 1 to 2
        sut.updateLastKnownPosition(expected.first, expected.second)

        sut.state.test {
            skipItems(1)
            val actual = awaitItem().lastKnownPosition

            assertEquals(expected, actual)
        }
    }
```
