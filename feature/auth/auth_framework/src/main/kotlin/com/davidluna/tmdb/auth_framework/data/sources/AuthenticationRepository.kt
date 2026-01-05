package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import com.davidluna.tmdb.auth_domain.entities.LoginRequest
import com.davidluna.tmdb.auth_domain.usecases.FetchUserAccount
import com.davidluna.tmdb.auth_domain.usecases.LoginWithCredentials
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.remote.RemoteAuthenticationService
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteLoginRequest
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteTokenResponse
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val remote: RemoteAuthenticationService,
    private val fetchUserAccount: FetchUserAccount,
    private val local: SessionDao
) : LoginWithCredentials {

    override suspend fun invoke(loginRequest: LoginRequest): Either<AppError, Unit> = either {
        val token = remote.createRequestToken().getOrElse { raise(it.toAppError()) }
        val authorization: RemoteTokenResponse = remote.authorizeToken(token.toRemote(loginRequest))
            .getOrElse { raise(it.toAppError()) }
        val session =
            remote.createSessionId(RemoteLoginRequest(requestToken = authorization.requestToken))
                .getOrElse { raise(it.toAppError()) }
        try {
            local.insertSession(session.toLocalStorage())
        } catch (e: Exception) {
            raise(e.toAppError())
        }
        fetchUserAccount().getOrElse {
            raise(it.toAppError())
        }
    }
}
