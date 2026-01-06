package com.davidluna.tmdb.media_ui.presenter.videos

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaVideosDao
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaVideosDaoSpy
import com.davidluna.tmdb.media_framework.data.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_framework.data.paging.CachePolicyValidator
import com.davidluna.tmdb.media_framework.data.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_framework.data.remote.services.RemoteMediaServiceSpy
import com.davidluna.tmdb.media_framework.data.repositories.CatalogVideosRepository
import com.davidluna.tmdb.media_framework.fakes.fakeVideos
import com.davidluna.tmdb.media_ui.view.utils.UiState
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VideoPlayerIntegrationTest {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var mediaVideosDao: MediaVideosDao
    private lateinit var remoteMediaService: RemoteMediaService
    private val mediaId = 1022789

    @Test
    fun `GIVEN ViewModel instantiated WHEN mediaDetails is observed THEN initial state is Loading`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Loading
            val sut = buildSUT()

            sut.mediaVideos.test {
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN GetMediaDetailsUseCase WHEN is successful THEN mediaDetails emits UiState Success`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Success(data = fakeVideos)
            val sut = buildSUT()

            sut.mediaVideos.test {
                skipItems(1)
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN GetMediaDetailsUseCase WHEN any local failure THEN mediaDetails emits UiState Failure`() =
        coroutineTestRule.scope.runTest {
            val exception = IllegalStateException("Error")
            val expected = UiState.Failure(appError = exception.toAppError())
            val sut = buildSUT()

            (mediaVideosDao as MediaVideosDaoSpy).setError(exception)
            sut.mediaVideos.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }


    private fun buildSUT(): VideoPlayerViewModel {
        mediaVideosDao = MediaVideosDaoSpy()
        remoteMediaService = RemoteMediaServiceSpy()
        val dataStore = newDataStore(temporaryFolderRule.newFolder())
        val getSelectedMediaCatalog = SelectedCatalogDataSource(dataStore)
        val getMediaVideosUseCase = CatalogVideosRepository(
            local = mediaVideosDao,
            remote = remoteMediaService,
            isCacheExpired = CachePolicyValidator()
        )

        return VideoPlayerViewModel(
            observeSelectedMediaCatalogUseCase = getSelectedMediaCatalog,
            getCatalogVideos = getMediaVideosUseCase,
            mediaId = mediaId
        )
    }

    private fun newDataStore(tmp: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = coroutineTestRule.scope,
        ) { File(tmp, "test.preferences_pb") }
}