package com.davidluna.tmdb.media_framework.data.repositories

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.media_framework.data.remote.model.toEndpointPath
import com.davidluna.tmdb.media_domain.entities.details.Video
import com.davidluna.tmdb.media_domain.usecases.GetMediaVideosUseCase
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaVideosDao
import com.davidluna.tmdb.media_framework.data.local.database.entities.videos.RoomVideo
import com.davidluna.tmdb.media_framework.data.paging.IsCacheExpired
import com.davidluna.tmdb.media_framework.data.remote.model.videos.RemoteVideo
import com.davidluna.tmdb.media_framework.data.remote.services.RemoteMediaService
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class MediaVideosRepository @Inject constructor(
    private val local: MediaVideosDao,
    private val remote: RemoteMediaService,
    private val isCacheExpired: IsCacheExpired,
) : GetMediaVideosUseCase {

    override suspend operator fun invoke(
        catalog: Catalog,
        mediaId: Int,
    ): Either<AppError, List<Video>> = tryCatch {
        val localVideos = local.getVideo(mediaId)
        val isCacheExpired = isCacheExpired(localVideos.firstOrNull()?.savedTimeMillis)
        if (localVideos.isNotEmpty() && !isCacheExpired) {
            localVideos.map { it.toDomain() }
        } else {
            val endpoint = catalog.toEndpointPath(mediaId)
            val remoteVideos = fetchRemoteVideos(endpoint, mediaId)
            local.cacheVideos(remoteVideos, isCacheExpired).map { it.toDomain() }
        }
    }

    private suspend fun fetchRemoteVideos(endpoint: String, mediaId: Int): List<RoomVideo> =
        coroutineScope {
            remote.getVideos(endpoint).fold(
                ifLeft = { emptyList() },
                ifRight = { response ->
                    response.results.mapNotNull {
                        if (it.site?.lowercase() == "youtube" && it.type?.lowercase() == "trailer") {
                            it.toLocalStorage(mediaId)
                        } else {
                            null
                        }
                    }
                }
            )
        }

    private fun RemoteVideo.toLocalStorage(mediaId: Int): RoomVideo = RoomVideo(
        id = id.orEmpty(),
        key = key.orEmpty(),
        mediaId = mediaId,
        savedTimeMillis = System.currentTimeMillis()
    )

    private fun RoomVideo.toDomain(): Video = Video(
        id = id,
        key = key,
    )
}
