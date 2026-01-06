package com.davidluna.tmdb.auth_ui.presenter.splash

import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var validateSession: ValidateSession

    @MockK
    private lateinit var observeSession: ObserveSession

    @Test
    fun `GIVEN the SplashViewModel is initialized THEN isLoggedIn_value should be null`() {
        val sut = buildSUT()

        val actual = sut.isLoggedIn.value

        assertNull(actual)
    }

    private fun buildSUT() = SplashViewModel(
        ioDispatcher = coroutineTestRule.dispatcher,
        validateSession = validateSession,
        observeSession = observeSession
    )
}