package com.davidluna.tmdb.app.di

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.davidluna.tmdb.core_framework.di.qualifiers.ApiKey
import com.davidluna.tmdb.core_framework.di.qualifiers.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NativeModule::class]
)
@Module
object TestNativeModule {


    @Singleton
    @Provides
    @ApiKey
    fun provideApiKey(): String = "my_fake_api_key"

    @Singleton
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = "http://localhost:8080/"

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = ApplicationProvider.getApplicationContext()
}