package com.davidluna.tmdb.media_data.repositories

import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatchSuspend
import com.davidluna.tmdb.media_data.framework.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomFavorite
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Favorite
import com.davidluna.tmdb.media_domain.usecases.ClearFavorites
import com.davidluna.tmdb.media_domain.usecases.ObserveFavoriteMedia
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val favoritesDao: FavoritesDao
) : ObserveFavoriteMedia, ToggleFavorite, ClearFavorites {

    override fun observe(catalog: Catalog): Flow<List<Favorite>> {
        return favoritesDao.getFavorites(catalog.name).map { favorites: List<RoomFavorite> ->
            favorites.map { it.toDomain() }
        }
    }

    override suspend fun toggle(mediaId: Int, catalog: Catalog): AppError? = tryCatchSuspend {
        favoritesDao.toggleFavorite(mediaId, catalog.name)
    }.leftOrNull()

    override suspend fun clear(): AppError? = tryCatchSuspend {
        favoritesDao.clearFavorites()
        null
    }.leftOrNull()

    private fun RoomFavorite.toDomain(): Favorite = Favorite(
        id = id,
        category = category
    )
}
