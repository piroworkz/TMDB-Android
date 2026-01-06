package com.davidluna.tmdb.app.main_ui.presenter

import app.cash.turbine.test
import com.davidluna.tmdb.app.main_ui.fakes.fakeAppError
import com.davidluna.tmdb.app.main_ui.fakes.fakeUserAccount
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import com.davidluna.tmdb.test_shared.rules.CoroutineTestRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

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
    private lateinit var updateMediaCatalogUseCase: UpdateSelectedEndpoint

    private val initialState = MainViewModel.State()

    @Test
    fun `GIVEN initial state WHEN MainViewModel is created THEN state should be the initial one`() =
        coroutineTestRule.scope.runTest {
            every { observeUserAccount.userAccount } returns flowOf(null)
            val sut = buildSUT()

            sut.state.test {
                val actual = awaitItem()
                assertEquals(initialState, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN no user account WHEN userAccount is observed THEN initial value should be null`() =
        coroutineTestRule.scope.runTest {
            every { observeUserAccount.userAccount } returns flowOf(null)
            val sut = buildSUT()

            sut.userAccount.test {
                val actual = awaitItem()
                assertNull(actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN a UserAccount WHEN getUserAccountUseCase is successful THEN userAccount StateFlow should emit it`() =
        coroutineTestRule.scope.runTest {
            val expected = fakeUserAccount

            every { observeUserAccount.userAccount } returns flowOf(expected)
            val sut = buildSUT()

            sut.userAccount.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN an exception WHEN getUserAccountUseCase throws an exception THEN _state should be updated with AppError and userAccount should remain null`() =
        coroutineTestRule.scope.runTest {
            val exception = IllegalStateException("Something went wrong")
            val expected = initialState.copy(
                appError = exception.toAppError()
            )

            every { observeUserAccount.userAccount } returns flow { throw exception }
            val sut = buildSUT()

            val userAccountJob = launch { sut.userAccount.collect {} }

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                assertNull(sut.userAccount.value)
                cancel()
            }

            userAccountJob.cancel()
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN closeSessionUseCase is successful THEN _state should be updated with isSessionClosed = true`() =
        coroutineTestRule.scope.runTest {
            val expected = initialState.copy(isSessionClosed = true)

            every { observeUserAccount.userAccount } returns flowOf(null)
            coEvery { closeSession.close() } returns null
            val sut = buildSUT()
            sut.onEvent(MainEvent.OnCloseSession)

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN closeSessionUseCase fails THEN _state should not be be updated`() =
        coroutineTestRule.scope.runTest {
            val expected = initialState

            every { observeUserAccount.userAccount } returns flowOf(null)
            coEvery { closeSession.close() } returns fakeAppError
            val sut = buildSUT()
            sut.onEvent(MainEvent.OnCloseSession)

            sut.state.test {
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN OnCloseSession event WHEN closeSessionUseCase returns an error THEN _state should be updated with AppError`() =
        coroutineTestRule.scope.runTest {
            val expected = initialState.copy(appError = fakeAppError)

            every { observeUserAccount.userAccount } returns flowOf(null)
            coEvery { closeSession.close() } returns fakeAppError
            val sut = buildSUT()
            sut.onEvent(MainEvent.OnCloseSession)

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN UpdateBottomNavItems event with new Endpoints WHEN onEvent is called THEN _state should be updated with new bottomNavItems`() =
        coroutineTestRule.scope.runTest {
            val bottomNavItems = MediaType.TV_SHOW.bottomBarItems()
            val expected = initialState.copy(bottomNavItems = bottomNavItems)

            every { observeUserAccount.userAccount } returns flowOf(null)
            val sut = buildSUT()
            sut.onEvent(MainEvent.UpdateBottomNavItems(bottomNavItems))

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN OnCatalogSelected event WHEN updateMediaCatalogUseCase succeeds THEN _state should be updated with new selectedCatalog`() =
        coroutineTestRule.scope.runTest {
            val selectedCatalog = Catalog.MOVIE_POPULAR
            val expected = initialState.copy(selectedCatalog = selectedCatalog)

            every { observeUserAccount.userAccount } returns flowOf(null)
            coEvery { updateMediaCatalogUseCase.update(any()) } returns null

            val sut = buildSUT()
            sut.onEvent(MainEvent.OnCatalogSelected(selectedCatalog))

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()

                assertEquals(expected, actual)
                cancel()
            }
        }

    @Test
    fun `GIVEN OnCatalogSelected event WHEN updateMediaCatalogUseCase returns an error THEN _state should be updated with AppError and selectedCatalog should remain unchanged`() =
        coroutineTestRule.scope.runTest {

            val selectedCatalog = Catalog.MOVIE_POPULAR
            val expected =
                initialState.copy(appError = fakeAppError, selectedCatalog = selectedCatalog)

            every { observeUserAccount.userAccount } returns flowOf(null)
            coEvery { updateMediaCatalogUseCase.update(any()) } returns fakeAppError

            val sut = buildSUT()
            sut.onEvent(MainEvent.OnCatalogSelected(selectedCatalog))

            sut.state.test {
                skipItems(1)
                val actual = awaitItem()
                assertEquals(expected, actual)
                assertEquals(expected.selectedCatalog, actual.selectedCatalog)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN _state appError is not null WHEN ResetAppError event THEN _state should be updated with appError = null`() =
        coroutineTestRule.scope.runTest {
            every { observeUserAccount.userAccount } returns flowOf(null)
            coEvery { closeSession.close() } returns fakeAppError

            val sut = buildSUT()
            sut.onEvent(MainEvent.OnCloseSession)

            sut.state.test {
                skipItems(2)
                sut.onEvent(MainEvent.ResetAppError)
                val actual = awaitItem().appError

                assertNull(actual)
                cancel()
            }
        }

    private fun buildSUT() = MainViewModel(
        observeUserAccount = observeUserAccount,
        closeSession = closeSession,
        ioDispatcher = coroutineTestRule.dispatcher,
        updateSelectedEndpoint = updateMediaCatalogUseCase
    )
}
