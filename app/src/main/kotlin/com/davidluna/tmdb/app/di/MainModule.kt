package com.davidluna.tmdb.app.di

import com.davidluna.tmdb.core_data.framework.remote.call_adapter.NetworkCallAdapterFactory
import com.davidluna.tmdb.core_data.framework.remote.interceptors.TmdbInterceptor
import com.davidluna.tmdb.core_data.di.qualifiers.BaseUrl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideClient(
        interceptor: TmdbInterceptor
    ): OkHttpClient = HttpLoggingInterceptor().run {
        level = HttpLoggingInterceptor.Level.BASIC
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(this)
            .build()
    }

    @Singleton
    @Provides
    fun provideJsonConverter(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        coerceInputValues = true
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        @BaseUrl
        baseUrl: String,
        client: OkHttpClient,
        adapter: NetworkCallAdapterFactory,
        json: Json
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(adapter)
            .build()

}