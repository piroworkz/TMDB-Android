package com.davidluna.tmdb.auth_ui.presenter.login

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_domain.entities.TextInputError.Required
import com.davidluna.tmdb.auth_domain.usecases.LoginAsGuest
import com.davidluna.tmdb.auth_domain.usecases.LoginWithCredentials
import com.davidluna.tmdb.auth_domain.usecases.GetTextInputError
import com.davidluna.tmdb.auth_ui.fakes.fakeAppError
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.GuestButtonClicked
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.LoginButtonClicked
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.SetPassword
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.SetUsername
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var closeSession: CloseSession

    @MockK
    private lateinit var loginAsGuest: LoginAsGuest

    @MockK
    private lateinit var loginWithCredentials: LoginWithCredentials

    @MockK
    private lateinit var getTextInputError: GetTextInputError

    private val initialState = LoginViewModel.State()

    @Test
    fun `GIVEN ViewModel WHEN is created THEN verify no side effects`() {
        buildSUT()

        verify { closeSession wasNot called }
        verify { loginAsGuest wasNot called }
        verify { loginWithCredentials wasNot called }
        verify { getTextInputError wasNot called }
    }

    @Test
    fun `GIVEN ViewModel initialized WHEN getState is called THEN initial state is correct`() {
        val sut = buildSUT()

        val actual = sut.state.value

        assertEquals(initialState, actual)
    }

    @Test
    fun `GIVEN appError is set WHEN ResetAppError event is dispatched THEN appError is cleared`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { closeSession.invoke() } returns fakeAppError.left()
            every { getTextInputError.invoke(any(), any()) } returns null

            sut.onEvent(LoginButtonClicked("username", "password"))

            sut.state.test {
                val initialValue = awaitItem().appError
                val onFailure = awaitItem().appError
                sut.onEvent(LoginEvent.ResetAppError)
                val actual = awaitItem().appError

                assertNull(initialValue)
                assertNotNull(onFailure)
                assertNull(actual)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN guest login succeeds WHEN GuestButtonClicked event is dispatched THEN isLoggedIn is true`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val expected = initialState.copy(isLoggedIn = true)

            coEvery { loginAsGuest.invoke() } returns Unit.right()

            sut.onEvent(GuestButtonClicked)

            sut.state.test {
                awaitItem()
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN guest login fails WHEN GuestButtonClicked event is dispatched THEN appError is set`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { loginAsGuest() } returns fakeAppError.left()
            sut.onEvent(GuestButtonClicked)

            sut.state.test {
                skipItems(1)
                val actual = awaitItem().appError

                assertNotNull(actual)
                cancelAndConsumeRemainingEvents()
            }

        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN invalid username or password WHEN LoginButtonClicked THEN login is not attempted`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            every {
                getTextInputError(
                    any(), any()
                )
            } returns null andThen Required

            sut.onEvent(LoginButtonClicked("username", ""))
            advanceUntilIdle()

            coVerify(exactly = 0) { closeSession() }
            coVerify(exactly = 0) { loginWithCredentials.invoke(any()) }
        }

    @Test
    fun `GIVEN valid credentials AND closeSession fails WHEN LoginButtonClicked THEN appError is set`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { closeSession.invoke() } returns fakeAppError.left()
            every { getTextInputError.invoke(any(), any()) } returns null

            sut.onEvent(LoginButtonClicked("username", "password"))

            sut.state.test {
                val initialValue = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialValue)
                assertNotNull(actual)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN valid credentials AND loginUser fails WHEN LoginButtonClicked THEN appError is set`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { loginWithCredentials.invoke(any()) } returns fakeAppError.left()
            coEvery { closeSession.invoke() } returns true.right()
            every { getTextInputError.invoke(any(), any()) } returns null

            sut.onEvent(LoginButtonClicked("username", "password"))

            sut.state.test {
                val initialValue = awaitItem().appError
                val actual = awaitItem().appError

                assertNull(initialValue)
                assertNotNull(actual)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN valid password WHEN SetPassword event is dispatched THEN password is updated and passwordError is cleared`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val expected = initialState.copy(password = "validPassword")

            every { getTextInputError(any(), any()) } returns Required andThen null
            sut.onEvent(SetPassword("invalidPassword"))

            sut.state.test {
                skipItems(1)
                val invalidPasswordError = awaitItem()
                sut.onEvent(SetPassword("validPassword"))
                val actual = awaitItem()

                assertEquals(Required.message, invalidPasswordError.passwordError)
                assertEquals(expected, actual)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN invalid password WHEN SetPassword is dispatched THEN password is updated and passwordError is set`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            every { getTextInputError(any(), any()) } returns Required andThen null
            sut.onEvent(SetPassword("invalidPassword"))

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(Required.message, actual.passwordError)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN valid username WHEN SetUsername is dispatched THEN username is updated and usernameError is cleared`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val expected = initialState.copy(username = "validUsername")

            every { getTextInputError(any(), any()) } returns Required andThen null
            sut.onEvent(SetUsername("invalidUsername"))

            sut.state.test {
                skipItems(1)
                val invalidUsernameError = awaitItem()
                sut.onEvent(SetUsername("validUsername"))
                val actual = awaitItem()

                assertEquals(Required.message, invalidUsernameError.usernameError)
                assertEquals(expected, actual)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN invalid username WHEN SetUsername is dispatched THEN username is updated and usernameError is set`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            every { getTextInputError(any(), any()) } returns Required andThen null
            sut.onEvent(SetUsername("invalidUsername"))

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(Required.message, actual.usernameError)
                cancelAndConsumeRemainingEvents()
            }
        }

    private fun buildSUT(): LoginViewModel = LoginViewModel(
        closeSession = closeSession,
        ioDispatcher = coroutineTestRule.dispatcher,
        loginGuest = loginAsGuest,
        loginUser = loginWithCredentials,
        validateInput = getTextInputError
    )

}