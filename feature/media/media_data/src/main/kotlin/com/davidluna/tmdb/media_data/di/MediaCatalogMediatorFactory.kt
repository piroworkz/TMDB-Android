package com.davidluna.tmdb.media_data.di

import com.davidluna.tmdb.media_data.framework.paging.MediaCatalogRemoteMediator

interface MediaCatalogMediatorFactory {
    fun create(path: String, catalogName: String): MediaCatalogRemoteMediator
}