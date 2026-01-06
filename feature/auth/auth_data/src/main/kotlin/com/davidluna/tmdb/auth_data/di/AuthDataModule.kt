package com.davidluna.tmdb.auth_data.di

import com.davidluna.tmdb.auth_data.framework.local.QueryParametersSnapshot
import com.davidluna.tmdb.auth_data.framework.local.TextInputValidator
import com.davidluna.tmdb.auth_data.repositories.AccountDetailsRepository
import com.davidluna.tmdb.auth_data.repositories.AccountRepository
import com.davidluna.tmdb.auth_data.repositories.AuthenticationRepository
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateInput
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.auth_domain.usecases.OpenSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.core_data.framework.remote.interceptors.ParametersSnapshot
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {
    @Binds
    abstract fun bindCloseSession(source: AuthenticationRepository): CloseSession

    @Binds
    abstract fun bindObserveSession(source: AuthenticationRepository): ObserveSession

    @Binds
    abstract fun bindOpenSession(source: AuthenticationRepository): OpenSession

    @Binds
    abstract fun bindValidateSession(source: AuthenticationRepository): ValidateSession

    @Binds
    abstract fun bindAccountStore(source: AccountRepository): ObserveUserAccount

    @Binds
    abstract fun bindAccountDetailsRepository(source: AccountRepository): AccountDetailsRepository

    @Binds
    abstract fun bindQueryParametersSnapshot(source: QueryParametersSnapshot): ParametersSnapshot

    @Binds
    abstract fun bindTextInputValidator(source: TextInputValidator): ValidateInput
}