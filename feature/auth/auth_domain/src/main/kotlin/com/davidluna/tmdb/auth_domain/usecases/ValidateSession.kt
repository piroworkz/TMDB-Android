package com.davidluna.tmdb.auth_domain.usecases

interface ValidateSession {
    suspend fun isValid(): Boolean
}