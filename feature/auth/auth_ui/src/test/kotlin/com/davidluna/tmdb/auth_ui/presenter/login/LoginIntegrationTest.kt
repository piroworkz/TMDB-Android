package com.davidluna.tmdb.auth_ui.presenter.login

import app.cash.turbine.test
import com.davidluna.tmdb.auth_data.data.local.database.dao.AccountDaoSpy
import com.davidluna.tmdb.auth_data.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_data.data.remote.AuthenticationApiSpy
import com.davidluna.tmdb.auth_data.data.remote.UserAccountApiSpy
import com.davidluna.tmdb.auth_data.utils.TextInputValidator
import com.davidluna.tmdb.auth_data.repositories.AccountRepository
import com.davidluna.tmdb.auth_data.repositories.AuthenticationRepository
import com.davidluna.tmdb.auth_domain.entities.LoginMethod
import com.davidluna.tmdb.auth_domain.usecases.OpenSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateInput
import com.davidluna.tmdb.auth_ui.fakes.fakeAppError
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode.SERVER
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LoginIntegrationTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var openSession: OpenSession
    private lateinit var validateInput: ValidateInput

    private lateinit var authAPI: AuthenticationApiSpy
    private lateinit var sessionDao: SessionDaoSpy
    private lateinit var userAccountApi: UserAccountApiSpy
    private lateinit var accountDao: AccountDaoSpy

    @Test
    fun `GIVEN sut WHEN is created THEN verify no side effects`() {
        val initialState = LoginViewModel.State()

        assertEquals(initialState, buildSut().state.value)
    }

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

    @Test
    fun `GIVEN valid credentials WHEN LoginButtonClicked event AND open fails THEN delivers appError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSut()
            val loginMethod = LoginMethod.AuthCredentials("username@mail.com", "password")
            val expected = fakeAppError

            sut.onEvent(LoginEvent.LoginButtonClicked(loginMethod.username, loginMethod.password))
            accountDao.throwException(expected)

            sut.state.test {
                awaitItem()
                val actual = awaitItem()
                assertEquals(expected, actual.appError)
                assertFalse(actual.isLoggedIn)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN LoginMethod AsGuest WHEN GuestButtonClicked event AND open succeeds THEN delivers isLoggedIn state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSut()

            sut.onEvent(LoginEvent.GuestButtonClicked)

            sut.state.test {
                awaitItem()
                val actual = awaitItem().isLoggedIn
                assertTrue(actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

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
}