package com.davidluna.tmdb.auth_data.repositories

import com.davidluna.tmdb.core_domain.entities.AppError

interface AccountDetailsRepository {
    suspend fun deleteAccount():  AppError?
    suspend fun fetch(): AppError?
    suspend fun hasAccount(): Boolean
}