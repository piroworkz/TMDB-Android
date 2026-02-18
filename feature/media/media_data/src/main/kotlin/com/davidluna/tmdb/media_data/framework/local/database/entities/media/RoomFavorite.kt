package com.davidluna.tmdb.media_data.framework.local.database.entities.media

import androidx.room.Entity

@Entity(primaryKeys = ["id", "category"])
data class RoomFavorite(
    val id: Int,
    val category: String
)