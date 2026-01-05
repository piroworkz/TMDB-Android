package com.davidluna.tmdb.media_framework.data.local.database.entities.media

import androidx.room.Entity

@Entity(primaryKeys = ["id", "category"])
data class RoomMedia(
    val category: String,
    val id: Int,
    val posterPath: String,
    val title: String,
    val mediaType: String = "",
    val isFavorite: Boolean = false,
)
