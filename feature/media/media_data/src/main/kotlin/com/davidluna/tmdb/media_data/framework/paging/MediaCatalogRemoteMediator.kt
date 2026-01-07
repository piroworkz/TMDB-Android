package com.davidluna.tmdb.media_data.framework.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import arrow.core.getOrElse
import com.davidluna.tmdb.core_data.framework.remote.model.buildModel
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.framework.local.database.dao.RemoteKeysDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RemoteKeys
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteResults
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MediaCatalogRemoteMediator @AssistedInject constructor(
    @Assisted("path") private val path: String,
    @Assisted("catalogName") private val catalogName: String,
    private val mediaDao: MediaDao,
    private val mediaService: RemoteMediaService,
    private val remoteKeysDao: RemoteKeysDao,
    private val isCacheExpired: IsCacheExpired,
) : RemoteMediator<Int, RoomMedia>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RoomMedia>,
    ): MediatorResult {
        return try {
            val currentCategory = catalogName
            val remoteKey = remoteKeysDao.getRemoteKey(currentCategory)
            val page = when (loadType) {
                PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                REFRESH -> 1
                APPEND -> {
                    if (remoteKey?.reachedEndOfPagination == true) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        (remoteKey?.lastPage ?: 1) + 1
                    }
                }
            }

            val remoteResponse: RemoteResults<RemoteMedia> =
                mediaService.getMediaCatalog(endpoint = path, page = page)
                    .getOrElse { return MediatorResult.Success(endOfPaginationReached = true) }

            val roomMediaList: List<RoomMedia> = remoteResponse.results.mapNotNull { media ->
                val hasPoster = !media.posterPath.isNullOrEmpty()
                val hasTitle = !media.title.isNullOrEmpty()
                if (hasPoster && hasTitle) {
                    media.toLocalStorage(currentCategory)
                } else {
                    null
                }
            }

            val endOfPaginationReached = page >= (remoteResponse.totalPages ?: 0)
            if (loadType == REFRESH) {
                remoteKeysDao.clearRemoteKey(currentCategory)
                mediaDao.deleteCatalog(currentCategory)
            }
            val key = remoteResponse.toKey(currentCategory, page)
            remoteKeysDao.insertKey(key)
            mediaDao.insertMedia(roomMediaList)

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: HttpException) {
            MediatorResult.Error(e.toAppError())
        } catch (e: IOException) {
            MediatorResult.Error(e.toAppError())
        } catch (e: Exception) {
            MediatorResult.Error(e.toAppError())
        }
    }

    override suspend fun initialize(): InitializeAction {
        val remoteKey = remoteKeysDao.getRemoteKey(catalogName)
        return if (isCacheExpired(remoteKey?.savedOnTimeMillis)) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    private fun RemoteResults<RemoteMedia>.toKey(category: String, page: Int): RemoteKeys =
        RemoteKeys(
            lastPage = page,
            category = category,
            reachedEndOfPagination = page == totalPages,
            savedOnTimeMillis = System.currentTimeMillis()
        )

    private fun RemoteMedia.toLocalStorage(category: String): RoomMedia = RoomMedia(
        id = id ?: 0,
        posterPath = posterPath?.buildModel().orEmpty(),
        title = title.orEmpty(),
        category = category
    )
}
