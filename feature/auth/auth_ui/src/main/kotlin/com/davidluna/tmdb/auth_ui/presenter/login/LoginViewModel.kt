package com.davidluna.tmdb.auth_ui.presenter.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.auth_domain.entities.LoginRequest
import com.davidluna.tmdb.auth_domain.entities.TextInputType.PASSWORD
import com.davidluna.tmdb.auth_domain.entities.TextInputType.USERNAME
import com.davidluna.tmdb.auth_domain.usecases.CloseSessionUseCase
import com.davidluna.tmdb.auth_domain.usecases.LoginAsGuest
import com.davidluna.tmdb.auth_domain.usecases.LoginWithCredentials
import com.davidluna.tmdb.auth_domain.usecases.ValidateInputUseCase
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.GuestButtonClicked
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.LoginButtonClicked
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.SetPassword
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent.SetUsername
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val closeSessionUseCase: CloseSessionUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val loginGuest: LoginAsGuest,
    private val loginUser: LoginWithCredentials,
    private val validateInput: ValidateInputUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = State()
    )

    @Stable
    data class State(
        val isLoading: Boolean = false,
        val appError: AppError? = null,
        val username: String = String(),
        val password: String = String(),
        val usernameError: String? = null,
        val passwordError: String? = null,
        val isLoggedIn: Boolean = false,
    )

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.ResetAppError -> _state.update { it.copy(appError = null) }
            GuestButtonClicked -> guestLogin()
            is LoginButtonClicked -> login(event.username, event.password)
            is SetPassword -> setPassword(password = event.password)
            is SetUsername -> setUsername(username = event.username)
        }
    }

    private suspend fun closeSession(): Boolean = closeSessionUseCase()
        .fold(
            ifLeft = {
                _state.update { s -> s.copy(appError = it.toAppError()) }
                false
            },
            ifRight = { true }
        )

    private fun login(username: String, password: String) = launchOnIO {
        if (validateLoginForm() && closeSession()) {
            loginUser(LoginRequest(username, password)).fold(
                ifLeft = { appError -> _state.update { s -> s.copy(appError = appError) } },
                ifRight = { _ -> _state.update { s -> s.copy(isLoggedIn = true) } }
            )
        }
    }

    private fun setPassword(password: String?) {
        val passwordError = validateInput(password, PASSWORD)
        _state.update {
            it.copy(
                password = password.orEmpty(),
                passwordError = passwordError?.message
            )
        }
    }

    private fun setUsername(username: String?) {
        val usernameError = validateInput(username, USERNAME)
        _state.update {
            it.copy(
                username = username.orEmpty(),
                usernameError = usernameError?.message
            )
        }
    }

    private fun validateLoginForm(): Boolean {
        val usernameError = validateInput(_state.value.username, USERNAME)
        val passwordError = validateInput(_state.value.password, PASSWORD)
        _state.update {
            it.copy(
                usernameError = usernameError?.message,
                passwordError = passwordError?.message
            )
        }
        return usernameError == null && passwordError == null
    }

    private fun guestLogin() = launchOnIO {
        loginGuest().fold(
            ifLeft = { appError -> _state.update { it.copy(appError = appError) } },
            ifRight = { _ -> _state.update { it.copy(isLoggedIn = true) } }
        )
    }

    private fun launchOnIO(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            try {
                _state.update { it.copy(isLoading = true) }
                action()
            } catch (appError: AppError) {
                _state.update { it.copy(isLoading = false, appError = appError) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, appError = e.toAppError()) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
