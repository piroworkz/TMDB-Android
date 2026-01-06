package com.davidluna.tmdb.auth_data.repositories

import android.database.sqlite.SQLiteException
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_data.fakes.fakeAuthCredentials
import com.davidluna.tmdb.auth_data.fakes.fakeRemoteError
import com.davidluna.tmdb.auth_data.fakes.fakeRemoteSessionIdResponse
import com.davidluna.tmdb.auth_data.fakes.fakeRemoteTokenResponse
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_domain.usecases.OpenSession
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
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

class OpenSessionTests {

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
    fun `GIVEN authCredentials method WHEN login succeeds THEN open delivers null`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = fakeAuthCredentials

            coEvery { authenticationApi.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
            coEvery { sessionDao.insertSession(any()) } returns 1
            coEvery { accountRepository.fetch() } returns null

            val actual = sut.open(loginMethod)
            assertNull(actual)
        }

    @Test
    fun `GIVEN authCredentials method WHEN createRequestToken fails THEN open delivers error`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = fakeAuthCredentials
            val expected = fakeRemoteError.toAppError()

            coEvery { authenticationApi.createRequestToken() } returns fakeRemoteError.left()

            val actual = sut.open(loginMethod)
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN authCredentials method WHEN authorizeToken fails THEN open delivers error on left`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = fakeAuthCredentials
            val expected = fakeRemoteError.toAppError()

            coEvery { authenticationApi.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.authorizeToken(any()) } returns fakeRemoteError.left()

            val actual = sut.open(loginMethod)
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN authCredentials method WHEN createSessionId fails THEN open delivers error on left`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = fakeAuthCredentials
            val expected = fakeRemoteError.toAppError()

            coEvery { authenticationApi.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.createSessionId(any()) } returns fakeRemoteError.left()

            val actual = sut.open(loginMethod)
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN authCredentials method WHEN insertSession fails THEN open delivers error on left`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = fakeAuthCredentials
            val expected = SQLiteException().toAppError()

            coEvery { authenticationApi.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
            coEvery { sessionDao.insertSession(any()) } throws SQLiteException()

            val actual = sut.open(loginMethod)
            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN authCredentials method WHEN fetch fails THEN open delivers error on left`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val loginMethod = fakeAuthCredentials
            val expected = SQLiteException().toAppError()

            coEvery { authenticationApi.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { authenticationApi.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
            coEvery { sessionDao.insertSession(any()) } returns 1
            coEvery { accountRepository.fetch() } returns expected

            val actual = sut.open(loginMethod)
            assertEquals(expected, actual)
        }

    private fun buildSUT(): OpenSession = AuthenticationRepository(
        authAPI = authenticationApi,
        sessionDao = sessionDao,
        accountDetailsRepository = accountRepository
    )

}
