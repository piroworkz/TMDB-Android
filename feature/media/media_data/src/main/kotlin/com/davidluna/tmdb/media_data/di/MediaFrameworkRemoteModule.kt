package com.davidluna.tmdb.media_data.di

import com.davidluna.tmdb.media_data.framework.paging.MediaCatalogRemoteMediator
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_data.repositories.CatalogVideosRepository
import com.davidluna.tmdb.media_data.repositories.MediaCatalogRepository
import com.davidluna.tmdb.media_data.repositories.MediaDetailsCacheRepository
import com.davidluna.tmdb.media_domain.usecases.GetCatalogVideos
import com.davidluna.tmdb.media_domain.usecases.GetMediaDetails
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalog
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val mediaFrameworkRemoteModule = module {
    singleOf(::provideMediaCatalogService)
    factoryOf(::MediaCatalogRemoteMediator)
    factoryOf(::CatalogVideosRepository) bind GetCatalogVideos::class
    factoryOf(::MediaCatalogRepository) bind ObserveMediaCatalog::class
    factoryOf(::MediaDetailsCacheRepository) bind GetMediaDetails::class
    factory<MediaCatalogMediatorFactory> {
        val koin = getKoin()
        object : MediaCatalogMediatorFactory {
            override fun create(path: String, catalogName: String): MediaCatalogRemoteMediator =
                koin.get { parametersOf(path, catalogName) }
        }
    }
}

private fun provideMediaCatalogService(retrofit: Retrofit): RemoteMediaService =
    retrofit.create(RemoteMediaService::class.java)