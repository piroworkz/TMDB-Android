package com.davidluna.tmdb.auth_ui.presenter.splash

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.IsGuestSessionValid
import com.davidluna.tmdb.auth_ui.view.splash.holder.CurrentScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val isGuestSessionValid: IsGuestSessionValid,
    private val observeSession: ObserveSession,
) : ViewModel() {

    val isLoggedIn: MutableState<Boolean?> = mutableStateOf(null)

    private val _currentScreen = MutableStateFlow(CurrentScreen.SPLASH)
    val state = _currentScreen
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CurrentScreen.SPLASH
        )

    fun updateCurrentScreen(screen: CurrentScreen) {
        _currentScreen.update { screen }
    }

    fun checkSessionStatus() {
        viewModelScope.launch(ioDispatcher) {
            val session = observeSession().first()
            val isSessionValid: Boolean = when {
                session == null -> false
                session.isGuest -> isGuestSessionValid(session.expiresAt)
                else -> true
            }
            isLoggedIn.value = isSessionValid
        }
    }
}

