package com.davidluna.tmdb.media_data.framework.local.database.mappers

import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType

fun RoomMedia.toDomain(): Media = Media(
    id = id,
    posterPath = posterPath,
    title = title,
    isFavorite = isFavorite
)

fun MediaType.toCategoryPrefix(): String = when (this) {
    MediaType.MOVIE -> "MOVIE_%"
    MediaType.TV_SHOW -> "TV_%"
}
