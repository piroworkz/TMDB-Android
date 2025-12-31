package com.davidluna.tmdb.app.main_ui.presenter

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.davidluna.tmdb.app.main_ui.fakes.fakeUserAccount
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCloseSession
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ResetAppError
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDaoSpy
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.auth_framework.data.sources.SessionCloser
import com.davidluna.tmdb.auth_framework.data.sources.AccountStore
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType.TV_SHOW
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_framework.data.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MainIntegrationTest {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var accountDao: AccountDao
    private lateinit var sessionDao: SessionDao
    private lateinit var selectedCatalogDataSource: UpdateSelectedEndpoint

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

            (accountDao as AccountDaoSpy).shouldThrowException(expected)

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

            sut.onEvent(OnCloseSession)
            sut.state.test {
                val initialValue = awaitItem().isSessionClosed
                val actual = awaitItem().isSessionClosed

                assertFalse(initialValue)
                assertTrue(actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN accountDao fails THEN state updates appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("test exception")
            val sut = buildSUT()

            (accountDao as AccountDaoSpy).shouldThrowException(expected)
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
    fun `GIVEN OnCloseSession event WHEN sessionDao fails THEN state updates appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("test exception")
            val sut = buildSUT()

            (sessionDao as SessionDaoSpy).shouldThrowException(expected)
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

            (accountDao as AccountDaoSpy).shouldThrowException(expected)

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
        accountDao = AccountDaoSpy()
        sessionDao = SessionDaoSpy()
        selectedCatalogDataSource = buildSelectedCatalogDataSource()

        return MainViewModel(
            observeUserAccount = AccountStore(accountDao),
            closeSession = SessionCloser(accountDao, sessionDao),
            ioDispatcher = coroutineTestRule.dispatcher,
            updateMediaCatalogUseCase = selectedCatalogDataSource
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
}