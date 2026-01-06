package com.davidluna.tmdb.auth_ui.fakes

import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_framework.data.remote.model.RemoteError

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