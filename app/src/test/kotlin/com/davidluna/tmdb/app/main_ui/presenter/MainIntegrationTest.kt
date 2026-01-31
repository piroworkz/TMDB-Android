package com.davidluna.tmdb.app.main_ui.presenter

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.davidluna.tmdb.auth_data.data.fakeAccount
import com.davidluna.tmdb.auth_data.data.fakeGuestSession
import com.davidluna.tmdb.auth_data.data.fakeRoomAccount
import com.davidluna.tmdb.auth_data.data.fakeRoomGuestSession
import com.davidluna.tmdb.auth_data.data.fakeRoomSession
import com.davidluna.tmdb.auth_data.data.fakeSession
import com.davidluna.tmdb.auth_data.data.local.database.dao.AccountDaoSpy
import com.davidluna.tmdb.auth_data.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_data.data.remote.AuthenticationApiSpy
import com.davidluna.tmdb.auth_data.data.remote.UserAccountApiSpy
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.auth_data.repositories.AccountRepository
import com.davidluna.tmdb.auth_data.repositories.AuthenticationRepository
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.media_data.data.local.database.dao.FavoritesDaoSpy
import com.davidluna.tmdb.media_data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_data.repositories.FavoritesRepository
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainIntegrationTest {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var accountDao: AccountDaoSpy
    private lateinit var authAPI: AuthenticationApiSpy
    private lateinit var favoritesDao: FavoritesDaoSpy
    private lateinit var selectedCatalogDataSource: UpdateSelectedEndpoint
    private lateinit var sessionDao: SessionDaoSpy
    private lateinit var userAccountApi: UserAccountApiSpy

    @Test
    fun `GIVEN a valid guest session WHEN state flow has at least one subscriber THEN _state should be updated with guest session`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            sessionDao.insertSession(fakeRoomGuestSession)

            sut.state.test {
                awaitItem()
                val actual = awaitItem().session

                assertEquals(fakeGuestSession, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN a valid session AND user account WHEN state flow has at least one subscriber THEN _state should be updated with guest session`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            sessionDao.insertSession(fakeRoomSession)
            accountDao.insertAccount(fakeRoomAccount)

            sut.state.test {
                awaitItem()
                val actual = awaitItem()

                assertEquals(fakeSession, actual.session)
                assertEquals(fakeAccount, actual.userAccount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildSUT(): MainViewModel {
        userAccountApi = UserAccountApiSpy()
        accountDao = AccountDaoSpy()
        authAPI = AuthenticationApiSpy()
        sessionDao = SessionDaoSpy()
        favoritesDao = FavoritesDaoSpy()
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

        val favoritesRepository = FavoritesRepository(
            favoritesDao = favoritesDao
        )

        return MainViewModel(
            clearFavorites = favoritesRepository,
            closeSession = authenticationRepository,
            ioDispatcher = coroutineTestRule.dispatcher,
            observeSession = authenticationRepository,
            observeUserAccount = accountRepository,
            updateSelectedEndpoint = selectedCatalogDataSource,
            validateSession = authenticationRepository
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