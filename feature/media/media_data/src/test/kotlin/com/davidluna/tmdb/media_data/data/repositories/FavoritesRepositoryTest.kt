package com.davidluna.tmdb.media_data.data.repositories

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import androidx.sqlite.SQLiteException
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_data.framework.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.framework.local.database.mappers.toDomain
import com.davidluna.tmdb.media_data.repositories.FavoritesRepository
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class FavoritesRepositoryTest {

    @get:Rule(order = 0)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 1)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var favoritesDao: FavoritesDao

    @Test
    fun `GIVEN sut WHEN created THEN no side effects happen`() {
        buildSUT()

        verify(exactly = 0) { favoritesDao wasNot called }
    }

    @Test
    fun `GIVEN favorites in db WHEN observe is called THEN emits mapped favorites`() =
        coroutineTestRule.scope.runTest {
            val roomMedia = listOf(
                RoomMedia(
                    category = "MOVIE_POPULAR",
                    id = 1,
                    posterPath = "poster",
                    title = "title",
                    isFavorite = true
                )
            )
            val expected: List<Media> = roomMedia.map { it.toDomain() }
            val sut = buildSUT()

            every { favoritesDao.getFavorites("MOVIE_%") } returns buildFakePagingSource(roomMedia)

            val actual = sut.observe(MediaType.MOVIE, backgroundScope).asSnapshot()

            assertEquals(expected, actual)
            verify(exactly = 1) { favoritesDao.getFavorites("MOVIE_%") }
        }

    @Test
    fun `GIVEN toggle is requested WHEN dao succeeds THEN returns null`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { favoritesDao.toggleFavorite(1, "TV_%") } returns Unit

            val actual = sut.toggle(1, MediaType.TV_SHOW)

            assertNull(actual)
        }

    @Test
    fun `GIVEN toggle throws exception WHEN toggled THEN returns app error`() =
        coroutineTestRule.scope.runTest {
            val expected = SQLiteException().toAppError()
            val sut = buildSUT()

            coEvery { favoritesDao.toggleFavorite(1, "MOVIE_%") } throws SQLiteException()

            val actual = sut.toggle(1, MediaType.MOVIE)

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN clear favorites WHEN dao throws exception THEN returns app error`() =
        coroutineTestRule.scope.runTest {
            val expected = SQLiteException().toAppError()
            val sut = buildSUT()

            coEvery { favoritesDao.clearFavorites() } throws SQLiteException()

            val actual = sut.clear()

            assertEquals(expected, actual)
        }

    private fun buildSUT(): FavoritesRepository =
        FavoritesRepository(favoritesDao = favoritesDao)

    private fun buildFakePagingSource(
        roomMedia: List<RoomMedia> = emptyList(),
        loadResult: LoadResult<Int, RoomMedia> = LoadResult.Page(
            data = roomMedia,
            prevKey = null,
            nextKey = null
        ),
    ): PagingSource<Int, RoomMedia> = object : PagingSource<Int, RoomMedia>() {
        override fun getRefreshKey(state: PagingState<Int, RoomMedia>): Int? = null
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RoomMedia> = loadResult
    }
}
