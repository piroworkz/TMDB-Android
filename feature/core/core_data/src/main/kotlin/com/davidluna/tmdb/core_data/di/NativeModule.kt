package com.davidluna.tmdb.core_data.di

import org.koin.dsl.module

val nativeModule = module {
    single { NativeModule.provideApiKey() }
    single { NativeModule.provideBaseUrl() }
}

object NativeModule {

    init {
        System.loadLibrary("native")
    }

    private external fun getApiKey(): String
    private external fun getBaseUrl(): String

    fun provideApiKey(): ApiKey = ApiKey(getApiKey())
    fun provideBaseUrl(): BaseUrl = BaseUrl(getBaseUrl())

    @JvmInline
    value class BaseUrl(val value: String)

    @JvmInline
    value class ApiKey(val value: String)
}