package com.davidluna.tmdb.media_domain.favorites

import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.repository.FavoritesRepository
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import com.davidluna.tmdb.media_domain.favorites.usecase.ToggleFavoriteUseCase
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ToggleFavoriteUseCaseTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Test
    fun `GIVEN item not in favorites WHEN toggled THEN item is added`() =
        coroutineRule.scope.runTest {
            val item = buildFavoriteItem(id = 1)
            val repository = FakeFavoritesRepository()
            val sut = ToggleFavoriteUseCase(repository)

            val result = sut(item)

            assertTrue(result.isRight())
            assertEquals(listOf(item), repository.observeFavorites().first())
        }

    @Test
    fun `GIVEN item in favorites WHEN toggled THEN item is removed`() =
        coroutineRule.scope.runTest {
            val item = buildFavoriteItem(id = 1)
            val repository = FakeFavoritesRepository(seed = listOf(item))
            val sut = ToggleFavoriteUseCase(repository)

            val result = sut(item)

            assertTrue(result.isRight())
            assertEquals(emptyList<FavoriteItem>(), repository.observeFavorites().first())
        }

    @Test
    fun `GIVEN repository failure WHEN toggled THEN returns error`() =
        coroutineRule.scope.runTest {
            val expected = AppError(
                code = AppErrorCode.LOCAL_ERROR,
                description = "toggle failed"
            )
            val repository = FakeFavoritesRepository(error = expected)
            val sut = ToggleFavoriteUseCase(repository)

            val result = sut(buildFavoriteItem(id = 1))

            assertTrue(result.isLeft())
            assertEquals(expected, result.swap().getOrNull())
        }

    private fun buildFavoriteItem(id: Int): FavoriteItem = FavoriteItem(
        id = id,
        mediaType = MediaType.MOVIE,
        title = "Movie $id",
        posterPath = "/poster/$id",
        timestamp = 1000L + id
    )

    private class FakeFavoritesRepository(
        seed: List<FavoriteItem> = emptyList(),
        private val error: AppError? = null,
    ) : FavoritesRepository {
        private val state = MutableStateFlow(seed)

        override fun observeFavorites(): Flow<List<FavoriteItem>> = state

        override fun observeFavoritesByType(mediaType: MediaType): Flow<List<FavoriteItem>> =
            state.map { favorites -> favorites.filter { it.mediaType == mediaType } }

        override suspend fun addFavorite(item: FavoriteItem) =
            update { favorites -> favorites + item }

        override suspend fun removeFavorite(id: Int, mediaType: MediaType) =
            update { favorites -> favorites.filterNot { it.id == id && it.mediaType == mediaType } }

        override suspend fun toggleFavorite(item: FavoriteItem) =
            update { favorites ->
                val exists = favorites.any { it.id == item.id && it.mediaType == item.mediaType }
                if (exists) favorites.filterNot { it.id == item.id && it.mediaType == item.mediaType }
                else favorites + item
            }

        private fun update(
            block: (List<FavoriteItem>) -> List<FavoriteItem>
        ) = error?.left() ?: run {
            state.value = block(state.value)
            Unit.right()
        }
    }
}
