package com.davidluna.tmdb.media_ui.favorites

import app.cash.turbine.test
import arrow.core.right
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import com.davidluna.tmdb.media_domain.favorites.usecase.ToggleFavoriteUseCase
import com.davidluna.tmdb.media_ui.favorites.viewmodel.FavoritesViewModel
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coAnswers
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.CompletableDeferred
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
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase

    @Test
    fun `GIVEN item not favorited WHEN toggled THEN favoriteIds updates immediately`() =
        coroutineRule.scope.runTest {
            val gate = CompletableDeferred<Unit>()
            val item = buildFavoriteItem(id = 1)
            coEvery { toggleFavoriteUseCase.invoke(item) } coAnswers {
                gate.await()
                Unit.right()
            }
            val sut = FavoritesViewModel(toggleFavoriteUseCase = toggleFavoriteUseCase)

            sut.favoriteIds.test {
                assertEquals(emptySet<Int>(), awaitItem())

                sut.onToggleFavorite(item)

                assertEquals(setOf(item.id), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN item already favorited WHEN toggled THEN favoriteIds removes immediately`() =
        coroutineRule.scope.runTest {
            val gate = CompletableDeferred<Unit>()
            val item = buildFavoriteItem(id = 2)
            var firstCall = true
            coEvery { toggleFavoriteUseCase.invoke(item) } coAnswers {
                if (firstCall) {
                    firstCall = false
                    Unit.right()
                } else {
                    gate.await()
                    Unit.right()
                }
            }
            val sut = FavoritesViewModel(toggleFavoriteUseCase = toggleFavoriteUseCase)

            sut.favoriteIds.test {
                assertEquals(emptySet<Int>(), awaitItem())

                sut.onToggleFavorite(item)
                assertEquals(setOf(item.id), awaitItem())

                sut.onToggleFavorite(item)
                assertEquals(emptySet<Int>(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildFavoriteItem(id: Int): FavoriteItem = FavoriteItem(
        id = id,
        mediaType = MediaType.MOVIE,
        title = "Movie $id",
        posterPath = "/poster/$id",
        timestamp = 1_000L + id
    )
}
