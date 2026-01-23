package com.davidluna.tmdb.media_data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatchSuspend
import com.davidluna.tmdb.media_data.framework.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_data.framework.local.database.mappers.toCategoryPrefix
import com.davidluna.tmdb.media_data.framework.local.database.mappers.toDomain
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ClearFavorites
import com.davidluna.tmdb.media_domain.usecases.ObserveFavoriteMedia
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val favoritesDao: FavoritesDao
) : ObserveFavoriteMedia, ToggleFavorite, ClearFavorites {

    override fun observe(mediaType: MediaType, scope: CoroutineScope): Flow<PagingData<Media>> {
        val categoryPrefix = mediaType.toCategoryPrefix()
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { favoritesDao.getFavorites(categoryPrefix) }
        ).flow.cachedIn(scope).map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun toggle(mediaId: Int, mediaType: MediaType): AppError? =
        tryCatchSuspend {
            favoritesDao.toggleFavorite(mediaId, mediaType.toCategoryPrefix())
            null
        }.leftOrNull()

    override suspend fun clear(): AppError? = tryCatchSuspend {
        favoritesDao.clearFavorites()
        null
    }.leftOrNull()
}
