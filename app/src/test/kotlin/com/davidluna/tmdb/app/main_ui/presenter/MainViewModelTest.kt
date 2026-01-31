package com.davidluna.tmdb.app.main_ui.presenter

import app.cash.turbine.test
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ClearAppData
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCatalogSelected
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCloseSession
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ResetAppError
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.UpdateBottomNavItems
import com.davidluna.tmdb.auth_data.data.fakeAccount
import com.davidluna.tmdb.auth_data.data.fakeGuestSession
import com.davidluna.tmdb.auth_data.data.fakeSession
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ClearFavorites
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule(order = 1)
    val mockkRule = MockKRule(this)

    @get:Rule(order = 2)
    val coroutineTestRule = CoroutineTestRule()

    @MockK
    private lateinit var observeUserAccount: ObserveUserAccount

    @MockK
    private lateinit var closeSession: CloseSession

    @MockK
    private lateinit var observeSession: ObserveSession

    @MockK
    private lateinit var updateMediaCatalogUseCase: UpdateSelectedEndpoint

    @MockK
    private lateinit var clearFavorites: ClearFavorites

    @MockK
    private lateinit var validateSession: ValidateSession

    private val initialState = MainViewModel.State()

    @Test
    fun `GIVEN initial State WHEN state flow has no subscribers THEN state value is the initial value`() {
        assertEquals(initialState, buildSUT().state.value)
    }

    @Test
    fun `GIVEN a valid guest session WHEN state flow has at least one subscriber THEN _state should be updated with guest session`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeGuestSession

            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(expected)
            coEvery { validateSession.isValid() } returns true
            val sut = buildSUT()

            sut.state.test {
                awaitItem()

                val actual = awaitItem().session

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN a valid session AND user account WHEN state flow has at least one subscriber THEN _state should be updated with guest session`() =
        coroutineTestRule.scope.runTest {
            val expectedSession = fakeSession
            val expectedAccount = fakeAccount

            every { observeUserAccount.userAccount } returns flowOf(expectedAccount)
            every { observeSession.session } returns flowOf(expectedSession)
            val sut = buildSUT()

            sut.state.test {
                awaitItem()

                val actual = awaitItem()

                assertEquals(expectedSession, actual.session)
                assertEquals(expectedAccount, actual.userAccount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN session flow throws exception WHEN collected THEN _state should be updated with appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException()

            every { observeSession.session } returns flow { throw expected }
            every { observeUserAccount.userAccount } returns flowOf(null)
            val sut = buildSUT()

            sut.state.test {
                awaitItem()

                val actual = awaitItem().appError

                assertEquals(expected.toAppError(), actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN userAccount flow throws exception WHEN collected THEN _state should be updated with appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException()

            every { observeSession.session } returns flowOf(fakeSession)
            every { observeUserAccount.userAccount } returns flow { throw expected }
            val sut = buildSUT()

            sut.state.test {
                awaitItem()

                val actual = awaitItem().appError

                assertEquals(expected.toAppError(), actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN OnCatalogSelected event WHEN updateMediaCatalog is called THEN _state should be updated selectedCatalog`() =
        coroutineTestRule.scope.runTest {
            val expected = Catalog.MOVIE_POPULAR
            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(null)
            coEvery { updateMediaCatalogUseCase.update(expected) } returns null
            val sut = buildSUT()

            sut.onEvent(OnCatalogSelected(expected))

            sut.state.test {
                awaitItem()
                val actual = awaitItem().selectedCatalog

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN OnCatalogSelected event WHEN updateMediaCatalog fails THEN _state should be updated with appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException().toAppError()
            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(null)
            coEvery { updateMediaCatalogUseCase.update(any()) } returns expected
            val sut = buildSUT()

            sut.onEvent(OnCatalogSelected(Catalog.MOVIE_POPULAR))

            sut.state.test {
                awaitItem()
                val actual = awaitItem().appError

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN appError is not null WHEN ResetAppError is handled THEN _state should be updated with appError as null`() =
        coroutineTestRule.scope.runTest {
            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(null)
            coEvery { updateMediaCatalogUseCase.update(any()) } returns IllegalStateException().toAppError()

            val sut = buildSUT()
            sut.onEvent(OnCatalogSelected(Catalog.MOVIE_POPULAR))

            sut.state.test {
                skipItems(2)
                sut.onEvent(ResetAppError)
                val actual = awaitItem().appError

                assertNull(actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN UpdateBottomNavItems event WHEN event is handled THEN _state should be updated with new bottomNavItems`() =
        coroutineTestRule.scope.runTest {
            val expected = MediaType.TV_SHOW.bottomBarItems()
            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(null)

            val sut = buildSUT()
            sut.onEvent(UpdateBottomNavItems(expected))

            sut.state.test {
                awaitItem()
                val actual = awaitItem().bottomNavItems

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN event is handled THEN _state should be updated with new bottomNavItems`() =
        coroutineTestRule.scope.runTest {

            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(null)
            coEvery { closeSession.close(any()) } returns null

            val sut = buildSUT()
            sut.onEvent(OnCloseSession)

            sut.state.test {
                awaitItem()
                val actual = awaitItem().finishActivity

                assertTrue(actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN closeSession fails THEN _state should be updated with appError`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException().toAppError()
            every { observeUserAccount.userAccount } returns flowOf(null)
            every { observeSession.session } returns flowOf(null)
            coEvery { closeSession.close(any()) } returns expected

            val sut = buildSUT()
            sut.onEvent(OnCloseSession)

            sut.state.test {
                awaitItem()
                val actual = awaitItem().appError

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN ClearAppData event WHEN clearFavorites fails THEN _state should update appError as null`() =
        coroutineTestRule.scope.runTest {
            val expected = IllegalStateException().toAppError()
            every { observeUserAccount.userAccount } returns flowOf(fakeAccount)
            every { observeSession.session } returns flowOf(fakeSession)
            coEvery { clearFavorites.clear() } returns expected

            val sut = buildSUT()
            sut.onEvent(ClearAppData)

            sut.state.test {
                awaitItem()
                val actual = awaitItem().appError

                assertEquals(expected, actual)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildSUT() = MainViewModel(
        clearFavorites = clearFavorites,
        closeSession = closeSession,
        ioDispatcher = coroutineTestRule.dispatcher,
        observeSession = observeSession,
        observeUserAccount = observeUserAccount,
        updateSelectedEndpoint = updateMediaCatalogUseCase,
        validateSession = validateSession
    )
}
