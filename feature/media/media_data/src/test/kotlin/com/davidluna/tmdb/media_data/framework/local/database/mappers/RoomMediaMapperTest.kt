package com.davidluna.tmdb.media_data.framework.local.database.mappers

import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomMediaMapperTest {

    @Test
    fun `GIVEN room media WHEN mapped to domain THEN fields are preserved`() {
        val input = RoomMedia(
            category = "MOVIE_POPULAR",
            id = 10,
            posterPath = "/path.jpg",
            title = "Some title",
            isFavorite = true
        )
        val expected = Media(
            id = 10,
            posterPath = "/path.jpg",
            title = "Some title",
            isFavorite = true
        )

        val actual = input.toDomain()

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN media type movie WHEN mapped THEN movie prefix is returned`() {
        val actual = MediaType.MOVIE.toCategoryPrefix()

        assertEquals("MOVIE_%", actual)
    }

    @Test
    fun `GIVEN media type tv show WHEN mapped THEN tv prefix is returned`() {
        val actual = MediaType.TV_SHOW.toCategoryPrefix()

        assertEquals("TV_%", actual)
    }
}
