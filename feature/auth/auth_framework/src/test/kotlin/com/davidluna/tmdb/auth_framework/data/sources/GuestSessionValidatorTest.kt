package com.davidluna.tmdb.auth_framework.data.sources

import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class GuestSessionValidatorTest {

    private val sut = GuestSessionExpirationValidator()

    @Test
    fun `GIVEN expiration date is in the future WHEN isGuestSessionValid is called THEN returns true`() {
        val futureDate = buildDate(addAmount = 1)
        val expiration = utcStringFromDate(futureDate)

        val actual = sut(expiration)

        Assert.assertTrue(actual)
    }

    @Test
    fun `GIVEN expiration date is in the past WHEN isGuestSessionValid is called THEN returns false`() {
        val pastDate = buildDate(addAmount = -1)
        val expiration = utcStringFromDate(pastDate)

        val actual = sut(expiration)

        Assert.assertFalse(actual)
    }

    @Test
    fun `GIVEN expiration date is null WHEN isGuestSessionValid is called THEN returns false`() {

        val actual = sut(null)

        Assert.assertFalse(actual)
    }

    @Test
    fun `GIVEN expiration date is in invalid format WHEN isGuestSessionValid is called THEN returns false`() {
        val invalidDate = "invalid-date"

        val actual = sut(invalidDate)

        Assert.assertFalse(actual)
    }

    private fun buildDate(calendarField: Int = Calendar.HOUR, addAmount: Int): Date =
        Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply { add(calendarField, addAmount) }.time

    private fun utcStringFromDate(date: Date): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(date)
}