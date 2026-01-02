package com.davidluna.tmdb.media_framework.data.local.database.entities.favorites

import androidx.room.Entity

@Entity(primaryKeys = ["id", "category"])
data class RoomFavorite(
    val category: String,
    val id: Int,
    val posterPath: String,
    val title: String,
)
