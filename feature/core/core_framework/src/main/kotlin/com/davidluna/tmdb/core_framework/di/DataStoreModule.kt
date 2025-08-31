package com.davidluna.tmdb.core_framework.di

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun provideDatastorePreferences(application: Application) = PreferenceDataStoreFactory.create {
        application.filesDir.resolve("datastore.preferences_pb")
    }
}