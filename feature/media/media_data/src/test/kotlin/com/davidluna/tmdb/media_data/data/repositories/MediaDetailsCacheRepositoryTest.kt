package com.davidluna.tmdb.media_data.data.repositories

import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.core_data.framework.remote.model.toAppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.media_data.fakes.fakeCatalog
import com.davidluna.tmdb.media_data.fakes.fakeMediaDetails
import com.davidluna.tmdb.media_data.fakes.fakeRemoteCredits
import com.davidluna.tmdb.media_data.fakes.fakeRemoteError
import com.davidluna.tmdb.media_data.fakes.fakeRemoteImages
import com.davidluna.tmdb.media_data.fakes.fakeRemoteMediaDetail
import com.davidluna.tmdb.media_data.fakes.fakeRoomMediaDetailsRelations
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDetailsDao
import com.davidluna.tmdb.media_data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.media_data.framework.remote.services.RemoteMediaService
import com.davidluna.tmdb.media_data.repositories.MediaDetailsCacheRepository
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
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
    private lateinit var local: MediaDetailsDao

    @MockK
    private lateinit var remote: RemoteMediaService

    @MockK
    private lateinit var isCacheExpired: IsCacheExpired

    @MockK
    private lateinit var observeCountryCode: ObserveCountryCode

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
            every { observeCountryCode.invoke() } returns flowOf("US")

            val actual = sut.invoke(fakeCatalog, mediaId).getOrNull()

            assertEquals(fakeMediaDetails, actual)
        }

    @Test
    fun `GIVEN no local data WHEN invoke is called THEN fetches from remote and returns MediaDetails`() =
        coroutineTestRule.scope.runTest {
            val sut = buildSUT()

            coEvery { local.getFullDetail(mediaId) } returns null andThen fakeRoomMediaDetailsRelations
            coEvery { isCacheExpired(any()) } returns false
            every { observeCountryCode.invoke() } returns flowOf("US")
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteImages.right()
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
            every { observeCountryCode.invoke() } returns flowOf("US")
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteImages.right()
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
            every { observeCountryCode.invoke() } returns flowOf("US")
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
            every { observeCountryCode.invoke() } returns flowOf("US")
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
            every { observeCountryCode.invoke() } returns flowOf("US")
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
            every { observeCountryCode.invoke() } returns flowOf("US")
            coEvery { remote.getDetailById(any()) } returns fakeRemoteMediaDetail.right()
            coEvery { remote.getCreditsById(any()) } returns fakeRemoteCredits.right()
            coEvery { remote.getImagesById(any()) } returns fakeRemoteImages.right()
            coEvery { local.cacheDetails(any(), any()) } throws exception

            val actual = sut.invoke(fakeCatalog, mediaId)

            assertEquals(expected, actual)
        }

    private fun buildSUT() = MediaDetailsCacheRepository(
        local = local,
        remote = remote,
        isCacheExpired = isCacheExpired,
        observeCountryCode = observeCountryCode
    )
}