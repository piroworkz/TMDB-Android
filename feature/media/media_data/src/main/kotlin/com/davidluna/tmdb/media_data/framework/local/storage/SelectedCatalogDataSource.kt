package com.davidluna.tmdb.media_data.framework.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatchSuspend
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SelectedCatalogDataSource(
    private val datastore: DataStore<Preferences>,
) : UpdateSelectedEndpoint, ObserveSelectedMediaCatalog {

    private val key = stringPreferencesKey("selected_endpoint")

    override val selectedCatalog: Flow<Catalog> = datastore.data.map { preferences: Preferences ->
        preferences[key]?.let { catalog: String -> Catalog.valueOf(catalog) }
            ?: Catalog.MOVIE_NOW_PLAYING
    }

    override suspend fun update(catalog: Catalog): AppError? = tryCatchSuspend {
        datastore.edit { preferences -> preferences[key] = catalog.name }.contains(key)
        null
    }.leftOrNull()
}