package com.davidluna.tmdb.auth_ui.presenter.login

import app.cash.turbine.test
import com.davidluna.tmdb.auth_domain.entities.LoginMethod
import com.davidluna.tmdb.auth_domain.entities.TextInputError
import com.davidluna.tmdb.auth_domain.usecases.OpenSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateInput
import com.davidluna.tmdb.auth_ui.fakes.fakeAppError
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var openSession: OpenSession

    @MockK
    private lateinit var validateInput: ValidateInput

    @Test
    fun `GIVEN sut WHEN is created THEN verify no side effects`() {
        buildSUT()

        verify { openSession wasNot called }
        verify { validateInput wasNot called }
    }

    @Test
    fun `GIVEN valid password WHEN onEvent SetPassword THEN delivers password state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val password = "password"
            val expected = LoginViewModel.State().copy(password = password)

            coEvery { validateInput(any(), any()) } returns null

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetPassword(password))
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN invalid password WHEN onEvent SetPassword THEN delivers passwordError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val invalidPassword = "***"
            val expected = TextInputError.InvalidLength(8)

            coEvery { validateInput(any(), any()) } returns expected

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetPassword(invalidPassword))
                val actual = awaitItem().passwordError
                assertEquals(expected.message, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN empty password WHEN onEvent SetPassword THEN delivers passwordError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val emptyPassword = String()
            val expected = TextInputError.Required

            coEvery { validateInput(any(), any()) } returns expected

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetPassword(emptyPassword))
                val actual = awaitItem().passwordError
                assertEquals(expected.message, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN null password WHEN onEvent SetPassword THEN delivers passwordError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val nullPassword: String? = null
            val expected = TextInputError.Required

            coEvery { validateInput(any(), any()) } returns expected

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetPassword(nullPassword))
                val actual = awaitItem().passwordError
                assertEquals(expected.message, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }


    @Test
    fun `GIVEN valid username WHEN onEvent SetUsername THEN delivers username state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val username = "username@mail.com"
            val expected = LoginViewModel.State().copy(username = username)

            coEvery { validateInput(any(), any()) } returns null

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetUsername(username))
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN invalid username WHEN onEvent SetUsername THEN delivers usernameError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val invalidUsername = "***"
            val expected = TextInputError.InvalidEmail

            coEvery { validateInput(any(), any()) } returns expected

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetUsername(invalidUsername))
                val actual = awaitItem().usernameError
                assertEquals(expected.message, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN empty username WHEN onEvent SetUsername THEN delivers usernameError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val emptyUsername = String()
            val expected = TextInputError.Required

            coEvery { validateInput(any(), any()) } returns expected

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetUsername(emptyUsername))
                val actual = awaitItem().usernameError
                assertEquals(expected.message, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN null username WHEN onEvent SetUsername THEN delivers usernameError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val nullUsername: String? = null
            val expected = TextInputError.Required

            coEvery { validateInput(any(), any()) } returns expected

            sut.state.test {
                awaitItem()
                sut.onEvent(LoginEvent.SetUsername(nullUsername))
                val actual = awaitItem().usernameError
                assertEquals(expected.message, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN valid credentials WHEN LoginButtonClicked event AND open succeeds THEN delivers isLoggedIn state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = LoginMethod.AuthCredentials("username@mail.com", "password")

            coEvery { validateInput(any(), any()) } returns null
            coEvery { openSession.open(loginMethod) } returns null

            sut.onEvent(LoginEvent.LoginButtonClicked(loginMethod.username, loginMethod.password))
            sut.state.test {
                awaitItem()
                val actual = awaitItem().isLoggedIn
                assertTrue(actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN valid credentials WHEN LoginButtonClicked event AND open fails THEN delivers appError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = LoginMethod.AuthCredentials("username@mail.com", "password")

            coEvery { validateInput(any(), any()) } returns null
            coEvery { openSession.open(loginMethod) } returns fakeAppError

            sut.onEvent(LoginEvent.LoginButtonClicked(loginMethod.username, loginMethod.password))
            sut.state.test {
                awaitItem()
                val actual = awaitItem().appError
                assertEquals(fakeAppError, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN LoginMethod AsGuest WHEN GuestButtonClicked event AND open succeeds THEN delivers isLoggedIn state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = LoginMethod.AsGuest

            coEvery { openSession.open(loginMethod) } returns null
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
            val sut = buildSUT()
            val loginMethod = LoginMethod.AsGuest

            coEvery { openSession.open(loginMethod) } returns fakeAppError
            sut.onEvent(LoginEvent.GuestButtonClicked)

            sut.state.test {
                awaitItem()
                val actual = awaitItem().appError
                assertEquals(fakeAppError, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN AppError state not null WHEN ResetAppError event THEN delivers appError state update`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = LoginMethod.AsGuest

            coEvery { openSession.open(loginMethod) } returns fakeAppError
            sut.onEvent(LoginEvent.GuestButtonClicked)

            sut.state.test {
                awaitItem()
                awaitItem()
                sut.onEvent(LoginEvent.ResetAppError)
                val actual = awaitItem().appError
                assertNull(actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildSUT(): LoginViewModel = LoginViewModel(
        ioDispatcher = coroutineTestRule.dispatcher,
        openSession = openSession,
        validateInput = validateInput
    )

}