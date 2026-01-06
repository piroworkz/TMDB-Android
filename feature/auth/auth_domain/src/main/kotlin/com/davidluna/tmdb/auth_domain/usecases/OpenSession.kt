package com.davidluna.tmdb.auth_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.auth_domain.entities.LoginMethod
import com.davidluna.tmdb.core_domain.entities.AppError

interface OpenSession {
    suspend fun open(method: LoginMethod): AppError?
}