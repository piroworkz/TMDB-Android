package com.davidluna.tmdb.app.main_ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.OnCatalogSelected
import com.davidluna.tmdb.app.main_ui.presenter.MainEvent.ResetAppError
import com.davidluna.tmdb.app.main_ui.presenter.MainViewModel
import com.davidluna.tmdb.app.main_ui.view.Navigator
import com.davidluna.tmdb.app.main_ui.view.composables.rememberNavigatorState
import com.davidluna.tmdb.core_ui.composables.ErrorDialogView
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { false }
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val stateHolder = rememberNavigatorState(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            finish(state.finishActivity)
            if (state.isSessionClosed) {
                viewModel.onEvent(MainEvent.ClearAppData)
            }
            ErrorDialogView(state.appError) { viewModel.onEvent(ResetAppError) }
            stateHolder.BackStackEntryFlowCollectEffect()
            LaunchedEffect(state.bottomNavItems) {
                state.bottomNavItems.firstOrNull()?.let {
                    viewModel.onEvent(event = OnCatalogSelected(endpoint = it))
                }
            }

            TmdbTheme {
                Navigator(
                    navController = navController,
                    state = stateHolder,
                    bottomNavItems = state.bottomNavItems,
                    selectedCatalog = state.selectedCatalog,
                    userAccount = state.userAccount,
                    onMainEvent = { viewModel.onEvent(it) }
                )
            }
        }
    }

    private fun finish(isSessionClosed: Boolean) {
        if (isSessionClosed) finishAffinity()
    }
}