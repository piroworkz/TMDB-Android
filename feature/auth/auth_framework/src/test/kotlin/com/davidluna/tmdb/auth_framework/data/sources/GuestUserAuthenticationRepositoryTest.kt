package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteError
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteGuestSession
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.remote.RemoteAuthenticationService
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

class GuestUserAuthenticationRepositoryTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var remote: RemoteAuthenticationService

    @MockK
    private lateinit var local: SessionDao

    @Test
    fun `GIVEN sut WHEN sut is created THEN verify no side effects`() {
        buildSUT()

        verify { remote wasNot called }
        verify { local wasNot called }
    }

    @Test
    fun `GIVEN remote createGuestSession returns Right WHEN invoke is called THEN local insertSession is called and returns Right`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createGuestSession() } returns fakeRemoteGuestSession.right()
            coEvery { local.insertSession(any()) } returns Long.MIN_VALUE

            val actual = sut.invoke()

            assertTrue(actual.isRight())
        }

    @Test
    fun `GIVEN remote createGuestSession returns Left WHEN invoke is called THEN toAppError is invoked and returns Left AppError`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createGuestSession() } returns fakeRemoteError.left()

            val actual = sut.invoke()

            assertTrue(actual.isLeft())
        }

    @Test
    fun `GIVEN local insertSession throws exception WHEN invoke is called THEN tryCatch catches it and returns Left AppError`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remote.createGuestSession() } returns fakeRemoteGuestSession.right()
            coEvery { local.insertSession(any()) } throws IllegalStateException("some test error")

            val actual = sut.invoke()

            assertTrue(actual.isLeft())
        }

    private fun buildSUT() = GuestSessionRepository(remote, local)

}
