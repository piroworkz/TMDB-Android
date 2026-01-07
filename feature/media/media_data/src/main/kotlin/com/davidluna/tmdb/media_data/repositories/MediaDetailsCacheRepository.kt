package com.davidluna.tmdb.media_data.repositories

import arrow.core.Either
import com.davidluna.tmdb.core_data.framework.remote.model.buildModel
import com.davidluna.tmdb.core_data.framework.remote.model.toAppError
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.core_domain.entities.tryCatchSuspend
import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDetailsDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomGenre
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomMediaDetailsRelations
import com.davidluna.tmdb.media_data.framework.local.database.entities.images.RoomImage
import com.davidluna.tmdb.media_data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCast
import com.davidluna.tmdb.media_data.framework.remote.model.credits.RemoteCredits
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteGenre
import com.davidluna.tmdb.media_data.framework.remote.model.details.RemoteMediaDetail
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImage
import com.davidluna.tmdb.media_data.framework.remote.model.images.RemoteImages
import com.davidluna.tmdb.media_data.framework.remote.model.toEndpointPath
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.details.Cast
import com.davidluna.tmdb.media_domain.entities.details.Genre
import com.davidluna.tmdb.media_domain.entities.details.Image
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails
import com.davidluna.tmdb.media_domain.usecases.GetMediaDetails
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

class MediaDetailsCacheRepository @Inject constructor(
    private val local: MediaDetailsDao,
    private val remote: RemoteMediaService,
    private val isCacheExpired: IsCacheExpired,
    private val observeCountryCode: ObserveCountryCode,
) : GetMediaDetails {

    override suspend fun invoke(catalog: Catalog, mediaId: Int): Either<AppError, MediaDetails> =
        tryCatchSuspend {
            val endpoint = catalog.toEndpointPath(forId = mediaId)
            val localDetails: RoomMediaDetailsRelations? = local.getFullDetail(mediaId)
            val isCacheExpired = isCacheExpired(localDetails?.details?.savedOnTimeMillis)
            val countryCode = observeCountryCode().first()
            if (localDetails != null && !isCacheExpired) {
                localDetails.toDomain()
            } else {
                val remoteDetails: RoomMediaDetailsRelations = fetchDetails(endpoint, countryCode)

                local.cacheDetails(remoteDetails, isCacheExpired)?.toDomain() ?: throw appError()
            }
        }

    private suspend fun fetchDetails(
        endPoint: String,
        countryCode: String,
    ): RoomMediaDetailsRelations =
        coroutineScope {
            val detailsDeferred: Deferred<RemoteMediaDetail> = async {
                remote.getDetailById(endPoint)
                    .fold(ifLeft = { throw it.toAppError() }, ifRight = { it })
            }

            val creditsDeferred: Deferred<RemoteCredits> = async {
                remote.getCreditsById(endPoint)
                    .fold(ifLeft = { throw it.toAppError() }, ifRight = { it })
            }

            val imagesDeferred: Deferred<RemoteImages> = async {
                remote.getImagesById(endPoint)
                    .fold(ifLeft = { throw it.toAppError() }, ifRight = { it })
            }

            val details = detailsDeferred.await()
            val credits = creditsDeferred.await()
            val images = imagesDeferred.await()

            toLocalStorage(details, credits, images, countryCode)
        }

    private fun toLocalStorage(
        details: RemoteMediaDetail,
        credits: RemoteCredits,
        images: RemoteImages,
        countryCode: String,
    ) = RoomMediaDetailsRelations(
        details = details.toLocalStorage(countryCode = countryCode),
        cast = credits.cast.map { it.toLocalStorage(details.id ?: 0) },
        images = images.toLocalStorage(details.id ?: 0)
    )


    private fun appError() = AppError(
        code = AppErrorCode.LOCAL_ERROR,
        description = "Details not found in local database"
    )

    private fun RoomMediaDetailsRelations.toDomain(): MediaDetails = MediaDetails(
        id = details.id,
        title = details.title,
        releaseDate = details.releaseDate,
        runtime = details.runtime,
        posterPath = details.posterPath,
        backdropPath = details.backdropPath,
        overview = details.overview,
        tagline = details.tagline,
        hasVideo = details.hasVideo,
        voteAverage = details.voteAverage.toFloat(),
        genres = details.genres.map { it.toDomain() },
        castList = cast.map { it.toDomain() },
        images = images.map { it.toDomain() },
    )

    private fun RoomGenre.toDomain(): Genre = Genre(
        id = id,
        name = name
    )

    private fun RoomCast.toDomain(): Cast = Cast(
        character = character,
        name = name,
        profilePath = profilePath,
        castId = castId
    )

    private fun RoomImage.toDomain(): Image = Image(
        filePath = filePath,
        mediaId = mediaId
    )

    private fun RemoteMediaDetail.toLocalStorage(countryCode: String): RoomMediaDetails =
        RoomMediaDetails(
            id = id ?: 0,
            title = title.orEmpty(),
            releaseDate = formatDate(releaseDate, countryCode).orEmpty(),
            runtime = runtime ?: 0,
            posterPath = posterPath?.buildModel().orEmpty(),
            backdropPath = backdropPath?.buildModel().orEmpty(),
            overview = overview.orEmpty(),
            tagline = tagline.orEmpty(),
            voteAverage = voteAverage ?: 0.0,
            hasVideo = hasVideo,
            genres = genres.map { it.toLocalStorage() },
            savedOnTimeMillis = System.currentTimeMillis()
        )

    private fun RemoteGenre.toLocalStorage(): RoomGenre {
        return RoomGenre(
            id = id,
            name = name
        )
    }

    private fun RemoteCast.toLocalStorage(mediaId: Int): RoomCast = RoomCast(
        character = character.orEmpty(),
        name = name.orEmpty(),
        profilePath = profilePath?.buildModel().orEmpty(),
        castId = castId ?: 0,
        mediaId = mediaId
    )

    private fun RemoteImages.toLocalStorage(mediaId: Int): List<RoomImage> =
        posters.mapNotNull { image ->
            image.filePath?.takeIf { it.isNotEmpty() }?.let { image.toLocalStorage(mediaId) }
        }

    private fun RemoteImage.toLocalStorage(i: Int): RoomImage = RoomImage(
        filePath = filePath?.buildModel(width = "w500").orEmpty(),
        mediaId = i
    )

    fun formatDate(releaseDate: String?, countryCode: String): String? = try {
        releaseDate?.let {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)
            val date = LocalDate.parse(it, inputFormatter)

            val locale = if (countryCode.equals("MX", ignoreCase = true)) {
                @Suppress("DEPRECATION")
                Locale("es", "MX")
            } else {
                Locale.US
            }

            val outputFormatter =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)
            date.format(outputFormatter)
        }
    } catch (_: DateTimeParseException) {
        null
    }
}