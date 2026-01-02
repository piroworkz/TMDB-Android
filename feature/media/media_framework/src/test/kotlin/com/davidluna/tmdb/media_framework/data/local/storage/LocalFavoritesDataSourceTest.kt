package com.davidluna.tmdb.media_framework.data.local.storage

import com.davidluna.tmdb.media_framework.data.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.entities.MediaType
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

    private val favoriteItem = FavoriteItem(
        id = 42,
        posterPath = "/favorite.jpg",
        title = "Favorite Title",
        mediaType = MediaType.MOVIE,
    )

    private val roomFavorite = RoomFavorite(
        category = favoriteItem.mediaType.name,
        id = favoriteItem.id,
        posterPath = favoriteItem.posterPath,
        title = favoriteItem.title,
    )

    @Test
    fun `GIVEN favorite does not exist WHEN toggled THEN favorite is stored and returns true`() =
        coroutineRule.scope.runTest {
            coEvery { favoritesDao.getFavorite(roomFavorite.id, roomFavorite.category) } returns null
            coEvery { favoritesDao.upsertFavorite(roomFavorite) } returns Unit
            val sut = LocalFavoritesDataSource(favoritesDao)

            val actual = sut.invoke(favoriteItem).getOrNull()

            assertEquals(true, actual)
            coVerify(exactly = 1) { favoritesDao.upsertFavorite(roomFavorite) }
            coVerify(exactly = 0) {
                favoritesDao.deleteFavorite(roomFavorite.id, roomFavorite.category)
            }
        }

    @Test
    fun `GIVEN favorite exists WHEN toggled THEN favorite is removed and returns false`() =
        coroutineRule.scope.runTest {
            coEvery {
                favoritesDao.getFavorite(roomFavorite.id, roomFavorite.category)
            } returns roomFavorite
            coEvery {
                favoritesDao.deleteFavorite(roomFavorite.id, roomFavorite.category)
            } returns 1
            val sut = LocalFavoritesDataSource(favoritesDao)

            val actual = sut.invoke(favoriteItem).getOrNull()

            assertEquals(false, actual)
            coVerify(exactly = 1) {
                favoritesDao.deleteFavorite(roomFavorite.id, roomFavorite.category)
            }
            coVerify(exactly = 0) { favoritesDao.upsertFavorite(roomFavorite) }
        }
}
