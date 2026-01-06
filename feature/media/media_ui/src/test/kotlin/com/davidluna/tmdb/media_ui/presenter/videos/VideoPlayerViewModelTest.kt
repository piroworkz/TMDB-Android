package com.davidluna.tmdb.media_ui.presenter.videos

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_ui.view.utils.UiState
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.GetCatalogVideos
import com.davidluna.tmdb.media_data.fakes.fakeAppError
import com.davidluna.tmdb.media_data.fakes.fakeVideos
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class VideoPlayerViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var observeSelectedMediaCatalogUseCase: ObserveSelectedMediaCatalog

    @MockK
    private lateinit var getCatalogVideos: GetCatalogVideos

    @Test
    fun `GIVEN initial state WHEN viewModel is created THEN mediaVideos is Loading`() {
        every { observeSelectedMediaCatalogUseCase.selectedCatalog } returns emptyFlow()
        val sut = buildSUT()

        assert(sut.mediaVideos.value is UiState.Loading)
    }

    @Test
    fun `GIVEN getSelectedMediaCatalogUseCase emits any value and getMediaVideosUseCase returns Right WHEN fetching videos THEN mediaVideos emits Success with movie videos`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Success(fakeVideos)
            every { observeSelectedMediaCatalogUseCase.selectedCatalog } returns flowOf(Catalog.MOVIE_UPCOMING)
            coEvery { getCatalogVideos(any(), any()) } returns fakeVideos.right()
            val sut = buildSUT()

            sut.mediaVideos.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancelAndConsumeRemainingEvents()
            }

        }

    @Test
    fun `GIVEN getSelectedMediaCatalogUseCase emits any value and getMediaVideosUseCase returns Left WHEN fetching videos THEN mediaVideos emits Failure`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Failure(fakeAppError)
            every { observeSelectedMediaCatalogUseCase.selectedCatalog } returns flowOf(Catalog.MOVIE_UPCOMING)
            coEvery { getCatalogVideos(any(), any()) } returns fakeAppError.left()
            val sut = buildSUT()

            sut.mediaVideos.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancelAndConsumeRemainingEvents()
            }

        }

    private fun buildSUT() = VideoPlayerViewModel(
        observeSelectedMediaCatalogUseCase = observeSelectedMediaCatalogUseCase,
        getCatalogVideos = getCatalogVideos,
        mediaId = 1
    )

}