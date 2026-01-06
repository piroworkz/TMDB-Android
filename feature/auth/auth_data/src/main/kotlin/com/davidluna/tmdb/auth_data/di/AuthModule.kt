package com.davidluna.tmdb.auth_data.di

import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_data.framework.remote.UserAccountApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthenticationService(retrofit: Retrofit): AuthenticationApi =
        retrofit.create(AuthenticationApi::class.java)

    @Singleton
    @Provides
    fun provideUserAccountService(retrofit: Retrofit): UserAccountApi =
        retrofit.create(UserAccountApi::class.java)
}