package com.davidluna.tmdb.media_data.data.framework.remote.model.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteImages(
    @SerialName("backdrops")
    val backdrops: List<RemoteImage>,
    @SerialName("id")
    val id: Int,
    @SerialName("logos")
    val logos: List<RemoteImage>,
    @SerialName("posters")
    val posters: List<RemoteImage>
)