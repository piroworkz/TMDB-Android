package com.davidluna.tmdb.media_data.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.davidluna.tmdb.media_data.data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.data.framework.remote.model.toEndpointPath
import com.davidluna.tmdb.media_data.di.MediaCatalogMediatorFactory
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalogUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaCatalogRepository @Inject constructor(
    private val mediaDao: MediaDao,
    private val mediatorFactory: MediaCatalogMediatorFactory,
) : ObserveMediaCatalogUseCase {

    @OptIn(ExperimentalPagingApi::class)
    override operator fun invoke(
        catalog: Catalog,
        scope: CoroutineScope,
    ): Flow<PagingData<Media>> {
        val path = catalog.toEndpointPath()
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = mediatorFactory.create(path, catalog.name),
            pagingSourceFactory = { mediaDao.getMedia(catalog.name) }
        ).flow.cachedIn(scope).map { pagingData -> pagingData.map { it.toDomain() } }
    }

    private fun RoomMedia.toDomain(): Media = Media(
        id = id,
        posterPath = posterPath,
        title = title
    )
}