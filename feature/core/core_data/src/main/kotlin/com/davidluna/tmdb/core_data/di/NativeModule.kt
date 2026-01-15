package com.davidluna.tmdb.core_data.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val nativeModule = module {
    singleOf(NativeModule::provideApiKey)
    singleOf(NativeModule::provideBaseUrl)
}

object NativeModule {
    init {
        System.loadLibrary("native")
    }

    private external fun getApiKey(): String
    private external fun getBaseUrl(): String

    fun provideApiKey(): ApiKey = ApiKey(getApiKey())
    fun provideBaseUrl(): BaseUrl = BaseUrl(getBaseUrl())
}