package com.davidluna.tmdb.app.main_ui.presenter

import com.davidluna.tmdb.media_domain.entities.Catalog

sealed interface MainEvent {
    data class OnCatalogSelected(val endpoint: Catalog) : MainEvent
    data class UpdateBottomNavItems(val bottomNavItems: List<Catalog>) : MainEvent
    data object OnCloseSession : MainEvent
    data object ResetAppError: MainEvent
    data object ClearAppData: MainEvent
}