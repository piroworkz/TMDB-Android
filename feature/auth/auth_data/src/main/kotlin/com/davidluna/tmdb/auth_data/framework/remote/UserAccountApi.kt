package com.davidluna.tmdb.auth_data.framework.remote

import arrow.core.Either
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteUserAccountDetail
import com.davidluna.tmdb.core_framework.data.remote.model.RemoteError
import retrofit2.http.GET

interface UserAccountApi {
    @GET("account")
    suspend fun fetchAccountDetails(): Either<RemoteError, RemoteUserAccountDetail>
}