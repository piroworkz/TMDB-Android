package com.davidluna.tmdb.auth_ui.fakes

import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

val fakeSession = Session(
    sessionId = "1d77cf20ff3bf21ead146217af8e6b58",
    isGuest = false,
    expiresAt = null
)

val fakeGuestSession = Session(
    sessionId = "1d77cf20ff3bf21ead146217af8e6b58",
    isGuest = true,
    expiresAt = "2025-08-10 23:03:47 UTC"
)

val fakeException = Exception("fake exception")

val fakeAppError = fakeException.toAppError()
val fakeRemoteError = RemoteError(
    statusCode = 34,
    statusMessage = "The resource you requested could not be found.",
    success = false
)

val fakeUsername = "mail.account@someProvider.com"
val fakePassword = "someValidPassword"

val fakeRoomSession = RoomSession(
    sessionId = fakeSession.sessionId,
    isGuest = fakeSession.isGuest,
    expiresAt = fakeSession.expiresAt
)

val fakeGuestRoomSession = RoomSession(
    sessionId = fakeGuestSession.sessionId,
    isGuest = fakeGuestSession.isGuest,
    expiresAt = fakeGuestSession.expiresAt
)

fun buildDate(addDays: Int = 2): String? {
    val tzUtc = TimeZone.getTimeZone("UTC")
    val cal = Calendar.getInstance(tzUtc).apply {
        add(Calendar.DAY_OF_YEAR, addDays)
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'").apply {
        timeZone = tzUtc
    }
    return sdf.format(cal.time)
}