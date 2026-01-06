package com.davidluna.tmdb.auth_data.framework.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUserAccountDetail(
    @SerialName("avatar")
    val avatar: RemoteAvatar,
    @SerialName("id")
    val userId: Int,
    @SerialName("include_adult")
    val includeAdult: Boolean,
    @SerialName("iso_3166_1")
    val iso_3166_1: String,
    @SerialName("iso_639_1")
    val iso_639_1: String,
    @SerialName("name")
    val name: String,
    @SerialName("username")
    val username: String
)