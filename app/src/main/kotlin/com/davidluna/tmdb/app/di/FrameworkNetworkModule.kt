package com.davidluna.tmdb.app.di

import com.davidluna.tmdb.auth_data.framework.local.QueryParametersSnapshot
import com.davidluna.tmdb.core_data.di.NativeModule
import com.davidluna.tmdb.core_data.framework.remote.call_adapter.NetworkCallAdapterFactory
import com.davidluna.tmdb.core_data.framework.remote.interceptors.ParametersSnapshot
import com.davidluna.tmdb.core_data.framework.remote.interceptors.TmdbInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    singleOf(::NetworkCallAdapterFactory)
    singleOf(::provideClient)
    singleOf(::provideJsonConverter)
    singleOf(::provideRetrofit)
    singleOf(::TmdbInterceptor) bind Interceptor::class
    singleOf(::QueryParametersSnapshot) bind ParametersSnapshot::class
}

private fun provideClient(
    interceptor: TmdbInterceptor
): OkHttpClient = HttpLoggingInterceptor().run {
    level = HttpLoggingInterceptor.Level.BASIC
    OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(this)
        .build()
}


private fun provideJsonConverter(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
    coerceInputValues = true
}


private fun provideRetrofit(
    baseUrl: NativeModule.BaseUrl,
    client: OkHttpClient,
    adapter: NetworkCallAdapterFactory,
    json: Json
): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl.value)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(adapter)
        .build()