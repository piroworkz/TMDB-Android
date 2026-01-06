package com.davidluna.tmdb.media_data.fakes

import androidx.paging.PagingData
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.media_data.data.framework.remote.model.RemoteMedia
import com.davidluna.tmdb.media_data.data.framework.remote.model.RemoteResults
import com.davidluna.tmdb.media_data.data.framework.remote.model.credits.RemoteCast
import com.davidluna.tmdb.media_data.data.framework.remote.model.credits.RemoteCredits
import com.davidluna.tmdb.media_data.data.framework.remote.model.details.RemoteGenre
import com.davidluna.tmdb.media_data.data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.data.framework.remote.model.images.RemoteImage
import com.davidluna.tmdb.media_data.data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_data.data.framework.remote.model.videos.RemoteVideos
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.details.Cast
import com.davidluna.tmdb.media_domain.entities.details.Genre
import com.davidluna.tmdb.media_domain.entities.details.Image
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails
import com.davidluna.tmdb.media_domain.entities.details.Video
import com.davidluna.tmdb.test_shared.reader.Reader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale

val fakeEmptyPagingData: PagingData<Media> = PagingData.from(emptyList())
val fakeMediaPagingData: PagingData<Media> = PagingData.from(buildFakeMediaList())

val fakeAppError = AppError(
    code = AppErrorCode.NOT_FOUND,
    description = "some error"
)

fun buildFakeMediaList(): List<Media> {
    return Reader.fromJson<RemoteResults<RemoteMedia>>(Reader.MOVIE_LIST).results.map {
        Media(
            id = it.id ?: 0,
            posterPath = it.posterPath?.buildModel().orEmpty(),
            title = it.title.orEmpty()
        )
    }
}

val fakeRemoteMediaDetail: RemoteMediaDetail =
    Reader.fromJson<RemoteMediaDetail>(Reader.MOVIE_DETAIL)
val fakeRemoteImages: RemoteImages = Reader.fromJson<RemoteImages>(Reader.MOVIE_IMAGES)
val fakeRemoteCredits: RemoteCredits = Reader.fromJson<RemoteCredits>(Reader.MOVIE_CREDITS)
val fakeVideos: List<Video> =
    Reader.fromJson<RemoteVideos>(Reader.MOVIE_VIDEOS).toDomain()

val fakeMediaDetails = MediaDetails(
    id = fakeRemoteMediaDetail.id ?: 0,
    title = fakeRemoteMediaDetail.title.orEmpty(),
    releaseDate = formatDate(fakeRemoteMediaDetail.releaseDate).orEmpty(),
    runtime = fakeRemoteMediaDetail.runtime ?: 0,
    posterPath = fakeRemoteMediaDetail.posterPath?.buildModel().orEmpty(),
    backdropPath = fakeRemoteMediaDetail.backdropPath?.buildModel().orEmpty(),
    overview = fakeRemoteMediaDetail.overview.orEmpty(),
    tagline = fakeRemoteMediaDetail.tagline.orEmpty(),
    hasVideo = fakeRemoteMediaDetail.hasVideo,
    voteAverage = (fakeRemoteMediaDetail.voteAverage ?: 0.0).toFloat(),
    genres = fakeRemoteMediaDetail.genres.map { it.toDomain() },
    castList = fakeRemoteCredits.cast.map {
        it.toDomain()
    },
    images = fakeRemoteImages.toDomain(fakeRemoteMediaDetail.id ?: 0)
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
    return results.filter { it.site?.lowercase() == "youtube" && it.type?.lowercase() == "trailer" }.map {
        Video(
            id = it.id.orEmpty(),
            key = it.key.orEmpty()
        )
    }
}