package com.davidluna.tmdb.auth_framework.data.sources

import com.davidluna.tmdb.auth_domain.usecases.IsGuestSessionValid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class GuestSessionExpirationValidator @Inject constructor() : IsGuestSessionValid {
    override operator fun invoke(expirationDate: String?): Boolean = try {
        requireNotNull(expirationDate)
        val expiresAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .parse(expirationDate) ?: Date()
        val isValid = Date().before(expiresAt)
        isValid
    } catch (_: Exception) {
        false
    }
}