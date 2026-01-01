package com.davidluna.tmdb.media_domain.usecases

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.details.Video

fun interface GetCatalogVideos : suspend (Catalog, Int) -> Either<AppError, List<Video>>
