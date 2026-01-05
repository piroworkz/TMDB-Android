package com.davidluna.tmdb.media_domain.entities

enum class Catalog(val mediaType: MediaType) {
    MOVIE_DETAIL(MediaType.MOVIE),
    MOVIE_NOW_PLAYING(MediaType.MOVIE),
    MOVIE_POPULAR(MediaType.MOVIE),
    MOVIE_RECOMMENDATIONS(MediaType.MOVIE),
    MOVIE_SIMILAR(MediaType.MOVIE),
    MOVIE_TOP_RATED(MediaType.MOVIE),
    MOVIE_UPCOMING(MediaType.MOVIE),
    TV_AIRING_TODAY(MediaType.TV_SHOW),
    TV_DETAIL(MediaType.TV_SHOW),
    TV_ON_THE_AIR(MediaType.TV_SHOW),
    TV_POPULAR(MediaType.TV_SHOW),
    TV_RECOMMENDATIONS(MediaType.TV_SHOW),
    TV_SIMILAR(MediaType.TV_SHOW),
    TV_TOP_RATED(MediaType.TV_SHOW)
}
