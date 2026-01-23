package com.davidluna.tmdb.media_domain.usecases

import androidx.paging.PagingData
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Media
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

fun interface ObserveMediaCatalog : (Catalog, CoroutineScope) -> Flow<PagingData<Media>>