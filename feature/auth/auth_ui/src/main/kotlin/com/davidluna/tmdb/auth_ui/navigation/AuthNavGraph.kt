package com.davidluna.tmdb.auth_ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.davidluna.tmdb.auth_ui.navigation.AuthNavigation.Init
import com.davidluna.tmdb.auth_ui.navigation.AuthNavigation.Login
import com.davidluna.tmdb.auth_ui.view.login.LoginScreen

fun NavGraphBuilder.authNavGraph(
    navigateTo: () -> Unit,
) {
    navigation<Init>(
        startDestination = Login(),
    ) {
        composable<Login> { LoginScreen { navigateTo() } }
    }
}
