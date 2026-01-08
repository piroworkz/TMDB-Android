package com.davidluna.tmdb.auth_data.di

import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_data.framework.remote.UserAccountApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit

val authFrameworkRemoteModule = module {
    singleOf(::provideAuthenticationService)
    singleOf(::provideUserAccountService)
}

private fun provideAuthenticationService(retrofit: Retrofit): AuthenticationApi =
    retrofit.create(AuthenticationApi::class.java)

private fun provideUserAccountService(retrofit: Retrofit): UserAccountApi =
    retrofit.create(UserAccountApi::class.java)