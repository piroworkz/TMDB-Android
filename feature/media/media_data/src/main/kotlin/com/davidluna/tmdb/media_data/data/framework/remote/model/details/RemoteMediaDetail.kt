package com.davidluna.tmdb.media_data.data.framework.remote.model.details

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMediaDetail(
    @SerialName("id")
    val id: Int?,
    @SerialName("title")
    val titleMovie: String? = null,
    @SerialName("name")
    val titleShow: String? = null,
    @SerialName("release_date")
    val releaseDateMovie: String? = null,
    @SerialName("first_air_date")
    val releaseDateShow: String? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("video")
    val hasVideo: Boolean = false,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("genres")
    val genres: List<RemoteGenre> = emptyList(),
) {
    val title: String?
        get() = titleMovie ?: titleShow

    val releaseDate: String?
        get() = releaseDateMovie ?: releaseDateShow
}

