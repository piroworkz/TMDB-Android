package com.davidluna.tmdb.media_framework.data.local.storage

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.media_framework.data.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite
import javax.inject.Inject

class LocalFavoritesDataSource @Inject constructor(
    private val favoritesDao: FavoritesDao,
) {
    suspend fun toggleFavorite(favorite: RoomFavorite): Either<AppError, Boolean> = tryCatch {
        val storedFavorite = favoritesDao.getFavorite(favorite.id, favorite.category)
        if (storedFavorite == null) {
            favoritesDao.upsertFavorite(favorite)
            true
        } else {
            favoritesDao.deleteFavorite(favorite.id, favorite.category)
            false
        }
    }
}
