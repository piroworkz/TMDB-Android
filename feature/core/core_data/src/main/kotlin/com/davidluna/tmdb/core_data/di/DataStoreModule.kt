package com.davidluna.tmdb.core_data.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataStoreModule = module {
    singleOf(::provideDatastorePreferences)
}

private fun provideDatastorePreferences(application: Application): DataStore<Preferences> =
    PreferenceDataStoreFactory.create {
        application.filesDir.resolve("datastore.preferences_pb")
    }