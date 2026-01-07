package com.davidluna.tmdb.media_data.framework.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteResults<T>(
    @SerialName("page")
    val page: Int?,
    @SerialName("results")
    val results: List<T> = emptyList(),
    @SerialName("total_pages")
    val totalPages: Int?,
    @SerialName("total_results")
    val totalResults: Int?
)
