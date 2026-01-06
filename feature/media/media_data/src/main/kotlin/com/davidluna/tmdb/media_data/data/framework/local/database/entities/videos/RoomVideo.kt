package com.davidluna.tmdb.media_data.data.framework.local.database.entities.videos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomVideo(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val key: String,
    val mediaId: Int,
    val savedTimeMillis: Long
)