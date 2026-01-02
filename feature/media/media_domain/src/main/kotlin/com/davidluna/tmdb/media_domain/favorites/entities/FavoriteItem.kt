package com.davidluna.tmdb.media_domain.favorites.entities

import com.davidluna.tmdb.media_domain.favorites.types.MediaType

data class FavoriteItem(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val posterPath: String,
    val timestamp: Long
)
