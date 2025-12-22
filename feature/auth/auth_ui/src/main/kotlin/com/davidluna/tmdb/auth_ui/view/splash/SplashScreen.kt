package com.davidluna.tmdb.auth_ui.view.splash

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidluna.tmdb.auth_ui.navigation.AuthNavigation
import com.davidluna.tmdb.auth_ui.presenter.splash.SplashViewModel
import com.davidluna.tmdb.auth_ui.view.splash.holder.CurrentScreen.PERMISSIONS_PROMPT
import com.davidluna.tmdb.auth_ui.view.splash.holder.CurrentScreen.SPLASH
import com.davidluna.tmdb.auth_ui.view.splash.holder.SplashAnimationState
import com.davidluna.tmdb.auth_ui.view.splash.holder.invoke
import com.davidluna.tmdb.auth_ui.view.splash.holder.rememberBiometricsAuthenticator
import com.davidluna.tmdb.auth_ui.view.splash.holder.rememberPermissionsPromptState
import com.davidluna.tmdb.auth_ui.view.splash.holder.rememberSplashState
import com.davidluna.tmdb.core_ui.R
import com.davidluna.tmdb.core_ui.navigation.Destination
import com.davidluna.tmdb.core_ui.theme.TmdbTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navigateHome: () -> Unit,
    navigate: (Destination) -> Unit,
) {
    val currentScreen by viewModel.state.collectAsStateWithLifecycle()
    val biometricAuthenticator = rememberBiometricsAuthenticator(navigateHome, navigate)
    val permissionsPromptState = rememberPermissionsPromptState { viewModel.checkSessionStatus() }
    val animationState: SplashAnimationState =
        rememberSplashState {
            if (permissionsPromptState.areGranted()) {
                viewModel.checkSessionStatus()
            } else {
                viewModel.updateCurrentScreen(PERMISSIONS_PROMPT)
            }
        }

    LaunchedEffect(viewModel.isLoggedIn.value) {
        viewModel.isLoggedIn.value?.let {
            if (it) biometricAuthenticator.showBiometricPrompt() else navigate(AuthNavigation.Login())
        }
    }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = currentScreen,
            label = "CurrentScreen"
        ) { screen ->
            if (screen == SPLASH) {
                SplashScreen(
                    holder = animationState,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent
                )
            } else {
                PermissionsPromptScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    launchPermissionsPrompt = { permissionsPromptState() },
                    onDismiss = { viewModel.checkSessionStatus() }
                )

            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SplashScreen(
    holder: SplashAnimationState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    SideEffect { holder.startAnimation() }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        with(sharedTransitionScope) {
            Image(
                painter = painterResource(id = R.drawable.logo_v1),
                contentDescription = stringResource(R.string.logo_content_description),
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = holder.scale()
                        scaleY = holder.scale()
                        createBlurEffect(holder)
                    }
                    .sharedElement(
                        sharedContentState = rememberSharedContentState("tmdb_logo"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
        }
    }
}

private fun GraphicsLayerScope.createBlurEffect(holder: SplashAnimationState) {
    if (holder.blur() > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        renderEffect = RenderEffect.createBlurEffect(
            holder.blur(),
            holder.blur(),
            Shader.TileMode.DECAL
        ).asComposeRenderEffect()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SplashScreenPreview() {

    TmdbTheme {
        var currentScreen by remember { mutableStateOf(SPLASH) }
        val animationState: SplashAnimationState = rememberSplashState { currentScreen = PERMISSIONS_PROMPT }
        SharedTransitionLayout {
            AnimatedContent(
                targetState = currentScreen,
                label = "CurrentScreen"
            ) { screen ->
                if (screen == SPLASH) {
                    SplashScreen(
                        holder = animationState,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent
                    )
                } else {
                    PermissionsPromptScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@AnimatedContent,
                        launchPermissionsPrompt = {  },
                        onDismiss = {  }
                    )

                }
            }
        }
    }
}