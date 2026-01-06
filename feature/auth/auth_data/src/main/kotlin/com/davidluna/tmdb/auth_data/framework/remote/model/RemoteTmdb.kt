package com.davidluna.tmdb.auth_data.framework.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteTmdb(
    @SerialName("avatar_path")
    val avatarPath: String
)