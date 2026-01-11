package com.davidluna.tmdb.media_data.fakes

import androidx.paging.PagingData
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.media_data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCast
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteGenre
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImage
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_data.framework.remote.model.videos.RemoteVideos
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.details.Cast
import com.davidluna.tmdb.media_domain.entities.details.Genre
import com.davidluna.tmdb.media_domain.entities.details.Image
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails
import com.davidluna.tmdb.media_domain.entities.details.Video
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale

val fakeCatalog = Catalog.MOVIE_POPULAR
val fakeMediaList = fakeRemoteMediaList.map { it.toDomain() }
val fakeVideos: List<Video> = fakeRemoteVideos.toDomain()
val fakeImagesList = fakeRemoteImages.posters.map {
    Image(
        filePath = it.filePath?.buildModel("w500").orEmpty(),
        mediaId = fakeRemoteImages.id,
    )
}

val fakeCast = fakeRemoteCredits.cast.map {
    Cast(
        castId = it.castId ?: -1,
        character = it.character.orEmpty(),
        name = it.name.orEmpty(),
        profilePath = it.profilePath?.buildModel().orEmpty()
    )
}


val fakeGenres = fakeRemoteMediaDetail.genres.map {
    Genre(
        id = it.id,
        name = it.name
    )
}

val fakeMediaDetails = fakeRemoteMediaDetail.toDomain()

val fakeEmptyPagingData: PagingData<Media> = PagingData.from(emptyList())

val fakeMediaPagingData: PagingData<Media> = PagingData.from(fakeMediaList)

val fakeAppError = AppError(
    code = AppErrorCode.NOT_FOUND,
    description = "some error"
)

private fun RemoteMediaDetail.toDomain(): MediaDetails = MediaDetails(
    id = id ?: 0,
    title = title.orEmpty(),
    releaseDate = formatDate(releaseDate).orEmpty(),
    runtime = runtime ?: 0,
    posterPath = posterPath?.buildModel().orEmpty(),
    backdropPath = backdropPath?.buildModel().orEmpty(),
    overview = overview.orEmpty(),
    tagline = tagline.orEmpty(),
    hasVideo = hasVideo,
    voteAverage = (voteAverage ?: 0.0).toFloat(),
    genres = fakeGenres,
    castList = fakeCast,
    images = fakeImagesList
)

private fun RemoteMedia.toDomain(): Media = Media(
    id = id ?: 0,
    posterPath = posterPath?.buildModel().orEmpty(),
    title = title.orEmpty()
)

private fun RemoteImages.toDomain(i: Int): List<Image> =
    posters.mapNotNull { image ->
        image.filePath?.takeIf { it.isNotEmpty() }?.let { image.toDomain(i) }
    }

private fun RemoteImage.toDomain(i: Int) =
    Image(
        filePath = filePath?.buildModel("w500").orEmpty(),
        mediaId = i
    )

private fun RemoteCast.toDomain(): Cast = Cast(
    castId = castId ?: 0,
    character = character.orEmpty(),
    name = name.orEmpty(),
    profilePath = profilePath?.buildModel().orEmpty()
)

private fun RemoteGenre.toDomain(): Genre = Genre(
    id = id,
    name = name
)

private fun formatDate(releaseDate: String?, countryCode: String = "US"): String? = try {
    releaseDate?.let {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
        val date = LocalDate.parse(it, inputFormatter)

        val locale = if (countryCode.equals("MX", ignoreCase = true)) {
            @Suppress("DEPRECATION")
            Locale("es", "MX")
        } else {
            Locale.US
        }

        val outputFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
        date.format(outputFormatter)
    }
} catch (_: DateTimeParseException) {
    null
}

private fun String.buildModel(width: String = "w185"): String =
    "https://image.tmdb.org/t/p/$width$this"

private fun RemoteVideos.toDomain(): List<Video> {
    return results.filter { it.site?.lowercase() == "youtube" && it.type?.lowercase() == "trailer" }
        .map {
            Video(
                id = it.id.orEmpty(),
                key = it.key.orEmpty()
            )
        }
}