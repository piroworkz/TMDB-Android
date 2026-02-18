package com.davidluna.tmdb.media_data.fakes

import com.davidluna.tmdb.media_data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomGenre
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomMediaDetailsRelations
import com.davidluna.tmdb.media_data.framework.local.database.entities.images.RoomImage
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomFavorite
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_domain.entities.Favorite

val fakeRoomMediaList = fakeRemoteMediaList.map {
    RoomMedia(
        category = fakeCatalog.name,
        id = it.id ?: 0,
        posterPath = it.posterPath.orEmpty(),
        title = it.title.orEmpty()
    )
}

val fakeRoomFavorites = fakeRoomMediaList.map {
    RoomFavorite(
        id = it.id,
        category = it.category
    )
}

val fakeFavorites = fakeRoomFavorites.map {
    Favorite(
        id = it.id,
        category = it.category
    )
}

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
