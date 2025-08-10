package com.davidluna.tmdb.media_framework.di

import com.davidluna.tmdb.media_framework.data.remote.services.RemoteMediaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaNetworkModule {

    @Singleton
    @Provides
    fun provideMediaCatalogService(retrofit: Retrofit): RemoteMediaService =
        retrofit.create(RemoteMediaService::class.java)
}