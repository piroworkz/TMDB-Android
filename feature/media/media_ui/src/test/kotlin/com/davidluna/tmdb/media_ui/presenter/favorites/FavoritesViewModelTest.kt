package com.davidluna.tmdb.media_ui.presenter.favorites

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_framework.fakes.fakeAppError
import com.davidluna.tmdb.media_ui.view.utils.UiState
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
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

    private val favoriteItem = FavoriteItem(
        id = 10,
        posterPath = "/favorite.jpg",
        title = "Favorite",
        mediaType = MediaType.MOVIE,
    )

    @Test
    fun `GIVEN initial state WHEN viewModel is created THEN toggleState is Loading`() {
        val sut = buildSUT()

        assertEquals(UiState.Loading, sut.toggleState.value)
    }

    @Test
    fun `GIVEN toggleFavorite returns Right WHEN toggleFavorite is called THEN state emits Success`() =
        coroutineRule.scope.runTest {
            coEvery { toggleFavorite.invoke(favoriteItem) } returns true.right()
            val sut = buildSUT()

            sut.toggleState.test {
                assertEquals(UiState.Loading, awaitItem())

                sut.toggleFavorite(favoriteItem)

                assertEquals(UiState.Success(true), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN toggleFavorite returns Left WHEN toggleFavorite is called THEN state emits Failure`() =
        coroutineRule.scope.runTest {
            coEvery { toggleFavorite.invoke(favoriteItem) } returns fakeAppError.left()
            val sut = buildSUT()

            sut.toggleState.test {
                assertEquals(UiState.Loading, awaitItem())

                sut.toggleFavorite(favoriteItem)

                assertEquals(UiState.Failure(fakeAppError), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildSUT(): FavoritesViewModel = FavoritesViewModel(
        toggleFavorite = toggleFavorite
    )
}
