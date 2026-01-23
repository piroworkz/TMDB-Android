package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.core_domain.entities.AppError

fun interface ClearFavorites : suspend () -> AppError?
