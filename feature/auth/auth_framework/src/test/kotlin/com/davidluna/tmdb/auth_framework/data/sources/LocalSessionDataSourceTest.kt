package com.davidluna.tmdb.auth_framework.data.sources

import app.cash.turbine.test
import com.davidluna.tmdb.auth_framework.data.fakes.fakeDomainSession
import com.davidluna.tmdb.auth_framework.data.fakes.fakeRoomSession
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomSession
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

class LocalSessionDataSourceTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var sessionDao: SessionDao

    @Test
    fun `GIVEN RoomSession exists WHEN flow is collected THEN it should emit mapped Session`() = coroutineRule.scope.runTest {
        val sut = buildSUT()

        every { sessionDao.getSession() } returns flowOf(fakeRoomSession)

        sut().test {
            assertEquals(fakeDomainSession, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN RoomSession is null WHEN flow is collected THEN it should emit null`() = coroutineRule.scope.runTest {
        val sut = buildSUT()

        every { sessionDao.getSession() } returns flowOf(null)

        sut().test {
            assertEquals(null, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN database is updated WHEN flow is collected THEN it should emit new value`() = coroutineRule.scope.runTest {
        val sut = buildSUT()
        val firstSessionExpected = fakeDomainSession
        val secondSessionExpected = firstSessionExpected.copy(sessionId = "secondGuest")
        val sessionFlow = MutableStateFlow<RoomSession?>(fakeRoomSession)

        every { sessionDao.getSession() } returns sessionFlow

        sut().test {
            val first = awaitItem()
            assertEquals(firstSessionExpected, first)

            sessionFlow.value = fakeRoomSession.copy(sessionId = "secondGuest")

            val updated = awaitItem()
            assertEquals(secondSessionExpected, updated)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildSUT() = LocalSessionDataSource(sessionDao)
}