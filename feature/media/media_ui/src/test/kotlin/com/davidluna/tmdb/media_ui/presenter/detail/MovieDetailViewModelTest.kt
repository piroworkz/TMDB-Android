package com.davidluna.tmdb.media_ui.presenter.detail

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.usecases.GetMediaDetails
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_framework.fakes.fakeAppError
import com.davidluna.tmdb.media_framework.fakes.fakeMediaDetails
import com.davidluna.tmdb.media_ui.view.utils.UiState
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

class MovieDetailViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var observeSelectedMediaCatalogUseCase: ObserveSelectedMediaCatalog

    @MockK
    private lateinit var getMediaDetails: GetMediaDetails

    @Test
    fun `GIVEN initial state WHEN viewModel is created THEN mediaDetails is UiState Loading`() {
        every { observeSelectedMediaCatalogUseCase.selectedCatalog } returns emptyFlow()
        val sut = buildSUT()

        val actual = sut.mediaDetails.value

        assertEquals(UiState.Loading, actual)
    }

    @Test
    fun `GIVEN getSelectedMediaCatalogUseCase emits MOVIE and getMediaDetails returns Right WHEN fetching movie details THEN mediaDetails is UiState Success with movie details`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Success(fakeMediaDetails)
            every { observeSelectedMediaCatalogUseCase.selectedCatalog } returns flowOf(Catalog.MOVIE_UPCOMING)
            coEvery { getMediaDetails(any(), any()) } returns fakeMediaDetails.right()
            val sut = buildSUT()

            sut.mediaDetails.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN getMediaDetails returns Left WHEN fetching media details THEN mediaDetails is UiState Failure`() =
        coroutineTestRule.scope.runTest {
            val expected = UiState.Failure(fakeAppError)
            every { observeSelectedMediaCatalogUseCase.selectedCatalog } returns flowOf(Catalog.MOVIE_UPCOMING)
            coEvery { getMediaDetails(any(), any()) } returns fakeAppError.left()
            val sut = buildSUT()

            sut.mediaDetails.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildSUT(): MediaDetailsViewModel = MediaDetailsViewModel(
        observeSelectedMediaCatalogUseCase = observeSelectedMediaCatalogUseCase,
        getMediaDetails = getMediaDetails,
        mediaId = 1
    )

}