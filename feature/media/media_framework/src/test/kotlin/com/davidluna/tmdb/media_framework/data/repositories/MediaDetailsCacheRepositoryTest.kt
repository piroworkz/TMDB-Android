package com.davidluna.tmdb.media_framework.data.repositories

import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import com.davidluna.tmdb.media_framework.data.fakes.fakeCatalog
import com.davidluna.tmdb.media_framework.data.fakes.fakeMediaDetails
import com.davidluna.tmdb.media_framework.data.fakes.fakeRemoteCredits
import com.davidluna.tmdb.media_framework.data.fakes.fakeRemoteError
import com.davidluna.tmdb.media_framework.data.fakes.fakeRemoteImageResponse
import com.davidluna.tmdb.media_framework.data.fakes.fakeRemoteMediaDetail
import com.davidluna.tmdb.media_framework.data.fakes.fakeRoomMediaDetailsRelations
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaDetailsDaoSpy
import com.davidluna.tmdb.media_framework.data.paging.IsCacheExpired
import com.davidluna.tmdb.media_framework.data.remote.services.RemoteMediaServiceSpy
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MediaDetailsCacheRepositoryTest {

    @get:Rule(order = 0)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 1)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var local: MediaDetailsDaoSpy

    @MockK
    private lateinit var remote: RemoteMediaServiceSpy

    @MockK
    private lateinit var isCacheExpired: IsCacheExpired

    private val mediaId = fakeMediaDetails.id

    @Test
    fun `GIVEN sut WHEN is created THEN verify no side effects`() {
        buildSUT()

        verify { local wasNot called }
        verify { remote wasNot called }
        verify { isCacheExpired wasNot called }
    }

    @Test
    fun `GIVEN local data exists AND is not expired WHEN invoke is called THEN returns local MediaDetails`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns fakeRoomMediaDetailsRelations
            coEvery { isCacheExpired(any()) } returns false

            val actual = sut.invoke(fakeCatalog, mediaId).getOrNull()

            assertEquals(fakeMediaDetails, actual)
        }

    @Test
    fun `GIVEN no local data WHEN invoke is called THEN fetches from remote and returns MediaDetails`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns null andThen fakeRoomMediaDetailsRelations
            coEvery { isCacheExpired(any()) } returns false
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteImageResponse.right()
            coEvery { local.cacheDetails(any(), any()) } returns fakeRoomMediaDetailsRelations

            val actual = sut.invoke(fakeCatalog, mediaId).getOrNull()

            assertEquals(fakeMediaDetails, actual)
        }

    @Test
    fun `GIVEN local data exists AND is expired WHEN invoke is called THEN fetches from remote and returns MediaDetails`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns fakeRoomMediaDetailsRelations
            coEvery { isCacheExpired(any()) } returns true
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteImageResponse.right()
            coEvery { local.cacheDetails(any(), any()) } returns fakeRoomMediaDetailsRelations

            val actual = sut.invoke(fakeCatalog, mediaId).getOrNull()

            assertEquals(fakeMediaDetails, actual)
        }

    @Test
    fun `GIVEN no valid local data AND getDetailById fails WHEN invoke is called THEN returns AppError`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeRemoteError.toAppError().left()
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns null andThen fakeRoomMediaDetailsRelations
            coEvery { isCacheExpired(any()) } returns false
            coEvery { remote.getDetailById(any()) } returns fakeRemoteError.left()

            val actual = sut.invoke(fakeCatalog, mediaId)

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN getDetailById succeeds AND getCreditsById fails WHEN invoke is called THEN returns AppError`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeRemoteError.toAppError().left()
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns null
            coEvery { isCacheExpired(any()) } returns false
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteError.left()

            val actual = sut.invoke(fakeCatalog, mediaId)

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN getDetails and getCredits succeed AND getImagesById fails WHEN invoke is called THEN returns AppError`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeRemoteError.toAppError().left()
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns null
            coEvery { isCacheExpired(any()) } returns false
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteError.left()

            val actual = sut.invoke(fakeCatalog, mediaId)

            assertEquals(expected, actual)
        }

    @Test
    fun `GIVEN remote fetch succeeds AND caching fails WHEN invoke is called THEN returns LOCAL_ERROR AppError`() =
        coroutineTestRule.scope.runTest {
            val exception = IllegalStateException("Cache failed")
            val expected = exception.toAppError().left()
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns null
            coEvery { isCacheExpired(any()) } returns false
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteImageResponse.right()
            coEvery { local.cacheDetails(any(), any()) } throws exception

            val actual = sut.invoke(fakeCatalog, mediaId)

            assertEquals(expected, actual)
        }

    private fun buildSUT() = MediaDetailsCacheRepository(
        local = local,
        remote = remote,
        isCacheExpired = isCacheExpired
    )
}