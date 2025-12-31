package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_framework.data.fakes.fakeLoginRequest
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteError
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteSessionIdResponse
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteTokenResponse
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.remote.RemoteAuthenticationService
import com.davidluna.tmdb.auth_domain.usecases.FetchUserAccount
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisteredUserAuthenticationRepositoryTest {
    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var remote: RemoteAuthenticationService

    @MockK
    private lateinit var local: SessionDao

    @MockK
    private lateinit var fetchUserAccount: FetchUserAccount

    @Test
    fun `GIVEN sut WHEN sut is created THEN verify no side effects`() {
        buildSUT()

        verify { remote wasNot called }
        verify { fetchUserAccount wasNot called }
        verify { local wasNot called }
    }

    @Test
    fun `GIVEN all remote calls and local insertion succeed WHEN invoke is called THEN function returns Right(Unit)`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { remote.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { remote.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
            coEvery { fetchUserAccount() } returns Unit.right()
            coEvery { local.insertSession(any()) } returns Long.MIN_VALUE

            val actual = sut.invoke(fakeLoginRequest)

            assertTrue(actual.isRight())
        }

    @Test
    fun `GIVEN createRequestToken returns Left WHEN invoke is called THEN function returns Left with the corresponding AppError`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createRequestToken() } returns fakeRemoteError.left()

            val actual = sut.invoke(fakeLoginRequest)

            assertTrue(actual.isLeft())
        }

    @Test
    fun `GIVEN authorizeToken returns Left WHEN invoke is called THEN function returns Left with the corresponding AppError`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { remote.authorizeToken(any()) } returns fakeRemoteError.left()

            val actual = sut.invoke(fakeLoginRequest)

            assertTrue(actual.isLeft())
        }

    @Test
    fun `GIVEN createSessionId returns Left WHEN invoke is called THEN function returns Left with the corresponding AppError`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { remote.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { remote.createSessionId(any()) } returns fakeRemoteError.left()

            val actual = sut.invoke(fakeLoginRequest)

            assertTrue(actual.isLeft())
        }

    @Test
    fun `GIVEN fetchUserAccountUseCase returns Left WHEN invoke is called THEN function returns Left with the corresponding AppError`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { remote.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { remote.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
            coEvery { fetchUserAccount() } returns fakeRemoteError.toAppError().left()

            val actual = sut.invoke(fakeLoginRequest)

            assertTrue(actual.isLeft())
        }

    @Test
    fun `GIVEN local insertSession throws exception WHEN invoke is called THEN function returns Left with an AppError DataError Local`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createRequestToken() } returns fakeRemoteTokenResponse.right()
            coEvery { remote.authorizeToken(any()) } returns fakeRemoteTokenResponse.right()
            coEvery { remote.createSessionId(any()) } returns fakeRemoteSessionIdResponse.right()
            coEvery { fetchUserAccount() } returns Unit.right()
            coEvery { local.insertSession(any()) } throws IllegalStateException("some test error")

            val actual = sut.invoke(fakeLoginRequest)

            assertTrue(actual.isLeft())
        }

    private fun buildSUT() = AuthenticationRepository(
        remote = remote,
        fetchUserAccount = fetchUserAccount,
        local = local
    )
}