package com.davidluna.tmdb.auth_ui.presenter.login

import app.cash.turbine.test
import com.davidluna.tmdb.auth_domain.entities.TextInputError
import com.davidluna.tmdb.auth_framework.data.local.TextInputValidator
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDaoSpy
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_framework.data.remote.RemoteAuthenticationService
import com.davidluna.tmdb.auth_framework.data.remote.RemoteAuthenticationServiceSpy
import com.davidluna.tmdb.auth_framework.data.remote.UserAccountService
import com.davidluna.tmdb.auth_framework.data.remote.UserAccountServiceSpy
import com.davidluna.tmdb.auth_framework.data.sources.SessionCloser
import com.davidluna.tmdb.auth_framework.data.sources.GuestSessionRepository
import com.davidluna.tmdb.auth_framework.data.sources.AuthenticationRepository
import com.davidluna.tmdb.auth_framework.data.sources.UserAccountFetcher
import com.davidluna.tmdb.auth_ui.fakes.fakePassword
import com.davidluna.tmdb.auth_ui.fakes.fakeRemoteError
import com.davidluna.tmdb.auth_ui.fakes.fakeUsername
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.SetPassword
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.SetUsername
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test


class LoginIntegrationTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var accountDao: AccountDao
    private lateinit var remoteAuthenticationService: RemoteAuthenticationService
    private lateinit var sessionDao: SessionDao
    private lateinit var userAccountService: UserAccountService
    private val initialState = LoginViewModel.State()

    @Test
    fun `GIVEN GuestButtonClicked event WHEN LoginAsGuest is successful THEN user is logged in and account is not saved`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            sut.onEvent(LoginEvent.GuestButtonClicked)
            sut.state.onEach { println("<-- $it") }.test {
                val initialLoginState = awaitItem()
                val actual = awaitItem().isLoggedIn

                assertEquals(this@LoginIntegrationTest.initialState, initialLoginState)
                assertTrue(actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN GuestButtonClicked event WHEN LoginAsGuest fails remotely THEN appError is set and user is not logged in`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeRemoteError.toAppError()
            val sut = buildSUT()

            (remoteAuthenticationService as RemoteAuthenticationServiceSpy).throwError(true)
            sut.onEvent(LoginEvent.GuestButtonClicked)

            sut.state.onEach { println("<-- $it") }.test {
                val initialLoginState = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialLoginState)
                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN valid credentials WHEN LoginButtonClicked THEN user is logged in and session and account are saved`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            sut.onEvent(SetUsername(fakeUsername))
            sut.onEvent(SetPassword(fakePassword))
            sut.onEvent(LoginEvent.LoginButtonClicked(fakeUsername, fakePassword))
            sut.state.onEach { println("<-- $it") }.test {
                val notLoggedIn = awaitItem().isLoggedIn
                val actual = awaitItem().isLoggedIn

                assertFalse(notLoggedIn)
                assertTrue(actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN invalid credentials WHEN LoginButtonClicked THEN input validation errors are set`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val invalidUsername = "invalid"
            val invalidPassword = "...."
            val expectedUsernameError = TextInputError.InvalidEmail.message
            val expectedPasswordError = TextInputError.InvalidLength(8).message

            sut.onEvent(SetUsername(invalidUsername))
            sut.onEvent(SetPassword(invalidPassword))
            sut.onEvent(LoginEvent.LoginButtonClicked(invalidUsername, invalidPassword))
            sut.state.onEach { println("<-- $it") }.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expectedUsernameError, actual.usernameError)
                assertEquals(expectedPasswordError, actual.passwordError)
                cancel()
            }
        }

    @Test
    fun `GIVEN close session fails WHEN LoginButtonClicked with valid credentials THEN appError is set`() =
        coroutineTestRule.scope.runTest {
            val expectedError = IllegalStateException("fake exception")
            val sut = buildSUT()

            (sessionDao as SessionDaoSpy).shouldThrowException(expectedError)
            sut.onEvent(SetUsername(fakeUsername))
            sut.onEvent(SetPassword(fakePassword))
            sut.onEvent(LoginEvent.LoginButtonClicked(fakeUsername, fakePassword))

            sut.state.onEach { println("<-- $it") }.test {
                skipItems(1)
                val actual = awaitItem().appError

                assertNotNull(actual)
                cancel()
            }

        }

    @Test
    fun `GIVEN valid credentials WHEN remote login fails THEN appError is set`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeRemoteError
            val sut = buildSUT()

            (remoteAuthenticationService as RemoteAuthenticationServiceSpy).throwError(true)
            sut.onEvent(SetUsername(fakeUsername))
            sut.onEvent(SetPassword(fakePassword))
            sut.onEvent(LoginEvent.LoginButtonClicked(fakeUsername, fakePassword))

            sut.state.onEach { println("<-- $it") }.test {
                val initialLoginState = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialLoginState)
                assertEquals(expected.toAppError(), actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN state has appError WHEN ResetAppError is triggered THEN appError is cleared`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            (remoteAuthenticationService as RemoteAuthenticationServiceSpy).throwError(true)
            sut.onEvent(LoginEvent.GuestButtonClicked)
            sut.state.onEach { println("<-- $it") }.test {
                awaitItem()
                val initialAppError = awaitItem().appError
                sut.onEvent(LoginEvent.ResetAppError)
                val actual = awaitItem().appError

                assertNotNull(initialAppError)
                assertNull(actual)
                cancel()
            }
        }

    private fun buildSUT(): LoginViewModel {
        accountDao = AccountDaoSpy()
        remoteAuthenticationService = RemoteAuthenticationServiceSpy()
        sessionDao = SessionDaoSpy()
        userAccountService = UserAccountServiceSpy()
        val closeSessionUseCase = SessionCloser(
            accountDao = accountDao,
            sessionDao = sessionDao
        )
        val fetchUserAccountUseCase = UserAccountFetcher(
            remote = userAccountService,
            local = accountDao
        )
        val loginAsGuest = GuestSessionRepository(
            remote = remoteAuthenticationService,
            local = sessionDao
        )
        val loginUser = AuthenticationRepository(
            remote = remoteAuthenticationService,
            fetchUserAccount = fetchUserAccountUseCase,
            local = sessionDao
        )
        return LoginViewModel(
            closeSession = closeSessionUseCase,
            ioDispatcher = coroutineTestRule.dispatcher,
            loginGuest = loginAsGuest,
            loginUser = loginUser,
            validateInput = TextInputValidator()
        )
    }
}