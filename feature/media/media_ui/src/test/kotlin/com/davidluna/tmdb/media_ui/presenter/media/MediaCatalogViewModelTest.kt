package com.davidluna.tmdb.media_ui.presenter.media

import app.cash.turbine.test
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.usecases.GetSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalogUseCase
import com.davidluna.tmdb.media_ui.fakeEmptyPagingData
import com.davidluna.tmdb.media_ui.fakeMediaPagingData
import com.davidluna.tmdb.media_ui.view.utils.title
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MediaCatalogViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var getSelectedMediaCatalogUseCase: GetSelectedMediaCatalog

    @MockK
    private lateinit var observeMediaCatalogUseCase: ObserveMediaCatalogUseCase

    private val initialState = MediaCatalogViewModel.State()

    @Test
    fun `GIVEN ViewModel is created WHEN getState is called THEN it returns initial state`() {
        every { getSelectedMediaCatalogUseCase.selectedCatalog } returns emptyFlow()

        val sut = buildSUT()
        val actual = sut.state.value

        assertEquals(initialState, actual)
    }

    @Test
    fun `GIVEN an error occurs in getGridFlow WHEN getState is called THEN it reflects updated appError`() =
        coroutineTestRule.scope.runTest {
            val exception = IllegalStateException("Something went wrong")
            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns flow { throw exception }
            val sut = buildSUT()

            val getGridFlowJob = launch { sut.gridPagingDataFlow.collect {} }

            sut.state.test {
                awaitItem()
                val actual = awaitItem().appError

                assertEquals(exception.toAppError(), actual)
                cancelAndIgnoreRemainingEvents()
            }

            getGridFlowJob.cancelAndJoin()
        }

    @Test
    fun `GIVEN getGridFlow successfully processes a media catalog WHEN getState is called THEN it reflects updated gridCatalogTitle`() =
        coroutineTestRule.scope.runTest {
            val endpoint = Catalog.MOVIE_UPCOMING
            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns flow { emit(endpoint) }
            every { observeMediaCatalogUseCase(any(), any()) } returns emptyFlow()
            val sut = buildSUT()

            val getGridFlowJob = launch { sut.gridPagingDataFlow.collect {} }

            sut.state.test {
                awaitItem()
                val actual = awaitItem().gridCatalogTitle

                assertEquals(endpoint.title, actual)
                cancelAndIgnoreRemainingEvents()
            }

            getGridFlowJob.cancelAndJoin()
        }

    @Test
    fun `GIVEN getPagerFlow successfully processes a media catalog WHEN getState is called THEN it reflects updated pagerCatalogTitle`() =
        coroutineTestRule.scope.runTest {
            val endpoint = Catalog.MOVIE_UPCOMING
            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns flow { emit(endpoint) }
            every { observeMediaCatalogUseCase(any(), any()) } returns emptyFlow()

            val sut = buildSUT()

            val getGridFlowJob = launch { sut.gridPagingDataFlow.collect {} }

            sut.state.test {
                awaitItem()
                val actual = awaitItem().gridCatalogTitle

                assertEquals(endpoint.title, actual)
                cancelAndIgnoreRemainingEvents()
            }

            getGridFlowJob.cancelAndJoin()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GIVEN updateLastKnownPosition is called WHEN getState is called THEN it reflects updated lastKnownPosition`() =
        coroutineTestRule.scope.runTest {
            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns emptyFlow()
            val sut = buildSUT()

            sut.updateLastKnownPosition(10, 100)
            advanceUntilIdle()
            val actual = sut.state.value.lastKnownPosition

            assertEquals(10 to 100, actual)
        }

    @Test
    fun `GIVEN ViewModel is created WHEN pagerPagingDataFlow is collected THEN it emits initial empty PagingData`() =
        coroutineTestRule.scope.runTest {

            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns flowOf(Catalog.MOVIE_UPCOMING)
            every { observeMediaCatalogUseCase.invoke(any(), any()) } returns flowOf(
                fakeEmptyPagingData
            )
            val sut = buildSUT()

            sut.pagerPagingDataFlow.test {
                awaitItem()
                val actual = awaitItem()

                assertEquals(fakeEmptyPagingData, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN getSelectedMediaCatalogUseCase emits any WHEN pagerPagingDataFlow is collected THEN it emits Media PagingData`() =
        coroutineTestRule.scope.runTest {
            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns flowOf(Catalog.MOVIE_UPCOMING)
            every { observeMediaCatalogUseCase.invoke(any(), any()) } returns flowOf(
                fakeMediaPagingData
            )
            val sut = buildSUT()

            sut.pagerPagingDataFlow.test {
                awaitItem()
                val actual = awaitItem()

                assertEquals(fakeMediaPagingData, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN getMediaCatalogUseCase emits MOVIE WHEN pagerPagingDataFlow is collected THEN state's pagerCatalogTitle updates to MOVIE_UPCOMING title`() =
        coroutineTestRule.scope.runTest {
            val endpoint = Catalog.MOVIE_UPCOMING
            every { getSelectedMediaCatalogUseCase.selectedCatalog } returns flow { emit(endpoint) }
            every { observeMediaCatalogUseCase(any(), any()) } returns emptyFlow()
            val sut = buildSUT()

            val getGridFlowJob = launch { sut.pagerPagingDataFlow.collect {} }

            sut.state.test {
                awaitItem()
                val actual = awaitItem().pagerCatalogTitle

                assertEquals(endpoint.title, actual)
                cancelAndIgnoreRemainingEvents()
            }

            getGridFlowJob.cancelAndJoin()
        }

    private fun buildSUT(): MediaCatalogViewModel = MediaCatalogViewModel(
        getSelectedMediaCatalogUseCase = getSelectedMediaCatalogUseCase,
        observeMediaCatalogUseCase = observeMediaCatalogUseCase
    )
}