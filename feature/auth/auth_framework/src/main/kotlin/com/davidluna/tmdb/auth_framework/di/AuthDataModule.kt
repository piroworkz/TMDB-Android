package com.davidluna.tmdb.auth_framework.di

import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.FetchUserAccount
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.auth_domain.usecases.LoginAsGuest
import com.davidluna.tmdb.auth_domain.usecases.LoginWithCredentials
import com.davidluna.tmdb.auth_domain.usecases.IsGuestSessionValid
import com.davidluna.tmdb.auth_domain.usecases.GetTextInputError
import com.davidluna.tmdb.auth_framework.data.local.QueryParametersSnapshot
import com.davidluna.tmdb.auth_framework.data.local.TextInputValidator
import com.davidluna.tmdb.auth_framework.data.sources.SessionCloser
import com.davidluna.tmdb.auth_framework.data.sources.GuestSessionExpirationValidator
import com.davidluna.tmdb.auth_framework.data.sources.GuestSessionRepository
import com.davidluna.tmdb.auth_framework.data.sources.SessionStore
import com.davidluna.tmdb.auth_framework.data.sources.AccountStore
import com.davidluna.tmdb.auth_framework.data.sources.AuthenticationRepository
import com.davidluna.tmdb.auth_framework.data.sources.UserAccountFetcher
import com.davidluna.tmdb.core_framework.data.remote.interceptors.ParametersSnapshot
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthDataModule {
    @Binds abstract fun bindSessionCloser(source: SessionCloser): CloseSession
    @Binds abstract fun bindUserAccountFetcher(source: UserAccountFetcher): FetchUserAccount
    @Binds abstract fun bindSessionStore(source: SessionStore): ObserveSession
    @Binds abstract fun bindAccountStore(source: AccountStore): ObserveUserAccount
    @Binds abstract fun bindGuestSessionRepository(source: GuestSessionRepository): LoginAsGuest
    @Binds abstract fun bindAuthenticationRepository(source: AuthenticationRepository): LoginWithCredentials
    @Binds abstract fun bindQueryParametersSnapshot(source: QueryParametersSnapshot): ParametersSnapshot
    @Binds abstract fun bindGuestSessionExpirationValidator(source: GuestSessionExpirationValidator): IsGuestSessionValid
    @Binds abstract fun bindTextInputValidator(source: TextInputValidator): GetTextInputError
}