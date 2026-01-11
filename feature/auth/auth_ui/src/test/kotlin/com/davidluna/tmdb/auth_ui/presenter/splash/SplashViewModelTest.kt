package com.davidluna.tmdb.auth_ui.presenter.splash

import app.cash.turbine.test
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.auth_ui.fakes.fakeGuestSession
import com.davidluna.tmdb.auth_ui.fakes.fakeSession
import com.davidluna.tmdb.auth_ui.view.splash.holder.CurrentScreen
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var closeSession: CloseSession

    @MockK
    private lateinit var validateSession: ValidateSession

    @MockK
    private lateinit var observeSession: ObserveSession

    @Test
    fun `GIVEN sut WHEN is created THEN isLoggedIn delivers null`() {
        val sut = buildSUT()

        val actual = sut.isLoggedIn.value

        assertNull(actual)
    }

    @Test
    fun `GIVEN PERMISSIONS_PROMPT screen WHEN updateCurrentScreen THEN currentScreen delivers PERMISSIONS_PROMPT`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val expected = CurrentScreen.PERMISSIONS_PROMPT

            sut.updateCurrentScreen(expected)

            sut.state.test {
                awaitItem()
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN registered user session exists WHEN checkSessionStatus THEN isLoggedIn delivers true`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { observeSession.session } returns flowOf(fakeSession)
            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == true)
        }

    @Test
    fun `GIVEN guest session exists WHEN checkSessionStatus THEN isLoggedIn delivers true`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { observeSession.session } returns flowOf(fakeGuestSession)
            coEvery { validateSession.isValid() } returns true
            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == true)
        }

    @Test
    fun `GIVEN no session exists WHEN checkSessionStatus THEN isLoggedIn delivers false`(): Unit =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { observeSession.session } returns flowOf(null)
            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == false)
        }

    private fun buildSUT() = SplashViewModel(
        ioDispatcher = coroutineTestRule.dispatcher,
        validateSession = validateSession,
        observeSession = observeSession,
        closeSession = closeSession
    )
}