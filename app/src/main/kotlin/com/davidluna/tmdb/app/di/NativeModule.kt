package com.davidluna.tmdb.app.di

import com.davidluna.tmdb.core_data.di.qualifiers.ApiKey
import com.davidluna.tmdb.core_data.di.qualifiers.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NativeModule {
    init {
        System.loadLibrary("native")
    }

    private external fun getApiKey(): String
    private external fun getBaseUrl(): String

    @Singleton
    @Provides
    @ApiKey
    fun provideApiKey(): String = getApiKey()

    @Singleton
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = getBaseUrl()

}