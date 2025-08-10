package com.davidluna.tmdb.media_framework.data.remote.model.videos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteVideos(
    @SerialName("id")
    val mediaId: Int,
    @SerialName("results")
    val results: List<RemoteVideo>
)