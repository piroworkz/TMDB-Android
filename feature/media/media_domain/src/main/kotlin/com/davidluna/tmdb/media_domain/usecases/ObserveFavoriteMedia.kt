package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Favorite
import kotlinx.coroutines.flow.Flow

interface ObserveFavoriteMedia {
    fun observe(catalog: Catalog): Flow<List<Favorite>>
}
