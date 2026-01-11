package com.davidluna.tmdb.auth_ui.presenter.splash

import com.davidluna.tmdb.auth_data.data.local.database.dao.AccountDaoSpy
import com.davidluna.tmdb.auth_data.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_data.data.remote.AuthenticationApiSpy
import com.davidluna.tmdb.auth_data.data.remote.UserAccountApiSpy
import com.davidluna.tmdb.auth_data.repositories.AccountDetailsRepository
import com.davidluna.tmdb.auth_data.repositories.AccountRepository
import com.davidluna.tmdb.auth_data.repositories.AuthenticationRepository
import com.davidluna.tmdb.auth_ui.fakes.buildDate
import com.davidluna.tmdb.auth_ui.fakes.fakeGuestRoomSession
import com.davidluna.tmdb.auth_ui.fakes.fakeRoomSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashIntegrationTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var sessionDao: SessionDaoSpy
    private lateinit var authenticationApi: AuthenticationApiSpy
    private lateinit var userAccountApi: UserAccountApiSpy
    private lateinit var accountDao: AccountDaoSpy

    @Test
    fun `GIVEN session exists WHEN checkSessionStatus THEN isLoggedIn delivers true`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSut()
            sessionDao.insertSession(fakeRoomSession)

            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == true)
        }

    @Test
    fun `GIVEN guest session exists WHEN checkSessionStatus THEN isLoggedIn delivers true`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSut()
            sessionDao.insertSession(fakeGuestRoomSession.copy(expiresAt = buildDate()))

            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == true)
        }

    @Test
    fun `GIVEN expired guest session exists WHEN checkSessionStatus THEN isLoggedIn delivers true`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSut()
            sessionDao.insertSession(fakeGuestRoomSession.copy(expiresAt = buildDate(addDays = -1)))

            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == false)
        }

    @Test
    fun `GIVEN no session exists WHEN checkSessionStatus THEN isLoggedIn delivers false`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSut()

            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == false)
        }

    private fun buildSut(): SplashViewModel {
        sessionDao = SessionDaoSpy()
        authenticationApi = AuthenticationApiSpy()
        userAccountApi = UserAccountApiSpy()
        accountDao = AccountDaoSpy()
        val accountRepository: AccountDetailsRepository = AccountRepository(
            userAccountApi = userAccountApi,
            accountDao = accountDao
        )
        val authenticationRepository = AuthenticationRepository(
            sessionDao = sessionDao,
            authAPI = authenticationApi,
            accountDetailsRepository = accountRepository
        )

        return SplashViewModel(
            ioDispatcher = coroutineTestRule.dispatcher,
            validateSession = authenticationRepository,
            observeSession = authenticationRepository,
            closeSession = authenticationRepository
        )
    }

}