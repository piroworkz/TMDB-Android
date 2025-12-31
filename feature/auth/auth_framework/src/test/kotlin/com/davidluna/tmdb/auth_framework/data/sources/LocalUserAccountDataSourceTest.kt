package com.davidluna.tmdb.auth_framework.data.sources

import app.cash.turbine.test
import com.davidluna.tmdb.auth_framework.data.fakes.fakeDomainAccount
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRoomAccount
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LocalUserAccountDataSourceTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var accountDao: AccountDao

    private fun buildSUT() = AccountStore(accountDao)

    @Test
    fun `GIVEN RoomUserAccount exists WHEN flow is collected THEN it emits mapped UserAccount`() =
        coroutineRule.scope.runTest {
            val sut = buildSUT()
            val expected = fakeDomainAccount
            every { accountDao.getAccount() } returns flowOf(fakeRoomAccount)

            sut().test {
                val result = awaitItem()

                assertEquals(expected, result)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN RoomUserAccount is null WHEN flow is collected THEN it emits null`() = coroutineRule.scope.runTest {
        val sut = buildSUT()

        every { accountDao.getAccount() } returns flowOf(null)

        sut().test {
            assertEquals(null, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN flow is being collected WHEN database is updated THEN flow emits new value`() = coroutineRule.scope.runTest {
        val sut = buildSUT()
        val firstExpected = fakeDomainAccount
        val secondExpected = firstExpected.copy(name = "Jane Smith")
        val flow = MutableStateFlow<RoomUserAccount?>(fakeRoomAccount)

        every { accountDao.getAccount() } returns flow

        sut().test {
            val firstResult = awaitItem()
            assertEquals(firstExpected, firstResult)

            flow.value = fakeRoomAccount.copy(name = "Jane Smith")

            val secondResult = awaitItem()
            assertEquals(secondExpected, secondResult)

            cancelAndIgnoreRemainingEvents()
        }
    }
}