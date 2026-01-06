package com.davidluna.tmdb.auth_data.framework.remote

import arrow.core.Either
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteGuestSession
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteLoginRequest
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteSessionIdResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteTokenResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteValidateTokenWithLoginRequest
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthenticationApi {
    @GET("authentication/token/new")
    suspend fun createRequestToken(): Either<RemoteError, RemoteTokenResponse>

    @POST("authentication/token/validate_with_login")
    suspend fun authorizeToken(@Body loginRequest: RemoteValidateTokenWithLoginRequest): Either<RemoteError, RemoteTokenResponse>

    @POST("authentication/session/new")
    suspend fun createSessionId(@Body loginRequest: RemoteLoginRequest): Either<RemoteError, RemoteSessionIdResponse>

    @GET("authentication/guest_session/new")
    suspend fun createGuestSession(): Either<RemoteError, RemoteGuestSession>
}