package com.davidluna.tmdb.auth_data.data.remote

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteGuestSession
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteLoginRequest
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteSessionIdResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteTokenResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteValidateTokenWithLoginRequest
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.reader.Reader.fromJson

class AuthenticationApiSpy : AuthenticationApi {

    private var shouldThrowError: Boolean = false

    fun throwError(shouldThrow: Boolean) {
        shouldThrowError = shouldThrow
    }

    override suspend fun createRequestToken(): Either<RemoteError, RemoteTokenResponse> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteTokenResponse>(Reader.AUTH_TOKEN_NEW).right()
        }

    override suspend fun authorizeToken(loginRequest: RemoteValidateTokenWithLoginRequest): Either<RemoteError, RemoteTokenResponse> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteTokenResponse>(Reader.AUTH_TOKEN_NEW).right()
        }

    override suspend fun createSessionId(loginRequest: RemoteLoginRequest): Either<RemoteError, RemoteSessionIdResponse> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteSessionIdResponse>(Reader.AUTH_SESSION_NEW).right()
        }

    override suspend fun createGuestSession(): Either<RemoteError, RemoteGuestSession> =
        if (shouldThrowError) {
            fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            fromJson<RemoteGuestSession>(Reader.AUTH_GUEST_SESSION).right()
        }
}