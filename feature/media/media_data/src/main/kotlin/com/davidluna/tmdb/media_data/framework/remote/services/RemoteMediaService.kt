package com.davidluna.tmdb.media_data.framework.remote.services

import arrow.core.Either
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteResults
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCredits
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_data.framework.remote.model.videos.RemoteVideos
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteMediaService {
    @GET("{endpoint}")
    suspend fun getMediaCatalog(
        @Path("endpoint", encoded = true) endpoint: String,
        @Query("page") page: Int,
    ): Either<RemoteError, RemoteResults<RemoteMedia>>

    @GET("{endpoint}")
    suspend fun getDetailById(
        @Path("endpoint", encoded = true) endpoint: String,
    ): Either<RemoteError, RemoteMediaDetail>

    @GET("{endpoint}/credits")
    suspend fun getCreditsById(
        @Path("endpoint", encoded = true) endpoint: String,
    ): Either<RemoteError, RemoteCredits>

    @GET("{endpoint}/images")
    suspend fun getImagesById(
        @Path("endpoint", encoded = true) endpoint: String,
    ): Either<RemoteError, RemoteImages>

    @GET("{endpoint}/videos")
    suspend fun getVideos(
        @Path("endpoint", encoded = true) endpoint: String,
    ): Either<RemoteError, RemoteVideos>
}