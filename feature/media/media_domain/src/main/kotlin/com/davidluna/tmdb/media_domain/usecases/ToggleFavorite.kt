package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.Catalog

interface ToggleFavorite {
    suspend fun toggle(mediaId: Int, catalog: Catalog): AppError?
}
