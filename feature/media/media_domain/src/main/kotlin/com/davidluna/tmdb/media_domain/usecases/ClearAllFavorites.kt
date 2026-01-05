package com.davidluna.tmdb.media_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError

interface ClearAllFavorites {
    fun clear(): Either<AppError, Unit>
}
