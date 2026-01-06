package com.davidluna.tmdb.auth_data.repositories

import androidx.sqlite.SQLiteException
import app.cash.turbine.test
import com.davidluna.tmdb.auth_data.data.fakeDomainSession
import com.davidluna.tmdb.auth_data.data.fakeRoomSession
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class ObserveSessionTest {
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
    fun `GIVEN no session in db WHEN getSession is called THEN session emits null`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            every { sessionDao.getSession() } returns flowOf(null)

            sut.session.test {
                val actual = awaitItem()
                assertNull(actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN session in db WHEN getSession is called THEN session emits session`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = fakeDomainSession

            every { sessionDao.getSession() } returns flowOf(fakeRoomSession)

            sut.session.test {
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN session in db WHEN getSession throws exception THEN session emits error`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = SQLiteException()

            every { sessionDao.getSession() } returns flow { throw expected }

            sut.session.test {
                val actual = awaitError()
                assertEquals(expected, actual)
                cancel()
            }
        }

    private fun buildSUT(): ObserveSession = AuthenticationRepository(
        authAPI = authenticationApi,
        sessionDao = sessionDao,
        accountDetailsRepository = accountRepository
    )
}