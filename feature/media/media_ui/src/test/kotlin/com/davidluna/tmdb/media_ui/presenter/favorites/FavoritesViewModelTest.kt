package com.davidluna.tmdb.media_ui.presenter.favorites

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ObserveFavorites
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_framework.fakes.fakeAppError
import com.davidluna.tmdb.media_ui.view.utils.UiState
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var toggleFavorite: ToggleFavorite

    @MockK
    private lateinit var observeFavorites: ObserveFavorites

    private val favoriteItem = FavoriteItem(
        id = 10,
        posterPath = "/favorite.jpg",
        title = "Favorite",
        mediaType = MediaType.MOVIE,
    )

    private val media = Media(
        id = favoriteItem.id,
        posterPath = favoriteItem.posterPath,
        title = favoriteItem.title,
    )

    @Test
    fun `GIVEN initial state WHEN viewModel is created THEN toggleState is Loading`() {
        every { observeFavorites.favorites } returns emptyFlow()
        val sut = buildSUT()

        assertEquals(UiState.Loading, sut.toggleState.value)
    }

    @Test
    fun `GIVEN toggleFavorite returns Right WHEN toggleFavorite is called THEN state emits Success`() =
        coroutineRule.scope.runTest {
            coEvery { toggleFavorite.invoke(favoriteItem) } returns true.right()
            every { observeFavorites.favorites } returns emptyFlow()
            val sut = buildSUT()

            sut.toggleState.test {
                assertEquals(UiState.Loading, awaitItem())

                sut.toggleFavorite(media, MediaType.MOVIE)

                assertEquals(UiState.Success(true), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            coroutineRule.scope.coroutineContext.cancelChildren()
        }

    @Test
    fun `GIVEN toggleFavorite returns Left WHEN toggleFavorite is called THEN state emits Failure`() =
        coroutineRule.scope.runTest {
            coEvery { toggleFavorite.invoke(favoriteItem) } returns fakeAppError.left()
            every { observeFavorites.favorites } returns emptyFlow()
            val sut = buildSUT()

            sut.toggleState.test {
                assertEquals(UiState.Loading, awaitItem())

                sut.toggleFavorite(media, MediaType.MOVIE)

                assertEquals(UiState.Failure(fakeAppError), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            coroutineRule.scope.coroutineContext.cancelChildren()
        }

    @Test
    fun `GIVEN favorites are observed WHEN favoritesState is collected THEN it emits Success`() =
        coroutineRule.scope.runTest {
            every { observeFavorites.favorites } returns flowOf(listOf(favoriteItem))
            val sut = buildSUT()

            sut.favoritesState.test {
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(UiState.Success(listOf(favoriteItem)), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            coroutineRule.scope.coroutineContext.cancelChildren()
        }

    private fun buildSUT(): FavoritesViewModel = FavoritesViewModel(
        toggleFavorite = toggleFavorite,
        observeFavorites = observeFavorites,
        scope = coroutineRule.scope
    )
}
