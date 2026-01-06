package com.davidluna.tmdb.media_data.data.local.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.davidluna.tmdb.media_data.data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SelectedCatalogDataSourceTest {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private fun newDataStore(tmp: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = coroutineTestRule.scope,
        ) { File(tmp, "test.preferences_pb") }

    private fun buildSUT(dataStore: DataStore<Preferences>) =
        SelectedCatalogDataSource(dataStore)

    private val key = stringPreferencesKey("selected_endpoint")

    @Test
    fun `GIVEN existing valid catalog in datastore WHEN getSelectedCatalog is called THEN flow emits correct Catalog enum`() =
        coroutineTestRule.scope.runTest {
            val dataStore = newDataStore(temporaryFolderRule.newFolder())
            dataStore.edit { it[key] = Catalog.TV_POPULAR.name }
            val sut = buildSUT(dataStore)

            val actual = sut.selectedCatalog.first()

            assertEquals(Catalog.TV_POPULAR, actual)
        }

    @Test
    fun `GIVEN no catalog in datastore WHEN getSelectedCatalog is called THEN flow emits default Catalog`() =
        coroutineTestRule.scope.runTest {
            val dataStore = newDataStore(temporaryFolderRule.newFolder())
            val sut = buildSUT(dataStore)

            val actual = sut.selectedCatalog.first()

            assertEquals(Catalog.MOVIE_NOW_PLAYING, actual)
        }

    @Test
    fun `GIVEN datastore updates WHEN getSelectedCatalog is collected THEN flow emits new values correctly`() =
        coroutineTestRule.scope.runTest {
            val dataStore = newDataStore(temporaryFolderRule.newFolder())
            val sut = buildSUT(dataStore)

            sut.selectedCatalog.test {
                val initialValue = awaitItem()
                dataStore.edit { it[key] = Catalog.TV_TOP_RATED.name }
                val actual = awaitItem()

                assertEquals(Catalog.MOVIE_NOW_PLAYING, initialValue)
                assertEquals(Catalog.TV_TOP_RATED, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN getSelectedCatalog flow is collected WHEN collector is cancelled THEN flow collection stops without issues`() =
        coroutineTestRule.scope.runTest {
            val dataStore = newDataStore(temporaryFolderRule.newFolder())
            val sut = buildSUT(dataStore)

            val job = launch { sut.selectedCatalog.test { cancelAndIgnoreRemainingEvents() } }
            job.cancelAndJoin()

            assertTrue(job.isCancelled)
        }

    @Test
    fun `GIVEN a valid Catalog WHEN invoke is called THEN DataStore is updated and Either Right(true) is returned`() =
        coroutineTestRule.scope.runTest {
            val dataStore = newDataStore(temporaryFolderRule.newFolder())
            val sut = buildSUT(dataStore)

            val actual = sut.update(Catalog.TV_TOP_RATED)

            assertNull(actual)
        }
}