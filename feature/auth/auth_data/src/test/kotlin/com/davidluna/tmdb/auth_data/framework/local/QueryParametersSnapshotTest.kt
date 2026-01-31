package com.davidluna.tmdb.auth_data.framework.local

import com.davidluna.tmdb.auth_data.data.fakeGuestSession
import com.davidluna.tmdb.auth_data.data.fakeRoomGuestSession
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.core_data.framework.remote.interceptors.ParametersSnapshot.Keys
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QueryParametersSnapshotTest {
    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var getCountryCode: ObserveCountryCode

    @MockK
    private lateinit var sessionDao: SessionDao

    @Test
    fun `GIVEN no session AND country US WHEN invoke is called THEN returns defaults without session`() =
        coroutineRule.scope.runTest {
            every { sessionDao.getSession() } returns flowOf(null)
            every { getCountryCode.invoke() } returns flowOf("US")

            val sut = buildSut()
            advanceUntilIdle()

            val actual = sut()

            assertEquals("US", actual[Keys.REGION])
            assertEquals(Keys.DEFAULT_LANGUAGE, actual[Keys.LANGUAGE])
            assertEquals("en", actual[Keys.INCLUDE_IMAGE_LANGUAGE])
            assertFalse(actual.containsKey(Keys.SESSION_ID))
        }

    @Test
    fun `GIVEN no session AND country MX WHEN invoke is called THEN returns spanish params without session`() =
        coroutineRule.scope.runTest {
            every { sessionDao.getSession() } returns flowOf(null)
            every { getCountryCode.invoke() } returns flowOf("MX")

            val sut = buildSut()
            advanceUntilIdle()

            val actual = sut()

            assertEquals("MX", actual[Keys.REGION])
            assertEquals("es-mx", actual[Keys.LANGUAGE])
            assertEquals("es", actual[Keys.INCLUDE_IMAGE_LANGUAGE])
            assertFalse(actual.containsKey(Keys.SESSION_ID))
        }

    @Test
    fun `GIVEN session AND country US WHEN invoke is called THEN returns params with session`() =
        coroutineRule.scope.runTest {
            every { sessionDao.getSession() } returns flowOf(fakeRoomGuestSession)
            every { getCountryCode.invoke() } returns flowOf("US")

            val sut = buildSut()
            advanceUntilIdle()

            val actual = sut()

            assertEquals("US", actual[Keys.REGION])
            assertEquals(Keys.DEFAULT_LANGUAGE, actual[Keys.LANGUAGE])
            assertEquals("en", actual[Keys.INCLUDE_IMAGE_LANGUAGE])
            assertEquals(fakeGuestSession.sessionId, actual[Keys.SESSION_ID])
        }

    @Test
    fun `GIVEN session AND country MX WHEN invoke is called THEN returns params with session and spanish locale`() =
        coroutineRule.scope.runTest {
            every { sessionDao.getSession() } returns flowOf(fakeRoomGuestSession)
            every { getCountryCode.invoke() } returns flowOf("MX")

            val sut = buildSut()
            advanceUntilIdle()

            val actual = sut()

            assertEquals("MX", actual[Keys.REGION])
            assertEquals("es-mx", actual[Keys.LANGUAGE])
            assertEquals("es", actual[Keys.INCLUDE_IMAGE_LANGUAGE])
            assertEquals(fakeGuestSession.sessionId, actual[Keys.SESSION_ID])
        }

    private fun buildSut(): QueryParametersSnapshot {
        return QueryParametersSnapshot(
            getCountryCode = getCountryCode,
            sessionDao = sessionDao,
            scope = coroutineRule.scope
        )
    }
}