package com.davidluna.tmdb.media_domain.entities

data class Media(
    val id: Int,
    val posterPath: String,
    val title: String,
    val mediaType: MediaType,
    val isFavorite: Boolean = false
)
