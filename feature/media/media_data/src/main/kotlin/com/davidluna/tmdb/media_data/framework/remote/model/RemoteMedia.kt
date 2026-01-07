package com.davidluna.tmdb.media_data.framework.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMedia(
    @SerialName("id")
    val id: Int?,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("title")
    val titleMovie: String? = null,
    @SerialName("name")
    val titleShow: String? = null,
) {
    val title: String?
        get() = titleMovie ?: titleShow
}