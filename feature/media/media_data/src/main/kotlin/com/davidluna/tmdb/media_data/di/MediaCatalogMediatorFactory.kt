package com.davidluna.tmdb.media_data.di

import com.davidluna.tmdb.media_data.data.framework.paging.MediaCatalogRemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

@AssistedFactory
interface MediaCatalogMediatorFactory {
    fun create(@Assisted("path")path: String, @Assisted("catalogName")catalogName: String): MediaCatalogRemoteMediator
}