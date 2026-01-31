package com.davidluna.tmdb.media_data.data.remote.services

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteResults
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCredits
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_data.framework.remote.model.videos.RemoteVideos
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.reader.Reader.fromJson

class RemoteMediaServiceSpy : RemoteMediaService {

    private var shouldThrowError: Boolean = false

    fun throwError(shouldThrow: Boolean) {
        shouldThrowError = shouldThrow
    }

    override suspend fun getMediaCatalog(
        endpoint: String,
        page: Int,
    ): Either<RemoteError, RemoteResults<RemoteMedia>> = if (shouldThrowError) {
        fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
    } else {
        fromJson<RemoteResults<RemoteMedia>>(Reader.MOVIE_LIST).right()
    }

    override suspend fun getDetailById(endpoint: String): Either<RemoteError, RemoteMediaDetail> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteMediaDetail>(Reader.MOVIE_DETAIL).right()
        }

    override suspend fun getCreditsById(endpoint: String): Either<RemoteError, RemoteCredits> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteCredits>(Reader.MOVIE_CREDITS).right()
        }

    override suspend fun getImagesById(endpoint: String): Either<RemoteError, RemoteImages> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteImages>(Reader.MOVIE_IMAGES).right()
        }

    override suspend fun getVideos(endpoint: String): Either<RemoteError, RemoteVideos> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteVideos>(Reader.MOVIE_VIDEOS).right()
        }
}