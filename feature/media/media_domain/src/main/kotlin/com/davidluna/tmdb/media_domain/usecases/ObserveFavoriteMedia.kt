package com.davidluna.tmdb.media_domain.usecases

import androidx.paging.PagingData
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ObserveFavoriteMedia {
    fun observe(mediaType: MediaType, scope: CoroutineScope): Flow<PagingData<Media>>
}
