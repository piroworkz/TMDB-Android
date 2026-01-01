package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.media_domain.entities.Catalog
import kotlinx.coroutines.flow.Flow

interface ObserveSelectedMediaCatalog {
    val selectedCatalog: Flow<Catalog>
}