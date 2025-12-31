package com.davidluna.tmdb.auth_framework.data.sources

import com.davidluna.tmdb.auth_domain.entities.LoginRequest
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteTokenResponse
import com.davidluna.tmdb.auth_framework.data.remote.model.RemoteValidateTokenWithLoginRequest

fun RemoteTokenResponse.toRemote(request: LoginRequest): RemoteValidateTokenWithLoginRequest =
    RemoteValidateTokenWithLoginRequest(
        requestToken = requestToken,
        username = request.username,
        password = request.password
    )