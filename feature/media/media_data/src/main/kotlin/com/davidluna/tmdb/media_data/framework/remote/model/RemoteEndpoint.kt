package com.davidluna.tmdb.media_data.framework.remote.model

import com.davidluna.tmdb.media_domain.entities.Catalog

fun Catalog.toEndpointPath(forId: Int? = null): String = when (this) {
    Catalog.MOVIE_DETAIL -> "movie/$forId"
    Catalog.TV_DETAIL -> "tv/$forId"
    Catalog.MOVIE_NOW_PLAYING -> "movie/now_playing"
    Catalog.MOVIE_POPULAR -> "movie/popular"
    Catalog.MOVIE_RECOMMENDATIONS -> "movie/$forId/recommendations"
    Catalog.MOVIE_SIMILAR -> "movie/$forId/similar"
    Catalog.MOVIE_TOP_RATED -> "movie/top_rated"
    Catalog.MOVIE_UPCOMING -> "movie/upcoming"
    Catalog.TV_AIRING_TODAY -> "tv/airing_today"
    Catalog.TV_ON_THE_AIR -> "tv/on_the_air"
    Catalog.TV_POPULAR -> "tv/popular"
    Catalog.TV_RECOMMENDATIONS -> "tv/$forId/recommendations"
    Catalog.TV_SIMILAR -> "tv/$forId/similar"
    Catalog.TV_TOP_RATED -> "tv/top_rated"
}