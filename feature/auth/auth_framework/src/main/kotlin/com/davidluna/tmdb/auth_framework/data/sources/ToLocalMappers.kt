package com.davidluna.tmdb.auth_framework.data.sources

import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteGuestSession
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteSessionIdResponse
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteUserAccountDetail
import com.davidluna.tmdb.core_framework.data.remote.model.buildModel

fun RemoteGuestSession.toLocalStorage(): RoomSession = RoomSession(
    sessionId = guestSessionId,
    isGuest = success,
    expiresAt = expiresAt
)

fun RemoteSessionIdResponse.toLocalStorage(): RoomSession = RoomSession(
    sessionId = sessionId,
    isGuest = false,
    expiresAt = null
)

fun RemoteUserAccountDetail.toLocalStorage(): RoomUserAccount = RoomUserAccount(
    userId = userId,
    name = name,
    username = username,
    avatarPath = avatar.tmdb.avatarPath.buildModel()
)