package com.davidluna.tmdb.media_framework.favorites

import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import com.davidluna.tmdb.media_framework.favorites.local.FavoriteEntity
import com.davidluna.tmdb.media_framework.favorites.local.FavoritesDao
import com.davidluna.tmdb.media_framework.favorites.mapper.toEntity
import com.davidluna.tmdb.media_framework.favorites.repository.FavoritesRepositoryImpl
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class FavoritesRepositoryImplTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    @Test
    fun `GIVEN item not in favorites WHEN toggled THEN item is added`() =
        coroutineRule.scope.runTest {
            val sut = FavoritesRepositoryImpl(FakeFavoritesDao())
            val item = buildFavoriteItem(id = 1)

            val result = sut.toggleFavorite(item)

            assertTrue(result.isRight())
            assertEquals(listOf(item), sut.observeFavorites().first())
        }

    @Test
    fun `GIVEN item in favorites WHEN toggled THEN item is removed`() =
        coroutineRule.scope.runTest {
            val item = buildFavoriteItem(id = 2)
            val sut = FavoritesRepositoryImpl(FakeFavoritesDao(seed = listOf(item.toEntity())))

            val result = sut.toggleFavorite(item)

            assertTrue(result.isRight())
            assertEquals(emptyList<FavoriteItem>(), sut.observeFavorites().first())
        }

    @Test
    fun `GIVEN dao throws WHEN toggled THEN returns error`() =
        coroutineRule.scope.runTest {
            val sut = FavoritesRepositoryImpl(FakeFavoritesDao(throwOnObserve = true))

            val result = sut.toggleFavorite(buildFavoriteItem(id = 3))

            assertTrue(result.isLeft())
        }

    private fun buildFavoriteItem(id: Int): FavoriteItem = FavoriteItem(
        id = id,
        mediaType = MediaType.MOVIE,
        title = "Movie $id",
        posterPath = "/poster/$id",
        timestamp = 1000L + id
    )

    private class FakeFavoritesDao(
        seed: List<FavoriteEntity> = emptyList(),
        private val throwOnObserve: Boolean = false,
    ) : FavoritesDao {
        private val state = MutableStateFlow(seed)

        override suspend fun upsertFavorite(favorite: FavoriteEntity) {
            val updated = state.value.toMutableList()
            val existingIndex = updated.indexOfFirst {
                it.id == favorite.id && it.mediaType == favorite.mediaType
            }
            if (existingIndex >= 0) {
                updated[existingIndex] = favorite
            } else {
                updated.add(favorite)
            }
            state.value = updated
        }

        override suspend fun deleteFavorite(id: Int, mediaType: String) {
            state.value = state.value.filterNot { it.id == id && it.mediaType == mediaType }
        }

        override fun observeFavorites(): Flow<List<FavoriteEntity>> = state

        override fun observeFavoritesByType(mediaType: String): Flow<List<FavoriteEntity>> =
            if (throwOnObserve) {
                flow { throw IllegalStateException("observe failed") }
            } else {
                state.map { favorites -> favorites.filter { it.mediaType == mediaType } }
            }
    }
}
