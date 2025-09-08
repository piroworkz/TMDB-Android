package com.davidluna.tmdb.auth_framework.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteLoginRequest(
    @SerialName("request_token")
    val requestToken: String
)