package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.getOrElse
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CloseSessionDataSourceTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var accountDao: AccountDao

    @MockK
    private lateinit var sessionDao: SessionDao

    @Test
    fun `GIVEN closeSession returns true WHEN invoke is called THEN Right true is returned`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            coEvery { sessionDao.deleteSession() } returns 1
            coEvery { accountDao.deleteAccount() } returns 1

            val result = sut.invoke()

            assertTrue(result.isRight())
            assertEquals(true, result.getOrElse { false })
        }

    @Test
    fun `GIVEN closeSession returns false WHEN invoke is called THEN Right false is returned`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            coEvery { sessionDao.deleteSession() } returns -1
            coEvery { accountDao.deleteAccount() } returns 1

            val result = sut.invoke()

            assertTrue(result.isRight())
            assertEquals(false, result.getOrElse { true })
        }

    @Test
    fun `GIVEN DAO throws exception WHEN invoke is called THEN Left AppError is returned`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            coEvery { accountDao.deleteAccount() } throws RuntimeException("boom")

            val result = sut.invoke()

            assertTrue(result.isLeft())
            val error = result.swap().getOrNull()
            assertNotNull(error)
            assertTrue(error is AppError)
        }

    private fun buildSUT() = SessionCloser(
        accountDao = accountDao,
        sessionDao = sessionDao
    )
}
