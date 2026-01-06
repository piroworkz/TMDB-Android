package com.davidluna.tmdb.auth_data.repositories

import com.davidluna.tmdb.auth_data.fakes.fakeRoomSession
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ValidateSessionTests {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var authenticationApi: AuthenticationApi

    @MockK
    private lateinit var sessionDao: SessionDao

    @MockK
    private lateinit var accountRepository: AccountDetailsRepository

    @Test
    fun `GIVEN sut WHEN sut is created THEN verify no side effects`() {
        buildSUT()

        verify { authenticationApi wasNot called }
        verify { sessionDao wasNot called }
        verify { accountRepository wasNot called }
    }

    @Test
    fun `GIVEN future expiration date AND session exists in db WHEN getSession succeeds THEN isValid delivers true`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val futureDate = buildDate(addAmount = 1)
            val expiration = utcStringFromDate(futureDate)

            coEvery { sessionDao.hasSession() } returns true
            every { sessionDao.getSession() } returns flowOf(fakeRoomSession.copy(expiresAt = expiration))

            val actual = sut.isValid()
            assertTrue(actual)
        }

    @Test
    fun `GIVEN past expiration date AND session exists in db  WHEN getSession succeeds THEN isValid delivers false`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val pastDate = buildDate(addAmount = -1)
            val expiration = utcStringFromDate(pastDate)

            coEvery { sessionDao.hasSession() } returns true
            every { sessionDao.getSession() } returns flowOf(fakeRoomSession.copy(expiresAt = expiration))

            val actual = sut.isValid()
            assertFalse(actual)
        }

    @Test
    fun `GIVEN expiration date is in invalid format AND session exists in db WHEN getSession succeeds THEN isValid delivers false`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val invalidDate = "invalid-date"

            coEvery { sessionDao.hasSession() } returns true
            every { sessionDao.getSession() } returns flowOf(fakeRoomSession.copy(expiresAt = invalidDate))

            val actual = sut.isValid()

            assertFalse(actual)
        }

    @Test
    fun `GIVEN session does not exists in db WHEN hasSession is false THEN isValid delivers false`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { sessionDao.hasSession() } returns false
            val actual = sut.isValid()

            assertFalse(actual)
        }

    private fun buildDate(calendarField: Int = Calendar.HOUR, addAmount: Int): Date =
        Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply { add(calendarField, addAmount) }.time

    private fun utcStringFromDate(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(date)


    private fun buildSUT(): ValidateSession = AuthenticationRepository(
        authAPI = authenticationApi,
        sessionDao = sessionDao,
        accountDetailsRepository = accountRepository
    )
}