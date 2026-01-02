package com.davidluna.tmdb.media_framework.favorites.mapper

import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import com.davidluna.tmdb.media_framework.favorites.local.FavoriteEntity

fun FavoriteEntity.toDomain(): FavoriteItem = FavoriteItem(
    id = id,
    mediaType = MediaType.valueOf(mediaType),
    title = title,
    posterPath = posterPath,
    timestamp = timestamp
)

fun FavoriteItem.toEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    mediaType = mediaType.name,
    title = title,
    posterPath = posterPath,
    timestamp = timestamp
)
