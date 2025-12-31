package com.davidluna.tmdb.auth_framework.data.sources

import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomUserAccount

fun RoomSession.toDomain(): Session = Session(
    sessionId = sessionId,
    isGuest = isGuest,
    expiresAt = expiresAt
)

fun RoomUserAccount.toDomain(): UserAccount = UserAccount(
    userId = userId,
    name = name,
    username = username,
    avatarPath = avatarPath
)