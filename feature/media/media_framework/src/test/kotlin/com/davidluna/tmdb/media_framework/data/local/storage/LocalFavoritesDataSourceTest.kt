package com.davidluna.tmdb.media_framework.data.local.storage

import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite
import com.davidluna.tmdb.media_framework.data.local.database.dao.FavoritesDao
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LocalFavoritesDataSourceTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineRule = CoroutineTestRule()

    @MockK
    private lateinit var favoritesDao: FavoritesDao

    private val favorite = RoomFavorite(
        category = "MOVIE_POPULAR",
        id = 42,
        posterPath = "/favorite.jpg",
        title = "Favorite Title",
    )

    @Test
    fun `GIVEN favorite does not exist WHEN toggled THEN favorite is stored and returns true`() =
        coroutineRule.scope.runTest {
            coEvery { favoritesDao.getFavorite(favorite.id, favorite.category) } returns null
            coEvery { favoritesDao.upsertFavorite(favorite) } returns Unit
            val sut = LocalFavoritesDataSource(favoritesDao)

            val actual = sut.toggleFavorite(favorite).getOrNull()

            assertEquals(true, actual)
            coVerify(exactly = 1) { favoritesDao.upsertFavorite(favorite) }
            coVerify(exactly = 0) { favoritesDao.deleteFavorite(favorite.id, favorite.category) }
        }

    @Test
    fun `GIVEN favorite exists WHEN toggled THEN favorite is removed and returns false`() =
        coroutineRule.scope.runTest {
            coEvery { favoritesDao.getFavorite(favorite.id, favorite.category) } returns favorite
            coEvery { favoritesDao.deleteFavorite(favorite.id, favorite.category) } returns 1
            val sut = LocalFavoritesDataSource(favoritesDao)

            val actual = sut.toggleFavorite(favorite).getOrNull()

            assertEquals(false, actual)
            coVerify(exactly = 1) { favoritesDao.deleteFavorite(favorite.id, favorite.category) }
            coVerify(exactly = 0) { favoritesDao.upsertFavorite(favorite) }
        }
}
