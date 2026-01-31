package com.davidluna.tmdb.app.main_ui.presenter

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ClearAppData
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCatalogSelected
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCloseSession
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ResetAppError
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.UpdateBottomNavItems
import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ClearFavorites
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val clearFavorites: ClearFavorites,
    private val closeSession: CloseSession,
    private val ioDispatcher: CoroutineDispatcher,
    private val observeSession: ObserveSession,
    private val observeUserAccount: ObserveUserAccount,
    private val updateSelectedEndpoint: UpdateSelectedEndpoint,
    private val validateSession: ValidateSession
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = initState()
        )

    @Stable
    data class State(
        val appError: AppError? = null,
        val bottomNavItems: List<Catalog> = MediaType.MOVIE.bottomBarItems(),
        val finishActivity: Boolean = false,
        val isSessionClosed: Boolean = false,
        val selectedCatalog: Catalog = Catalog.MOVIE_NOW_PLAYING,
        val session: Session? = null,
        val userAccount: UserAccount? = null,
    )

    fun onEvent(event: MainEvent) = when (event) {
        ClearAppData -> onClearAppData()
        OnCloseSession -> endSession()
        ResetAppError -> _state.update { it.copy(appError = null) }
        is OnCatalogSelected -> selectCatalog(event.endpoint)
        is UpdateBottomNavItems -> updateBottomNavItems(event.bottomNavItems)
    }

    private fun onClearAppData() {
        viewModelScope.launch(ioDispatcher) {
            _state.update { it.copy(appError = clearFavorites.clear()) }
        }
    }

    private fun selectCatalog(catalog: Catalog) {
        viewModelScope.launch(ioDispatcher) {
            val result = updateSelectedEndpoint.update(catalog)
            _state.update { it.copy(appError = result, selectedCatalog = catalog) }
        }
    }

    private fun updateBottomNavItems(bottomNavItems: List<Catalog>) {
        _state.update { it.copy(bottomNavItems = bottomNavItems) }
    }

    private fun initState(): State {
        viewModelScope.launch(ioDispatcher) {
            observeSession.session
                .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
                .collect { session ->
                    val isSessionClosed: Boolean = when {
                        session == null -> true
                        session.isGuest -> !validateSession.isValid()
                        else -> false
                    }
                    _state.update { it.copy(isSessionClosed = it.finishActivity && isSessionClosed, session = session) }
                }
        }
        viewModelScope.launch(ioDispatcher) {
            observeUserAccount.userAccount
                .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
                .collect { userAccount ->
                    _state.update { it.copy(userAccount = userAccount) }
                }
        }
        return State()
    }

    private fun endSession() {
        viewModelScope.launch(ioDispatcher) {
            val result = closeSession.close(_state.value.session?.isGuest == true)
            _state.update { it.copy(appError = result, finishActivity = result == null) }
        }
    }
}