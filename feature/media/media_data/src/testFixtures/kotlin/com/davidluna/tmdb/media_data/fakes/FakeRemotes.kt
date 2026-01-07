package com.davidluna.tmdb.media_data.fakes

import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RemoteKeys
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteResults
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCredits
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCrew
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_data.framework.remote.model.videos.RemoteVideos
import com.davidluna.tmdb.test_shared.reader.Reader

val fakeRemoteMediaDetail: RemoteMediaDetail = Reader.fromJson<RemoteMediaDetail>(Reader.MOVIE_DETAIL)
val fakeRemoteImages: RemoteImages = Reader.fromJson<RemoteImages>(Reader.MOVIE_IMAGES)
val fakeRemoteCredits: RemoteCredits = Reader.fromJson<RemoteCredits>(Reader.MOVIE_CREDITS)
val fakeRemoteVideos: RemoteVideos = Reader.fromJson<RemoteVideos>(Reader.MOVIE_VIDEOS)
val fakeRemoteMediaList: List<RemoteMedia> = Reader.fromJson<RemoteResults<RemoteMedia>>(Reader.MOVIE_LIST).results
val fakeRemoteError = Reader.fromJson<RemoteError>(Reader.REMOTE_ERROR)

val fakeRemoteResults = RemoteResults(
    page = 1,
    results = fakeRemoteMediaList,
    totalPages = 1,
    totalResults = fakeRemoteMediaList.size
)

val fakeRemoteKey = RemoteKeys(
    lastPage = 1,
    category = fakeCatalog.name,
    reachedEndOfPagination = false,
    savedOnTimeMillis = System.currentTimeMillis()
)

val fakeRemoteCrew = (0..3).map {
    RemoteCrew(
        adult = false,
        creditId = "creditId $it",
        department = "department $it",
        gender = 0,
        id = it,
        job = "job $it",
        knownForDepartment = "knownForDepartment $it",
        name = "name $it",
        originalName = "originalName $it",
        popularity = 0.0,
        profilePath = "profilePath $it"
    )
}
