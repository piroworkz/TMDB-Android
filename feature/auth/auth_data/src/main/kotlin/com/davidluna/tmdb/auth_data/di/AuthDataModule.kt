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
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    factoryOf(::AccountRepository) bind AccountDetailsRepository::class
    factoryOf(::AccountRepository) bind ObserveUserAccount::class
    factoryOf(::AuthenticationRepository) bind CloseSession::class
    factoryOf(::AuthenticationRepository) bind ObserveSession::class
    factoryOf(::AuthenticationRepository) bind OpenSession::class
    factoryOf(::AuthenticationRepository) bind ValidateSession::class
    factoryOf(::TextInputValidator) bind ValidateInput::class
}