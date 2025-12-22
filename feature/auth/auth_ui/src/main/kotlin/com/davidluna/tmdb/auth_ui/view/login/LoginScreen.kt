package com.davidluna.tmdb.auth_ui.view.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidluna.tmdb.auth_ui.presenter.login.LoginEvent
import com.davidluna.tmdb.auth_ui.presenter.login.LoginViewModel
import com.davidluna.tmdb.auth_ui.view.login.composables.PasswordTextField
import com.davidluna.tmdb.auth_ui.view.login.composables.UsernameTextField
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_ui.R
import com.davidluna.tmdb.core_ui.composables.ErrorDialogView
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            navigate()
        }
    }

    LoginScreen(
        appError = state.appError,
        isLoading = state.isLoading,
        password = state.password,
        passwordError = state.passwordError,
        username = state.username,
        usernameError = state.usernameError,
        onEvent = { viewModel.onEvent(it) }
    )
}

@Composable
fun LoginScreen(
    appError: AppError?,
    isLoading: Boolean,
    password: String,
    passwordError: String?,
    username: String,
    usernameError: String?,
    onEvent: (LoginEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo_v1),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            alpha = 0.5F
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.margins.xLarge)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            UsernameTextField(
                value = username,
                fieldErrorMessage = usernameError,
                onValueChange = { onEvent(LoginEvent.SetUsername(it)) }
            )

            PasswordTextField(
                value = password,
                fieldErrorMessage = passwordError,
                onValueChange = { onEvent(LoginEvent.SetPassword(it)) }
            )

            Button(
                onClick = {
                    onEvent(
                        LoginEvent.LoginButtonClicked(
                            username,
                            password
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.margins.large),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = stringResource(R.string.btn_login),
                    modifier = Modifier.padding(horizontal = Dimens.margins.xLarge)
                )
            }

            TextButton(
                onClick = { onEvent(LoginEvent.GuestButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(
                    text = stringResource(id = R.string.btn_login_as_guest),
                    modifier = Modifier.padding(horizontal = Dimens.margins.xLarge)
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        if (appError != null) {
            ErrorDialogView(appError = appError) { onEvent(LoginEvent.ResetAppError) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    TmdbTheme {
        LoginScreen(
            appError = null,
            isLoading = false,
            password = "password",
            passwordError = null,
            username = "username",
            usernameError = null,
            onEvent = {}
        )
    }
}