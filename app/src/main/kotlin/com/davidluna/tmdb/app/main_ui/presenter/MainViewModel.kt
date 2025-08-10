package com.davidluna.tmdb.app.main_ui.presenter

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCatalogSelected
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCloseSession
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ResetAppError
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.UpdateBottomNavItems
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.auth_domain.usecases.CloseSessionUseCase
import com.davidluna.tmdb.auth_domain.usecases.GetUserAccountUseCase
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getUserAccountUseCase: GetUserAccountUseCase,
    private val closeSessionUseCase: CloseSessionUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val updateMediaCatalogUseCase: UpdateSelectedEndpoint,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = State()
        )

    val userAccount: StateFlow<UserAccount?> = getUserAccountUseCase()
        .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    @Stable
    data class State(
        val appError: AppError? = null,
        val isSessionClosed: Boolean = false,
        val bottomNavItems: List<Catalog> = MediaType.MOVIE.bottomBarItems(),
        val selectedCatalog: Catalog = Catalog.MOVIE_NOW_PLAYING,
    )

    fun onEvent(event: MainEvent) = when (event) {
        OnCloseSession -> closeSession()
        is UpdateBottomNavItems -> updateBottomNavItems(event.bottomNavItems)
        is OnCatalogSelected -> updateSelectedCatalog(event.endpoint)
        ResetAppError -> _state.update { it.copy(appError = null) }
    }

    private fun updateSelectedCatalog(endpoint: Catalog) {
        viewModelScope.launch(ioDispatcher) {
            updateMediaCatalogUseCase(endpoint)
                .fold(
                    ifLeft = { e -> _state.update { it.copy(appError = e) } },
                    ifRight = { success ->
                        if (success) {
                            _state.update { it.copy(selectedCatalog = endpoint) }
                        }
                    }
                )
        }
    }

    private fun updateBottomNavItems(bottomNavItems: List<Catalog>) {
        _state.update { it.copy(bottomNavItems = bottomNavItems) }
    }

    private fun closeSession() {
        viewModelScope.launch(ioDispatcher) {
            closeSessionUseCase().fold(
                ifLeft = { e -> _state.update { it.copy(appError = e) } },
                ifRight = { s -> _state.update { it.copy(isSessionClosed = s) } }
            )
        }
    }
}