package com.davidluna.tmdb.media_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError

fun interface ClearFavorites : suspend () -> Either<AppError, Unit>
