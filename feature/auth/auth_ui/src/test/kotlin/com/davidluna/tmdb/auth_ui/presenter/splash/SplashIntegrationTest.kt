package com.davidluna.tmdb.auth_ui.presenter.splash

import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.auth_data.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.data.local.database.dao.SessionDaoSpy
import com.davidluna.tmdb.auth_data.data.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_ui.fakes.fakeGuestSession
import com.davidluna.tmdb.auth_ui.fakes.fakeSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class SplashIntegrationTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var sessionDao: SessionDao

    @Test
    fun `GIVEN a valid session WHEN checking session status THEN isLoggedIn should be true`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            (sessionDao as SessionDaoSpy).insertSession(fakeSession.toEntity())
            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == true)
        }

    @Test
    fun `GIVEN no session WHEN checking session status THEN isLoggedIn should be false`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == false)
        }

    @Test
    fun `GIVEN a guest session expired WHEN checking session status THEN isLoggedIn should be false`() =
        coroutineTestRule.scope.runTest {
            val expiredGuestSession = fakeGuestSession.copy(expiresAt = "2022-08-10 23:03:47 UTC")
            val sut = buildSUT()

            (sessionDao as SessionDaoSpy).insertSession(expiredGuestSession.toEntity())
            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == false)
        }

    @Test
    fun `GIVEN a valid guest session WHEN checking session status THEN isLoggedIn should be true`() =
        coroutineTestRule.scope.runTest {
            val validGuestSession = fakeGuestSession.copy(expiresAt = buildValidExpirationDate())
            val sut = buildSUT()

            (sessionDao as SessionDaoSpy).insertSession(validGuestSession.toEntity())
            sut.checkSessionStatus()
            advanceUntilIdle()

            assertTrue(sut.isLoggedIn.value == true)
        }

    private fun buildSUT(): SplashViewModel {
        sessionDao = SessionDaoSpy()
        val isGuestSessionValid = GuestSessionExpirationValidator()
        val getSessionUseCase = SessionStore(sessionDao)

        return SplashViewModel(
            ioDispatcher = coroutineTestRule.dispatcher,
            validateSession = isGuestSessionValid,
            observeSession = getSessionUseCase
        )
    }

    private fun Session.toEntity(): RoomSession {
        return RoomSession(
            sessionId = sessionId,
            isGuest = isGuest,
            expiresAt = expiresAt,
        )
    }

    private fun buildValidExpirationDate(): String? {
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
            .withZone(ZoneOffset.UTC)
        return formatter.format(Instant.now().plusSeconds(60 * 60 * 24))
    }
}