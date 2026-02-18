package com.davidluna.tmdb.media_ui.presenter.media

import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Media

sealed interface MediaCatalogEvent {
    data class LastKnownPosition(val index: MediaIndex, val offset: MediaOffset) : MediaCatalogEvent
    data class ToggleFavorite(val media: Media, val catalog: Catalog) : MediaCatalogEvent
    object ClearAppError : MediaCatalogEvent
}