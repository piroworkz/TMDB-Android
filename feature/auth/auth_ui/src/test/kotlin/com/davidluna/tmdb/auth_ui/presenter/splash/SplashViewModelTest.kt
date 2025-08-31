package com.davidluna.tmdb.auth_ui.presenter.splash

import com.davidluna.tmdb.auth_domain.usecases.GetSessionUseCase
import com.davidluna.tmdb.auth_domain.usecases.ValidateGuestSessionUseCase
import com.davidluna.tmdb.auth_ui.fakes.fakeSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var isGuestSessionValid: ValidateGuestSessionUseCase

    @MockK
    private lateinit var getSessionUseCase: GetSessionUseCase

    @Test
    fun `GIVEN the SplashViewModel is initialized THEN isLoggedIn_value should be null`() {
        val sut = buildSUT()

        val actual = sut.isLoggedIn.value

        assertNull(actual)
    }

    @Test
    fun `GIVEN getSessionUseCase returns null WHEN checkSessionStatus is called THEN isLoggedIn should be false`() =
        coroutineTestRule.scope.runTest {
            every { getSessionUseCase() } returns flowOf(null)
            val sut = buildSUT()

            val actual = sut.isLoggedIn.value

            assertNull(actual)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN getSessionUseCase returns a guest session AND isGuestSessionValid returns true WHEN checkSessionStatus is called THEN isLoggedIn should be true`() =
        coroutineTestRule.scope.runTest {
            val guestSession = fakeSession.copy(expiresAt = "guest_session_id", isGuest = true)
            every { getSessionUseCase() } returns flowOf(guestSession)
            every { isGuestSessionValid(any()) } returns true
            val sut = buildSUT()

            sut.checkSessionStatus()
            advanceUntilIdle()
            val actual = sut.isLoggedIn.value

            assertEquals(true, actual)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN getSessionUseCase returns a guest session AND isGuestSessionValid returns false WHEN checkSessionStatus is called THEN isLoggedIn should be false`() =
        coroutineTestRule.scope.runTest {
            val guestSession = fakeSession.copy(expiresAt = "guest_session_id", isGuest = true)
            every { getSessionUseCase() } returns flowOf(guestSession)
            every { isGuestSessionValid(any()) } returns false
            val sut = buildSUT()

            sut.checkSessionStatus()
            advanceUntilIdle()
            val actual = sut.isLoggedIn.value

            assertEquals(false, actual)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN getSessionUseCase returns a valid non-guest session WHEN checkSessionStatus is called THEN isLoggedIn should be true`() =
        coroutineTestRule.scope.runTest {
            every { getSessionUseCase() } returns flowOf(fakeSession)
            val sut = buildSUT()

            sut.checkSessionStatus()
            advanceUntilIdle()
            val actual = sut.isLoggedIn.value

            assertEquals(true, actual)
        }

    private fun buildSUT() = SplashViewModel(
        ioDispatcher = coroutineTestRule.dispatcher,
        isGuestSessionValid = isGuestSessionValid,
        getSessionUseCase = getSessionUseCase
    )
}