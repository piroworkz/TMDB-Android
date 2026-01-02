package com.davidluna.tmdb.media_domain.favorites.repository

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteItem>>

    fun observeFavoritesByType(mediaType: MediaType): Flow<List<FavoriteItem>>

    suspend fun addFavorite(item: FavoriteItem): Either<AppError, Unit>

    suspend fun removeFavorite(id: Int, mediaType: MediaType): Either<AppError, Unit>

    suspend fun toggleFavorite(item: FavoriteItem): Either<AppError, Unit>
}
