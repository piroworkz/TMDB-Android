package com.davidluna.tmdb.auth_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError

fun interface FetchUserAccount : suspend () -> Either<AppError, Unit>