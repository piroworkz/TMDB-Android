package com.davidluna.tmdb.media_domain.entities

enum class Catalog {
    MOVIE_DETAIL,
    MOVIE_NOW_PLAYING,
    MOVIE_POPULAR,
    MOVIE_RECOMMENDATIONS,
    MOVIE_SIMILAR,
    MOVIE_TOP_RATED,
    MOVIE_UPCOMING,
    TV_AIRING_TODAY,
    TV_DETAIL,
    TV_ON_THE_AIR,
    TV_POPULAR,
    TV_RECOMMENDATIONS,
    TV_SIMILAR,
    TV_TOP_RATED;

    val mediaType: MediaType
        get() = when (this) {
            MOVIE_DETAIL,
            MOVIE_NOW_PLAYING,
            MOVIE_POPULAR,
            MOVIE_RECOMMENDATIONS,
            MOVIE_SIMILAR,
            MOVIE_TOP_RATED,
            MOVIE_UPCOMING -> MediaType.MOVIE
            TV_AIRING_TODAY,
            TV_DETAIL,
            TV_ON_THE_AIR,
            TV_POPULAR,
            TV_RECOMMENDATIONS,
            TV_SIMILAR,
            TV_TOP_RATED -> MediaType.TV_SHOW
        }
}
