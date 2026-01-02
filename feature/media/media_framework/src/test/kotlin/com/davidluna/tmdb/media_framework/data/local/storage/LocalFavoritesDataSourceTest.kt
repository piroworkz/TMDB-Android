package com.davidluna.tmdb.media_framework.data.local.storage

import com.davidluna.tmdb.media_framework.data.local.database.dao.FavoritesDaoSpy
import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class LocalFavoritesDataSourceTest {

    @get:Rule(order = 1)
    val coroutineRule = CoroutineTestRule()

    private val favorite = RoomFavorite(
        category = "MOVIE_POPULAR",
        id = 42,
        posterPath = "/favorite.jpg",
        title = "Favorite Title",
    )

    @Test
    fun `GIVEN favorite does not exist WHEN toggled THEN favorite is stored and returns true`() =
        coroutineRule.scope.runTest {
            val favoritesDao = FavoritesDaoSpy()
            val sut = LocalFavoritesDataSource(favoritesDao)

            val actual = sut(favorite).getOrNull()

            assertEquals(true, actual)
            assertEquals(favorite, favoritesDao.getFavorite(favorite.id, favorite.category))
        }

    @Test
    fun `GIVEN favorite exists WHEN toggled THEN favorite is removed and returns false`() =
        coroutineRule.scope.runTest {
            val favoritesDao = FavoritesDaoSpy()
            favoritesDao.upsertFavorite(favorite)
            val sut = LocalFavoritesDataSource(favoritesDao)

            val actual = sut(favorite).getOrNull()

            assertEquals(false, actual)
            assertNull(favoritesDao.getFavorite(favorite.id, favorite.category))
        }
}
