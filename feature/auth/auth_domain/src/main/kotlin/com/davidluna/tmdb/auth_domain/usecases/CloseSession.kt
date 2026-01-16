package com.davidluna.tmdb.auth_domain.usecases

import com.davidluna.tmdb.core_domain.entities.AppError

interface CloseSession {
    suspend fun close(): AppError?
}