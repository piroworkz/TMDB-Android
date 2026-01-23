package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.MediaType

fun interface ToggleFavorite : suspend (Int, MediaType) -> AppError?
