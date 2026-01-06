package com.davidluna.tmdb.auth_data.framework.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomSession(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val sessionId: String,
    val isGuest: Boolean,
    val expiresAt: String?
)