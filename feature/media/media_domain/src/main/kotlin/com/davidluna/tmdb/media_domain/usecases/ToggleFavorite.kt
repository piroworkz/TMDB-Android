package com.davidluna.tmdb.media_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.FavoriteItem

fun interface ToggleFavorite : suspend (FavoriteItem) -> Either<AppError, Boolean>
