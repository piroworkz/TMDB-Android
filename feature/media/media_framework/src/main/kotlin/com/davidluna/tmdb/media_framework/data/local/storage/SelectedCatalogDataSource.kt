package com.davidluna.tmdb.media_framework.data.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SelectedCatalogDataSource @Inject constructor(
    private val datastore: DataStore<Preferences>,
) : UpdateSelectedEndpoint, ObserveSelectedMediaCatalog {

    private val key = stringPreferencesKey("selected_endpoint")

    override val selectedCatalog: Flow<Catalog> = datastore.data.map { preferences: Preferences ->
        preferences[key]?.let { catalog: String -> Catalog.valueOf(catalog) }
            ?: Catalog.MOVIE_NOW_PLAYING
    }

    override suspend fun invoke(endpoint: Catalog): Either<AppError, Boolean> = tryCatch {
        datastore.edit { preferences -> preferences[key] = endpoint.name }.contains(key)
    }
}