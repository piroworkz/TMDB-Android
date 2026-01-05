package com.davidluna.tmdb.media_domain.usecases

import androidx.paging.PagingData
import com.davidluna.tmdb.media_domain.entities.Media
import kotlinx.coroutines.flow.Flow

interface ObserveFavorites {
    val favorites: Flow<PagingData<Media>>
}
