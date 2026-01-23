package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.core_domain.entities.AppError

interface ClearFavorites {
    suspend fun clear(): AppError?
}
