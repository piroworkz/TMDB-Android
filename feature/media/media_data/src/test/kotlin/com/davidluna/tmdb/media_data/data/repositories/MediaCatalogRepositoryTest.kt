package com.davidluna.tmdb.media_data.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.InitializeAction
import androidx.paging.RemoteMediator.MediatorResult.Success
import androidx.paging.testing.asSnapshot
import com.davidluna.tmdb.media_data.data.fakes.fakeCatalog
import com.davidluna.tmdb.media_data.data.fakes.fakeRoomMediaList
import com.davidluna.tmdb.media_data.data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.data.framework.paging.MediaCatalogRemoteMediator
import com.davidluna.tmdb.media_data.data.framework.remote.model.toEndpointPath
import com.davidluna.tmdb.media_data.di.MediaCatalogMediatorFactory
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class MediaCatalogRepositoryTest {

    @get:Rule(order = 0)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 1)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var mediaDao: MediaDao

    @MockK
    private lateinit var mediatorFactory: MediaCatalogMediatorFactory

    @MockK
    private lateinit var remoteMediator: MediaCatalogRemoteMediator

    @Test
    fun `GIVEN sut WHEN created THEN no side effects happen`() {
        buildSUT()

        verify(exactly = 0) { mediaDao wasNot called }
        verify(exactly = 0) { mediatorFactory wasNot called }
        verify(exactly = 0) { remoteMediator wasNot called }
    }

    @Test
    fun `GIVEN any catalog WHEN repository is invoked THEN data is fetched`() =
        coroutineTestRule.scope.runTest {
            val expected: List<Media> = fakeRoomMediaList.map { it.toDomain() }
            val sut = buildSUT()

            coEvery { mediaDao.getMedia(fakeCatalog.name) } returns
                    buildFakePagingSource(fakeRoomMediaList)
            every {
                mediatorFactory.create(fakeCatalog.toEndpointPath(), fakeCatalog.name)
            } returns remoteMediator
            coEvery { remoteMediator.initialize() } returns InitializeAction.SKIP_INITIAL_REFRESH
            coEvery { remoteMediator.load(any(), any()) } returns Success(true)
            val actual = sut.invoke(fakeCatalog, backgroundScope).asSnapshot()

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN MediatorFactory create throws exception WHEN repository is invoked THEN flow emits error`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("MediatorFactory failed")
            val sut = buildSUT()

            coEvery { mediaDao.getMedia(fakeCatalog.name) } returns
                    buildFakePagingSource(fakeRoomMediaList)
            every {
                mediatorFactory.create(fakeCatalog.toEndpointPath(), fakeCatalog.name)
            } throws expected

            val actual = runCatching { sut.invoke(fakeCatalog, backgroundScope).collect() }

            assertTrue(actual.isFailure)
            assertEquals(expected, actual.exceptionOrNull())
        }

    @Test
    fun `GIVEN RemoteMediator load returns Error WHEN repository is invoked THEN flow emits error`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException("PagingSourceFactory failed")
            val sut = buildSUT()

            coEvery { mediaDao.getMedia(fakeCatalog.name) } returns buildFakePagingSource()
            every {
                mediatorFactory.create(fakeCatalog.toEndpointPath(), fakeCatalog.name)
            } returns remoteMediator
            coEvery { remoteMediator.initialize() } returns InitializeAction.LAUNCH_INITIAL_REFRESH
            coEvery {
                remoteMediator.load(any(), any())
            } returns RemoteMediator.MediatorResult.Error(expected)

            val actual = runCatching { sut.invoke(fakeCatalog, backgroundScope).asSnapshot() }

            assertTrue(actual.isFailure)
            assertEquals(expected.message, actual.exceptionOrNull()?.message)
        }

    private fun buildSUT(): MediaCatalogRepository =
        MediaCatalogRepository(
            mediaDao = mediaDao,
            mediatorFactory = mediatorFactory
        )

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

    private fun RoomMedia.toDomain(): Media {
        return Media(
            id = id,
            posterPath = posterPath,
            title = title
        )
    }

}
