package com.davidluna.tmdb.media_domain.entities

data class FavoriteItem(
    val id: Int,
    val posterPath: String,
    val title: String,
    val mediaType: MediaType,
)
