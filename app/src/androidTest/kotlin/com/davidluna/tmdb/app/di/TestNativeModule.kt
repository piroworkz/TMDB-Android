package com.davidluna.tmdb.app.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.davidluna.tmdb.core_data.di.ApiKey
import com.davidluna.tmdb.core_data.di.BaseUrl
import org.koin.dsl.module

val testNativeModule = module {
    single<Context> { ApplicationProvider.getApplicationContext() }
    single { ApiKey("my_fake_api_key") }
    single { BaseUrl("http://localhost:8080/") }
}