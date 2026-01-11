package com.davidluna.tmdb.media_data.di

import com.davidluna.tmdb.media_data.framework.paging.MediaCatalogRemoteMediator
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_data.repositories.CatalogVideosRepository
import com.davidluna.tmdb.media_data.repositories.MediaCatalogRepository
import com.davidluna.tmdb.media_data.repositories.MediaDetailsCacheRepository
import com.davidluna.tmdb.media_domain.usecases.GetCatalogVideos
import com.davidluna.tmdb.media_domain.usecases.GetMediaDetails
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalogUseCase
import org.koin.core.Koin
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val mediaFrameworkRemoteModule = module {
    singleOf(::MediaCatalogRemoteMediator)
    singleOf(::provideMediaCatalogService)
    singleOf(::CatalogVideosRepository) bind GetCatalogVideos::class
    singleOf(::MediaCatalogRepository) bind ObserveMediaCatalogUseCase::class
    singleOf(::MediaDetailsCacheRepository) bind GetMediaDetails::class
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

private fun mediaCatalogMediatorFactory(koin: Koin): MediaCatalogMediatorFactory =
    object : MediaCatalogMediatorFactory {
        override fun create(path: String, catalogName: String): MediaCatalogRemoteMediator =
            koin.get { parametersOf(path, catalogName) }
    }