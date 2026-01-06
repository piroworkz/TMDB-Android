package com.davidluna.tmdb.auth_data.framework.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteLoginRequest(
    @SerialName("request_token")
    val requestToken: String
)