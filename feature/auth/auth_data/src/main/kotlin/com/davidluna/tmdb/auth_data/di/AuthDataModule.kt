package com.davidluna.tmdb.auth_data.di

import com.davidluna.tmdb.auth_data.repositories.AccountDetailsRepository
import com.davidluna.tmdb.auth_data.repositories.AccountRepository
import com.davidluna.tmdb.auth_data.repositories.AuthenticationRepository
import com.davidluna.tmdb.auth_data.utils.TextInputValidator
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.auth_domain.usecases.OpenSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateInput
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    singleOf(::TextInputValidator) bind ValidateInput::class
    singleOf(::AccountRepository) bind ObserveUserAccount::class
    singleOf(::AccountRepository) bind AccountDetailsRepository::class
    singleOf(::AuthenticationRepository) bind OpenSession::class
    singleOf(::AuthenticationRepository) bind CloseSession::class
    singleOf(::AuthenticationRepository) bind ValidateSession::class
    singleOf(::AuthenticationRepository) bind ObserveSession::class
}