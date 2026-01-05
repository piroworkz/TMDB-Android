package com.davidluna.tmdb.media_domain.entities

import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogTest {

    @Test
    fun `movie catalogs map to movie media type`() {
        val movieCatalogs = listOf(
            Catalog.MOVIE_DETAIL,
            Catalog.MOVIE_NOW_PLAYING,
            Catalog.MOVIE_POPULAR,
            Catalog.MOVIE_RECOMMENDATIONS,
            Catalog.MOVIE_SIMILAR,
            Catalog.MOVIE_TOP_RATED,
            Catalog.MOVIE_UPCOMING
        )

        movieCatalogs.forEach { catalog ->
            assertEquals(MediaType.MOVIE, catalog.mediaType)
        }
    }

    @Test
    fun `tv catalogs map to tv show media type`() {
        val tvCatalogs = listOf(
            Catalog.TV_AIRING_TODAY,
            Catalog.TV_DETAIL,
            Catalog.TV_ON_THE_AIR,
            Catalog.TV_POPULAR,
            Catalog.TV_RECOMMENDATIONS,
            Catalog.TV_SIMILAR,
            Catalog.TV_TOP_RATED
        )

        tvCatalogs.forEach { catalog ->
            assertEquals(MediaType.TV_SHOW, catalog.mediaType)
        }
    }
}
