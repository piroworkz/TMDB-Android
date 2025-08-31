package com.davidluna.tmdb.core_framework.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.junit.rules.TemporaryFolder
import java.io.File
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [
        DataStoreModule::class,
        CoroutinesModule::class
    ]
)
object TestCoreModule {
    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = StandardTestDispatcher()

    @Singleton
    @Provides
    fun provideCoroutineScope(ioDispatcher: CoroutineDispatcher): CoroutineScope =
        TestScope()

    @Provides
    fun provideDatastorePreferences(scope: CoroutineScope): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { File(TemporaryFolder().newFolder(), "test.preferences_pb") },
        )

}