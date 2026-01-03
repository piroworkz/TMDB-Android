package com.davidluna.tmdb.media_framework.data.local.storage

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ObserveFavorites
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_framework.data.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalFavoritesDataSource @Inject constructor(
    private val favoritesDao: FavoritesDao,
) : ToggleFavorite, ObserveFavorites {
    override suspend fun invoke(item: FavoriteItem): Either<AppError, Boolean> = tryCatch {
        val favorite = item.toRoomFavorite()
        val storedFavorite = favoritesDao.getFavorite(favorite.id, favorite.category)
        if (storedFavorite == null) {
            favoritesDao.upsertFavorite(favorite)
            true
        } else {
            favoritesDao.deleteFavorite(favorite.id, favorite.category)
            false
        }
    }

    override val favorites: Flow<List<FavoriteItem>> =
        favoritesDao.observeFavorites().map { favorites ->
            favorites.map { it.toFavoriteItem() }
        }

    private fun FavoriteItem.toRoomFavorite() = RoomFavorite(
        category = mediaType.name,
        id = id,
        posterPath = posterPath,
        title = title,
    )

    private fun RoomFavorite.toFavoriteItem() = FavoriteItem(
        id = id,
        posterPath = posterPath,
        title = title,
        mediaType = MediaType.valueOf(category),
    )
}
