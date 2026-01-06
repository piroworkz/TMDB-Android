package com.davidluna.tmdb.media_data.data.fakes

import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomGenre
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetailsRelations
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.images.RoomImage
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.media.RemoteKeys
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.data.framework.remote.model.RemoteResults
import com.davidluna.tmdb.media_data.data.framework.remote.model.credits.RemoteCast
import com.davidluna.tmdb.media_data.data.framework.remote.model.credits.RemoteCredits
import com.davidluna.tmdb.media_data.data.framework.remote.model.credits.RemoteCrew
import com.davidluna.tmdb.media_data.data.framework.remote.model.details.RemoteGenre
import com.davidluna.tmdb.media_data.data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.data.framework.remote.model.images.RemoteImage
import com.davidluna.tmdb.media_data.data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.details.Cast
import com.davidluna.tmdb.media_domain.entities.details.Genre
import com.davidluna.tmdb.media_domain.entities.details.Image
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails

val fakeCatalog = Catalog.MOVIE_POPULAR

val fakeRemoteMediaList = listOf(
    RemoteMedia(id = 1, posterPath = "/a.jpg", titleMovie = "Test 1"),
    RemoteMedia(id = 2, posterPath = "/b.jpg", titleShow = "Test 2"),
)

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

val fakeRoomMediaList = fakeRemoteMediaList.map {
    RoomMedia(
        category = fakeCatalog.name,
        id = it.id ?: 0,
        posterPath = it.posterPath.orEmpty(),
        title = it.title.orEmpty()
    )
}

val fakeImagesList = (0..3).map {
    Image(
        filePath = "filePath $it",
        mediaId = it,
    )
}

val fakeCast = (0..3).map {
    Cast(
        character = "character $it",
        castId = it,
        name = "name $it",
        profilePath = "profilePath $it"
    )
}

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

val fakeRemoteImages = fakeImagesList.map {
    RemoteImage(
        aspectRatio = 0.0,
        filePath = it.filePath,
        height = 0,
        iso6391 = null,
        voteAverage = 0.0,
        voteCount = 0,
        width = 0
    )
}

val fakeRemoteImageResponse = RemoteImages(
    backdrops = fakeRemoteImages,
    id = 0,
    logos = fakeRemoteImages,
    posters = fakeRemoteImages
)

val fakeGenres = listOf(
    Genre(
        id = 1,
        name = "genre 1"
    )
)

val fakeRemoteCredits = RemoteCredits(
    cast = fakeCast.map {
        RemoteCast(
            character = it.character,
            castId = it.castId,
            name = it.name,
            profilePath = it.profilePath
        )
    },
    crew = fakeRemoteCrew,
    id = 0
)

val fakeMediaDetails = MediaDetails(
    id = 2636,
    title = "falli",
    releaseDate = "consetetur",
    runtime = 6201,
    posterPath = "tantas",
    backdropPath = "viderer",
    overview = "signiferumque",
    tagline = "ultricies",
    voteAverage = 10.11F,
    genres = fakeGenres,
    castList = fakeCast,
    images = fakeImagesList,
    hasVideo = true
)

val fakeRemoteMediaDetail = RemoteMediaDetail(
    id = 2317,
    titleMovie = "agam",
    releaseDateMovie = "ullamcorper",
    runtime = 7188,
    posterPath = "usu",
    backdropPath = "fuisset",
    overview = "vocibus",
    tagline = "viderer",
    voteAverage = 2.3,
    genres = fakeGenres.map {
        RemoteGenre(
            id = it.id,
            name = it.name
        )
    }
)

val fakeRoomMediaDetails: RoomMediaDetails = with(fakeMediaDetails) {
    RoomMediaDetails(
        id = id,
        title = title,
        releaseDate = releaseDate,
        runtime = runtime,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        tagline = tagline,
        voteAverage = voteAverage.toDouble(),
        genres = genres.map { RoomGenre(id = it.id, name = it.name) },
        hasVideo = hasVideo,
        savedOnTimeMillis = System.currentTimeMillis()
    )
}

val fakeRoomImages = fakeImagesList.map {
    RoomImage(
        filePath = it.filePath,
        mediaId = it.mediaId
    )
}

val fakeRoomCast = fakeCast.map {
    RoomCast(
        character = it.character,
        name = it.name,
        profilePath = it.profilePath,
        castId = it.castId,
        mediaId = it.castId
    )
}

val fakeRoomMediaDetailsRelations = RoomMediaDetailsRelations(
    details = fakeRoomMediaDetails, images = fakeRoomImages, cast = fakeRoomCast
)

val fakeRemoteError = RemoteError(statusCode = 500, statusMessage = "maluisset", success = false)