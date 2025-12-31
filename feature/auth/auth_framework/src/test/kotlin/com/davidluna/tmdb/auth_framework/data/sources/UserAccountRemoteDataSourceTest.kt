package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteError
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRemoteUserAccountDetail
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRoomAccount
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.auth_framework.data.remote.UserAccountServiceSpy
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class UserAccountRemoteDataSourceTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    lateinit var remote: UserAccountServiceSpy

    @MockK
    lateinit var local: AccountDao

    @Test
    fun `GIVEN account exists locally WHEN invoke THEN should do nothing`() = runTest {
        coEvery { local.hasAccount() } returns true

        val result: Unit = buildSUT().invoke().getOrElse { }

        assert(result == Unit)
        coVerify(exactly = 0) { remote.getAccount() }
        coVerify(exactly = 0) { local.insertAccount(any()) }
    }

    @Test
    fun `GIVEN account does not exist locally WHEN invoke THEN should fetch and store account`() = runTest {
        val sut = buildSUT()
        val remoteResponse = fakeRemoteUserAccountDetail.right()

        coEvery { local.hasAccount() } returns false
        coEvery { remote.getAccount() } returns remoteResponse
        coEvery { local.insertAccount(any()) } returns 1L

        val result = sut.invoke()

        assertTrue(result.isRight())
        coVerify { remote.getAccount() }
        coVerify { local.insertAccount(fakeRoomAccount) }
    }

    @Test
    fun `GIVEN account does not exist locally AND remote returns error WHEN invoke THEN should not insert account`() = runTest {
        val sut = buildSUT()

        coEvery { local.hasAccount() } returns false
        coEvery { remote.getAccount() } returns fakeRemoteError.left()

        val result: Either<AppError, Unit> = sut()

        assertTrue(result.isLeft())
        coVerify(exactly = 1) { remote.getAccount() }
        coVerify(exactly = 0) { local.insertAccount(any()) }
    }

    private fun buildSUT() = UserAccountFetcher(remote, local)
}
