package com.davidluna.tmdb.media_data.data.framework.remote.model.credits

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCredits(
    @SerialName("cast")
    val cast: List<RemoteCast> = emptyList(),
    @SerialName("id")
    val id: Int?,
    @SerialName("crew")
    val crew: List<RemoteCrew> = emptyList(),
)