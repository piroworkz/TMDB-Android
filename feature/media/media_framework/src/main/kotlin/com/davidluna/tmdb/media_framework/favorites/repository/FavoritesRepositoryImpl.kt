package com.davidluna.tmdb.media_framework.favorites.repository

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.repository.FavoritesRepository
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import com.davidluna.tmdb.media_framework.favorites.local.FavoritesDao
import com.davidluna.tmdb.media_framework.favorites.mapper.toDomain
import com.davidluna.tmdb.media_framework.favorites.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FavoriteItem>> =
        favoritesDao.observeFavorites().map { favorites -> favorites.map { it.toDomain() } }

    override fun observeFavoritesByType(mediaType: MediaType): Flow<List<FavoriteItem>> =
        favoritesDao.observeFavoritesByType(mediaType.name)
            .map { favorites -> favorites.map { it.toDomain() } }

    override suspend fun addFavorite(item: FavoriteItem): Either<AppError, Unit> = tryCatch {
        favoritesDao.upsertFavorite(item.toEntity())
    }

    override suspend fun removeFavorite(id: Int, mediaType: MediaType): Either<AppError, Unit> =
        tryCatch {
            favoritesDao.deleteFavorite(id, mediaType.name)
        }

    override suspend fun toggleFavorite(item: FavoriteItem): Either<AppError, Unit> = tryCatch {
        val existing = favoritesDao.observeFavoritesByType(item.mediaType.name)
            .first()
            .any { it.id == item.id }

        if (existing) {
            favoritesDao.deleteFavorite(item.id, item.mediaType.name)
        } else {
            favoritesDao.upsertFavorite(item.toEntity())
        }
    }
}
