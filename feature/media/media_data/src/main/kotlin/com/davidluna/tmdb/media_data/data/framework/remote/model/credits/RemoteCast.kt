package com.davidluna.tmdb.media_data.data.framework.remote.model.credits

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCast(
    @SerialName("character")
    val character: String?,
    @SerialName("id")
    val castId: Int?,
    @SerialName("name")
    val name: String?,
    @SerialName("profile_path")
    val profilePath: String?,
)