package com.davidluna.tmdb.app.main_ui.presenter

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.davidluna.tmdb.app.main_ui.fakes.fakeUserAccount
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCloseSession
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ResetAppError
import com.davidluna.tmdb.auth_data.data.fakeRoomSession
import com.davidluna.tmdb.auth_data.data.local.database.dao.AccountDaoSpy
import com.davidluna.tmdb.auth_data.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_data.data.remote.AuthenticationApiSpy
import com.davidluna.tmdb.auth_data.data.remote.UserAccountApiSpy
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.auth_data.repositories.AccountRepository
import com.davidluna.tmdb.auth_data.repositories.AuthenticationRepository
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType.TV_SHOW
import com.davidluna.tmdb.media_domain.usecases.ClearFavorites
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class MainIntegrationTest {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var userAccountApi: UserAccountApiSpy
    private lateinit var accountDao: AccountDaoSpy
    private lateinit var authAPI: AuthenticationApiSpy
    private lateinit var sessionDao: SessionDaoSpy

    private lateinit var selectedCatalogDataSource: UpdateSelectedEndpoint
    private lateinit var clearFavorites: FakeClearFavorites

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN GetUserAccountUseCase returns user WHEN VM is initialized THEN userAccount emits`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            accountDao.insertAccount(fakeUserAccount.toEntity())

            sut.userAccount.test {
                val initialValue = awaitItem()
                val actual = awaitItem()

                assertEquals(initialValue, null)
                assertEquals(actual, fakeUserAccount)
                cancel()
            }
        }

    @Test
    fun `GIVEN GetUserAccountUseCase throws WHEN VM is initialized THEN state contains appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("test exception")
            val sut = buildSUT()

            accountDao.shouldThrowException(expected)

            val userAccountJob = launch { sut.userAccount.collect {} }

            sut.state.test {
                val initialValue = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialValue)
                assertEquals(expected.toAppError(), actual)
                cancel()
            }
            userAccountJob.cancel()
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN accountDao AND sessionDao succeed THEN isSessionClosed state should be true`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            sessionDao.insertSession(fakeRoomSession)
            sut.onEvent(OnCloseSession)

            sut.state.test {
                val initialValue = awaitItem().isSessionClosed
                val actual = awaitItem().isSessionClosed

                assertFalse(initialValue)
                assertTrue(actual)
                cancel()
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN OnCloseSession event WHEN session is cleared THEN favorites are cleared`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            sessionDao.insertSession(fakeRoomSession)
            sut.onEvent(OnCloseSession)
            advanceUntilIdle()

            assertEquals(1, clearFavorites.calls)
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN accountDao fails THEN state updates appError`() =
        coroutineTestRule.scope.runTest {
            val expected = AppError(
                code = AppErrorCode.NOT_FOUND,
                description = "Account not deleted or no session found",
                type = null
            )
            val sut = buildSUT()

            accountDao.shouldThrowException(expected)
            sut.onEvent(OnCloseSession)
            sut.state.test {
                val initialValue = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialValue)
                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN sessionDao fails THEN state updates appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("test exception")
            val sut = buildSUT()

            sessionDao.shouldThrowException(expected)
            sut.onEvent(OnCloseSession)
            sut.state.test {
                val initialValue = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialValue)
                assertEquals(expected.toAppError(), actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN UpdateBottomNavItems event WHEN bottomNavItems different from current THEN state updated`() =
        coroutineTestRule.scope.runTest {
            val initialBottomNavItems = MainViewModel.State().bottomNavItems
            val expected = TV_SHOW.bottomBarItems()
            val sut = buildSUT()

            sut.onEvent(MainEvent.UpdateBottomNavItems(expected))
            sut.state.test {
                val initialValue = awaitItem().bottomNavItems
                val actual = awaitItem().bottomNavItems

                assertEquals(initialBottomNavItems, initialValue)
                assertEquals(expected, actual)
                cancel()
            }

        }

    @Test
    fun `GIVEN UpdateBottomNavItems event WHEN bottomNavItems same as current THEN no new state is produced`() =
        coroutineTestRule.scope.runTest {
            val initialBottomNavItems = MainViewModel.State().bottomNavItems
            val sut = buildSUT()

            sut.onEvent(MainEvent.UpdateBottomNavItems(initialBottomNavItems))

            sut.state.test {
                val initialValue = awaitItem().bottomNavItems

                assertEquals(initialBottomNavItems, initialValue)
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN OnCatalogSelected event WHEN selectedCatalogDataSource succeeds THEN state updated`() =
        coroutineTestRule.scope.runTest {
            val expected = Catalog.MOVIE_POPULAR
            val sut = buildSUT()

            sut.onEvent(MainEvent.OnCatalogSelected(expected))
            sut.state.test {
                skipItems(1)
                val actual = awaitItem().selectedCatalog

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN ResetAppError event WHEN appError is not null THEN state appError is set to null`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("test exception")
            val sut = buildSUT()

            accountDao.shouldThrowException(expected)

            val userAccountJob = launch { sut.userAccount.collect {} }

            sut.state.test {
                skipItems(2)
                sut.onEvent(ResetAppError)
                val actual = awaitItem().appError

                assertNull(actual)
                cancel()
            }
            userAccountJob.cancel()
        }

    private fun buildSUT(): MainViewModel {
        userAccountApi = UserAccountApiSpy()
        accountDao = AccountDaoSpy()
        authAPI = AuthenticationApiSpy()
        sessionDao = SessionDaoSpy()
        clearFavorites = FakeClearFavorites()

        selectedCatalogDataSource = buildSelectedCatalogDataSource()

        val accountRepository = AccountRepository(
            userAccountApi = userAccountApi,
            accountDao = accountDao
        )
        val authenticationRepository = AuthenticationRepository(
            authAPI = authAPI,
            sessionDao = sessionDao,
            accountDetailsRepository = accountRepository
        )

        return MainViewModel(
            observeUserAccount = accountRepository,
            closeSession = authenticationRepository,
            ioDispatcher = coroutineTestRule.dispatcher,
            observeSession = authenticationRepository,
            updateSelectedEndpoint = selectedCatalogDataSource,
            clearFavorites = clearFavorites
        )
    }

    private fun buildSelectedCatalogDataSource(): UpdateSelectedEndpoint {
        val dataStore = newDataStore(temporaryFolderRule.newFolder())
        return SelectedCatalogDataSource(dataStore)
    }

    private fun newDataStore(tmp: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = coroutineTestRule.scope,
        ) { File(tmp, "test.preferences_pb") }

    private fun UserAccount.toEntity(): RoomUserAccount {
        return RoomUserAccount(
            userId = userId,
            name = name,
            username = username,
            avatarPath = avatarPath
        )
    }

    private class FakeClearFavorites : ClearFavorites {
        var calls = 0
            private set

        override suspend fun clear(): AppError? {
            calls += 1
            return null
        }
    }
}
