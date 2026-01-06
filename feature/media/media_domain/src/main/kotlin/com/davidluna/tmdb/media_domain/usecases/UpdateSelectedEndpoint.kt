package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.Catalog

interface UpdateSelectedEndpoint {
    suspend fun update(catalog: Catalog): AppError?
}