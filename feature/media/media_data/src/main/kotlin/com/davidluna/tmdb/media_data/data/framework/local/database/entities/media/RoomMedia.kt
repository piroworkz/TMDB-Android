package com.davidluna.tmdb.media_data.data.framework.local.database.entities.media

import androidx.room.Entity

@Entity(primaryKeys = ["id", "category"])
data class RoomMedia(
    val category: String,
    val id: Int,
    val posterPath: String,
    val title: String,
)