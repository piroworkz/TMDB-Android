package com.davidluna.tmdb.media_data.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import arrow.core.right
import com.davidluna.tmdb.media_data.data.fakes.fakeCatalog
import com.davidluna.tmdb.media_data.data.fakes.fakeRemoteKey
import com.davidluna.tmdb.media_data.data.fakes.fakeRemoteResults
import com.davidluna.tmdb.media_data.data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.data.framework.local.database.dao.RemoteKeysDao
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.media_data.data.framework.paging.MediaCatalogRemoteMediator
import com.davidluna.tmdb.media_data.data.framework.remote.model.toEndpointPath
import com.davidluna.tmdb.media_data.data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class EndpointRemoteMediatorTest {
    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var mediaDao: MediaDao

    @MockK
    private lateinit var mediaService: RemoteMediaService

    @MockK
    private lateinit var remoteKeysDao: RemoteKeysDao

    @MockK
    private lateinit var isCacheExpired: IsCacheExpired

    @Test
    fun `GIVEN sut WHEN created THEN no side effects happen`() {
        buildSUT()

        coVerify(exactly = 0) { mediaDao wasNot called }
        coVerify(exactly = 0) { remoteKeysDao wasNot called }
        coVerify(exactly = 0) { mediaService wasNot called }
        coVerify(exactly = 0) { isCacheExpired wasNot called }
    }

    @Test
    fun `GIVEN refresh load WHEN successful response THEN insert and return success`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { remoteKeysDao.getRemoteKey(fakeCatalog.name) } returns null
            coEvery { mediaService.getMediaCatalog(any(), any()) } returns fakeRemoteResults.right()
            coEvery { remoteKeysDao.clearRemoteKey(fakeCatalog.name) } returns Unit
            coEvery { mediaDao.deleteCatalog(fakeCatalog.name) } returns Unit
            coEvery { remoteKeysDao.insertKey(any()) } returns Unit
            coEvery { mediaDao.insertMedia(any()) } returns Unit

            val result: RemoteMediator.MediatorResult =
                sut.load(LoadType.REFRESH, fakePagingState())

            assertTrue(result is RemoteMediator.MediatorResult.Success)
            coVerify { mediaDao.insertMedia(any()) }
            coVerify { remoteKeysDao.insertKey(any()) }
        }

    @Test
    fun `GIVEN append load AND end of pagination reached THEN return success with endOfPagination true`() =
        runTest {
            val sut = buildSUT()
            coEvery {
                remoteKeysDao.getRemoteKey(fakeCatalog.name)
            } returns fakeRemoteKey.copy(reachedEndOfPagination = true)

            val result = sut.load(LoadType.APPEND, fakePagingState())

            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

    @Test
    fun `GIVEN append load AND has more pages THEN fetch and insert`() = runTest {
        val sut = buildSUT()
        coEvery { remoteKeysDao.getRemoteKey(fakeCatalog.name) } returns fakeRemoteKey.copy(
            lastPage = 1,
            reachedEndOfPagination = false
        )
        coEvery { mediaService.getMediaCatalog(any(), any()) } returns fakeRemoteResults.right()
        coEvery { remoteKeysDao.insertKey(any()) } returns Unit
        coEvery { mediaDao.insertMedia(any()) } returns Unit

        val result = sut.load(LoadType.APPEND, fakePagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        coVerify { mediaDao.insertMedia(any()) }
        coVerify { remoteKeysDao.insertKey(any()) }
    }

    @Test
    fun `GIVEN prepend load THEN return success with endOfPagination true`() = runTest {
        val sut = buildSUT()

        coEvery { remoteKeysDao.getRemoteKey(fakeCatalog.name) } returns fakeRemoteKey
        val result = sut.load(LoadType.PREPEND, fakePagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `GIVEN expired cache WHEN initialize THEN returns launch initial refresh`() = runTest {
        val sut = buildSUT()
        coEvery { remoteKeysDao.getRemoteKey(fakeCatalog.name) } returns fakeRemoteKey.copy(savedOnTimeMillis = 0)
        coEvery { isCacheExpired(any()) } returns true

        val result = sut.initialize()

        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }

    @Test
    fun `GIVEN valid cache WHEN initialize THEN returns skip initial refresh`() = runTest {
        val sut = buildSUT()
        coEvery { remoteKeysDao.getRemoteKey(fakeCatalog.name) } returns fakeRemoteKey
        coEvery { isCacheExpired(any()) } returns false

        val result = sut.initialize()

        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, result)
    }

    @Test
    fun `GIVEN exception WHEN load THEN return MediatorResult_Error`() = runTest {
        val sut = buildSUT()
        coEvery { remoteKeysDao.getRemoteKey(fakeCatalog.name) } returns null
        coEvery { mediaService.getMediaCatalog(any(), any()) } throws RuntimeException("BOOM")

        val result = sut.load(LoadType.REFRESH, fakePagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    private fun buildSUT(
        path: String = fakeCatalog.toEndpointPath(),
        catalogName: String = fakeCatalog.name,
    ): RemoteMediator<Int, RoomMedia> = MediaCatalogRemoteMediator(
        path = path,
        catalogName = catalogName,
        mediaDao = mediaDao,
        mediaService = mediaService,
        remoteKeysDao = remoteKeysDao,
        isCacheExpired = isCacheExpired
    )

    private fun fakePagingState(): PagingState<Int, RoomMedia> = PagingState(
        pages = emptyList(),
        anchorPosition = null,
        config = androidx.paging.PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0
    )
}