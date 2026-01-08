package com.davidluna.tmdb.media_ui.presenter.media

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.davidluna.tmdb.media_data.data.local.database.dao.MediaDaoSpy
import com.davidluna.tmdb.media_data.data.local.database.dao.RemoteKeysDaoSpy
import com.davidluna.tmdb.media_data.data.remote.services.RemoteMediaServiceSpy
import com.davidluna.tmdb.media_data.di.MediaCatalogMediatorFactory
import com.davidluna.tmdb.media_data.di.MediaCatalogMediatorFactorySpy
import com.davidluna.tmdb.media_data.fakes.fakeMediaList
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.framework.local.database.dao.RemoteKeysDao
import com.davidluna.tmdb.media_data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_data.framework.paging.CachePolicyValidator
import com.davidluna.tmdb.media_data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_data.repositories.MediaCatalogRepository
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MediaCatalogIntegrationTest {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var mediaDao: MediaDao
    private lateinit var mediaService: RemoteMediaService
    private lateinit var remoteKeysDao: RemoteKeysDao
    private lateinit var isCacheExpired: IsCacheExpired
    private lateinit var dataStore: DataStore<Preferences>

    @Test
    fun `GIVEN pagerPagingDataFlow WHEN subscriber is added and refresh is called THEN emits paging data`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeMediaList
            val sut = buildSUT()

            val actual: @JvmSuppressWildcards List<Media> =
                sut.pagerPagingDataFlow.asSnapshot { refresh() }

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN pagerPagingDataFlow WHEN refresh is called twice THEN result is idempotent`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeMediaList
            val sut = buildSUT()

            val actual = sut.pagerPagingDataFlow.asSnapshot {
                refresh()
                refresh()
            }

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN pagerPagingDataFlow WHEN refresh and then appendScrollWhile are called THEN snapshot remains valid`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            val snapshot = sut.pagerPagingDataFlow.asSnapshot {
                refresh()
                appendScrollWhile { true }
            }

            assertTrue("Snapshot is not empty after refresh()", snapshot.isNotEmpty())
        }

    @Test
    fun `GIVEN gridPagingDataFlow WHEN subscriber is added  and refresh is called THEN emits paging data`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeMediaList
            val sut = buildSUT()

            val actual: @JvmSuppressWildcards List<Media> =
                sut.gridPagingDataFlow.asSnapshot { refresh() }

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN gridPagingDataFlow WHEN refresh is called twice THEN result is idempotent`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeMediaList
            val sut = buildSUT()

            val actual = sut.gridPagingDataFlow.asSnapshot {
                refresh()
                refresh()
            }

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN gridPagingDataFlow WHEN refresh and then appendScrollWhile are called THEN snapshot remains valid`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            val snapshot = sut.gridPagingDataFlow.asSnapshot {
                refresh()
                appendScrollWhile { true }
            }

            assertTrue("Snapshot is not empty after refresh()", snapshot.isNotEmpty())
        }

    @Test
    fun `GIVEN getGridFlow and getPagerFlow WHEN selectedCatalog is collected successfully THEN should set corresponding titles in state`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            sut.pagerPagingDataFlow.asSnapshot { refresh() }
            sut.gridPagingDataFlow.asSnapshot { refresh() }

            sut.state.test {
                val actual = awaitItem()

                assertNotNull("pagerCatalogTitle is not null", actual.pagerCatalogTitle)
                assertNotNull("gridCatalogTitle is not null", actual.gridCatalogTitle)
            }
        }

    @Test
    fun `GIVEN updateLastKnownPosition WHEN called THEN should set lastKnownPosition in state`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()
            val expected = 1 to 2
            sut.updateLastKnownPosition(expected.first, expected.second)

            sut.state.test {
                skipItems(1)
                val actual = awaitItem().lastKnownPosition

                assertEquals(expected, actual)
            }
        }

    private fun buildSUT(): MediaCatalogViewModel {
        mediaDao = MediaDaoSpy()
        mediaService = RemoteMediaServiceSpy()
        remoteKeysDao = RemoteKeysDaoSpy()
        isCacheExpired = CachePolicyValidator()

        dataStore = newDataStore(temporaryFolderRule.newFolder())
        val getSelectedMediaCatalog = SelectedCatalogDataSource(dataStore)
        val mediatorFactory: MediaCatalogMediatorFactory = MediaCatalogMediatorFactorySpy(
            mediaDao = mediaDao,
            mediaService = mediaService,
            remoteKeysDao = remoteKeysDao,
            isCacheExpired = isCacheExpired
        )
        val observeMediaCatalogUseCase = MediaCatalogRepository(
            mediaDao = mediaDao,
            mediatorFactory = mediatorFactory
        )
        return MediaCatalogViewModel(
            observeSelectedMediaCatalogUseCase = getSelectedMediaCatalog,
            observeMediaCatalogUseCase = observeMediaCatalogUseCase
        )
    }

    private fun newDataStore(tmp: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = coroutineTestRule.scope,
        ) { File(tmp, "test.preferences_pb") }
}