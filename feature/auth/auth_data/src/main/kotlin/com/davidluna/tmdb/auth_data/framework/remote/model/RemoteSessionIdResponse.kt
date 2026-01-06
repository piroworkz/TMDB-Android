package com.davidluna.tmdb.auth_data.framework.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteSessionIdResponse(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("success")
    val success: Boolean
)