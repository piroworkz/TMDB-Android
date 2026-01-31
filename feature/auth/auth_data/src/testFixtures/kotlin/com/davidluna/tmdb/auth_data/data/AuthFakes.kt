package com.davidluna.tmdb.auth_data.data

import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteGuestSession
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteSessionIdResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteTokenResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteUserAccountDetail
import com.davidluna.tmdb.auth_domain.entities.LoginMethod
import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.core_data.framework.remote.model.buildModel
import com.davidluna.tmdb.core_data.framework.remote.model.toAppError
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.reader.Reader.fromJson

val fakeRemoteError = fromJson<RemoteError>(Reader.REMOTE_ERROR)
val fakeRemoteGuestSession = fromJson<RemoteGuestSession>(Reader.AUTH_GUEST_SESSION)
val fakeRemoteSessionIdResponse = fromJson<RemoteSessionIdResponse>(Reader.AUTH_SESSION_NEW)
val fakeRemoteTokenResponse = fromJson<RemoteTokenResponse>(Reader.AUTH_TOKEN_NEW)
val fakeRemoteUserAccountDetail = fromJson<RemoteUserAccountDetail>(Reader.USER_ACCOUNT)

val fakeRoomGuestSession = with(fakeRemoteGuestSession) {
    RoomSession(
        id = 0,
        sessionId = guestSessionId,
        isGuest = true,
        expiresAt = expiresAt
    )
}

val fakeRoomSession = with(fakeRemoteSessionIdResponse) {
    RoomSession(
        id = 0,
        sessionId = sessionId,
        isGuest = false,
        expiresAt = null
    )
}

val fakeRoomAccount = with(fakeRemoteUserAccountDetail) {
    RoomUserAccount(
        userId = userId,
        name = name,
        username = username,
        avatarPath = avatar.tmdb.avatarPath.buildModel()
    )
}

val fakeGuestSession = with(fakeRoomGuestSession) {
    Session(
        sessionId = sessionId,
        isGuest = isGuest,
        expiresAt = expiresAt
    )
}

val fakeSession = with(fakeRoomSession) {
    Session(
        sessionId = sessionId,
        isGuest = isGuest,
        expiresAt = expiresAt
    )
}

val fakeAccount = with(fakeRoomAccount) {
    UserAccount(
        userId = userId,
        name = name,
        username = username,
        avatarPath = avatarPath
    )
}

val fakeAuthCredentials = LoginMethod.AuthCredentials(
    username = "someEmail@mail.com", password = "password"
)

val fakeAppError: AppError = fakeRemoteError.toAppError()