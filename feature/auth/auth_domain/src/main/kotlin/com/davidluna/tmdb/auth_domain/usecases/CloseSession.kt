package com.davidluna.tmdb.auth_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError

interface CloseSession {
    suspend fun close(): AppError?
}