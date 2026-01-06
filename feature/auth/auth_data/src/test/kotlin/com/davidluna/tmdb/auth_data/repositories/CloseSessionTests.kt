package com.davidluna.tmdb.auth_data.repositories

import androidx.sqlite.SQLiteException
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CloseSessionTests {

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

    private val illegalStateException = IllegalStateException("Account not deleted or no session found").toAppError()

    @Test
    fun `GIVEN sut WHEN is created THEN delivers no side effects`() {
        buildSUT()

        verify { authenticationApi wasNot called }
        verify { sessionDao wasNot called }
        verify { accountRepository wasNot called }
    }

    @Test
    fun `GIVEN no session in db WHEN hasSession is false THEN close delivers error`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = illegalStateException

            coEvery { sessionDao.hasSession() } returns false

            val actual = sut.close()
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN no session in db WHEN account is not deleted THEN close delivers error `(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = illegalStateException

            coEvery { sessionDao.hasSession() } returns false
            coEvery { accountRepository.isAccountDeleted() } returns false

            val actual = sut.close()
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN session exists in db WHEN session is deleted AND account is deleted THEN close delivers null`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { sessionDao.hasSession() } returns true
            coEvery { sessionDao.deleteSession() } returns 1
            coEvery { accountRepository.isAccountDeleted() } returns true

            val actual = sut.close()

            assertNull(actual)
        }

    @Test
    fun `GIVEN session exists in db WHEN session is not deleted THEN close delivers error`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = illegalStateException

            coEvery { sessionDao.hasSession() } returns true
            coEvery { sessionDao.deleteSession() } returns -1
            coEvery { accountRepository.isAccountDeleted() } returns true

            val actual = sut.close()
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN session exists in db WHEN deleteSession throws exception THEN close delivers error on left`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = SQLiteException().toAppError()

            coEvery { sessionDao.hasSession() } returns true
            coEvery { sessionDao.deleteSession() } throws SQLiteException()

            val actual = sut.close()
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN session exists in db WHEN session is deleted AND isAccountDeleted fails THEN close delivers error on left`(): Unit =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = illegalStateException

            coEvery { sessionDao.hasSession() } returns true
            coEvery { sessionDao.deleteSession() } returns 1
            coEvery { accountRepository.isAccountDeleted() } returns false

            val actual = sut.close()

            assertEquals(expected, actual)
        }


    private fun buildSUT(): CloseSession = AuthenticationRepository(
        authAPI = authenticationApi,
        sessionDao = sessionDao,
        accountDetailsRepository = accountRepository
    )
}
