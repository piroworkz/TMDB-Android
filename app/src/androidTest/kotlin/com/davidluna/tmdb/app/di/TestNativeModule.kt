package com.davidluna.tmdb.app.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.davidluna.tmdb.core_data.di.NativeModule
import org.koin.dsl.module

val testNativeModule = module {
    single<Context> { ApplicationProvider.getApplicationContext() }
    single { NativeModule.ApiKey("my_fake_api_key") }
    single { NativeModule.BaseUrl("http://localhost:8080/") }
}