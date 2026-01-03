package com.davidluna.tmdb.media_domain.usecases

import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import kotlinx.coroutines.flow.Flow

interface ObserveFavorites {
    val favorites: Flow<List<FavoriteItem>>
}
