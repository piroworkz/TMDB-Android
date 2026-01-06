package com.davidluna.tmdb.auth_data.framework.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RemoteGravatar(
    val hash: String
)