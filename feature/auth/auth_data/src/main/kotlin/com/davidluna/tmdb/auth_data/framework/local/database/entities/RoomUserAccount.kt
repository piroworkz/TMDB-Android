package com.davidluna.tmdb.auth_data.framework.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomUserAccount(
    @PrimaryKey(autoGenerate = false)
    val userId: Int,
    val name: String,
    val username: String,
    val avatarPath: String
)