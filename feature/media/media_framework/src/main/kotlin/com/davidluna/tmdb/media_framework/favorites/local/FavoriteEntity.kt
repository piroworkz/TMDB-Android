package com.davidluna.tmdb.media_framework.favorites.local

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["id", "mediaType"]
)
data class FavoriteEntity(
    val id: Int,
    val mediaType: String,
    val title: String,
    val posterPath: String,
    val timestamp: Long,
)
