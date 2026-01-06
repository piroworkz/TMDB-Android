package com.davidluna.tmdb.media_data.data.local

import com.davidluna.tmdb.media_data.data.framework.paging.CachePolicyValidator
import com.davidluna.tmdb.media_data.data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.days

class CachePolicyValidatorTest {
    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private val sut: IsCacheExpired = CachePolicyValidator()

    @Test
    fun `GIVEN null lastUpdated WHEN invoke is called THEN return true`() {
        val lastUpdated: Long? = null

        val result = sut.invoke(lastUpdated)

        assertTrue(result)
    }

    @Test
    fun `GIVEN lastUpdated less than 7 days ago WHEN invoke is called THEN return false`() {
        val sevenDaysInMillis = 7.days.inWholeMilliseconds
        val recentTimestamp = System.currentTimeMillis() - (sevenDaysInMillis - 1)

        val result = sut.invoke(recentTimestamp)

        assertFalse(result)
    }

    @Test
    fun `GIVEN lastUpdated exactly 7 days ago WHEN invoke is called THEN return false`() {
        val sevenDaysInMillis = 7.days.inWholeMilliseconds
        val timestamp = System.currentTimeMillis() - sevenDaysInMillis

        val result = sut.invoke(timestamp)

        assertFalse(result)
    }

    @Test
    fun `GIVEN lastUpdated more than 7 days ago WHEN invoke is called THEN return true`() {
        val sevenDaysInMillis = 7.days.inWholeMilliseconds
        val oldTimestamp = System.currentTimeMillis() - (sevenDaysInMillis + 1)

        val result = sut.invoke(oldTimestamp)

        assertTrue(result)
    }
}