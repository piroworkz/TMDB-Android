package com.davidluna.tmdb.media_ui.presenter.detail

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.davidluna.tmdb.core_data.framework.remote.model.RemoteError
import com.davidluna.tmdb.core_data.framework.remote.model.toAppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.media_data.data.local.database.dao.MediaDetailsDaoSpy
import com.davidluna.tmdb.media_data.data.remote.services.RemoteMediaServiceSpy
import com.davidluna.tmdb.media_data.fakes.fakeMediaDetails
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDetailsDao
import com.davidluna.tmdb.media_data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_data.framework.paging.CachePolicyValidator
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_data.repositories.MediaDetailsCacheRepository
import com.davidluna.tmdb.media_ui.view.utils.UiState
import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MediaDetailsIntegrationTests {

    @get:Rule(order = 1)
    val temporaryFolderRule = TemporaryFolder()

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var mediaDetailsDao: MediaDetailsDao
    private lateinit var remoteMediaService: RemoteMediaService
    private val mediaId = 1022789

    @Test
    fun `GIVEN ViewModel instantiated WHEN mediaDetails is observed THEN initial state is Loading`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Loading
            val sut = buildSUT()

            sut.mediaDetails.test {
                val actual = awaitItem()
                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN GetMediaDetailsUseCase WHEN is successful THEN mediaDetails emits UiState Success`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Success(data = fakeMediaDetails)
            val sut = buildSUT()

            sut.mediaDetails.test {
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

            (mediaDetailsDao as MediaDetailsDaoSpy).setError(exception)
            sut.mediaDetails.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN GetMediaDetailsUseCase WHEN any remote failure THEN emits UiState Failure`() =
        coroutineTestRule.scope.runTest {
            val exception = Reader.fromJson<RemoteError>(Reader.REMOTE_ERROR)
            val expected = UiState.Failure(appError = exception.toAppError())
            val sut = buildSUT()

            (remoteMediaService as RemoteMediaServiceSpy).throwError(true)
            sut.mediaDetails.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    private fun buildSUT(): MediaDetailsViewModel {
        mediaDetailsDao = MediaDetailsDaoSpy()
        remoteMediaService = RemoteMediaServiceSpy()
        val dataStore = newDataStore(temporaryFolderRule.newFolder())
        val isCacheExpired = CachePolicyValidator()
        val getSelectedMediaCatalog = SelectedCatalogDataSource(dataStore)
        val getCountryCodeUseCase = provideFakeGetCountryCodeUseCase()
        val getMediaDetailsUseCase = MediaDetailsCacheRepository(
            local = mediaDetailsDao,
            remote = remoteMediaService,
            isCacheExpired = isCacheExpired,
            observeCountryCode = getCountryCodeUseCase
        )

        return MediaDetailsViewModel(
            observeSelectedMediaCatalogUseCase = getSelectedMediaCatalog,
            getMediaDetails = getMediaDetailsUseCase,
            mediaId = mediaId
        )
    }

    private fun newDataStore(tmp: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = coroutineTestRule.scope,
        ) { File(tmp, "test.preferences_pb") }

    private fun provideFakeGetCountryCodeUseCase(): ObserveCountryCode {
        return ObserveCountryCode { flowOf("US") }
    }
}