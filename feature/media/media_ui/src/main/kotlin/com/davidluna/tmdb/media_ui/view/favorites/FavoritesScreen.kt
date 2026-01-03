package com.davidluna.tmdb.media_ui.view.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidluna.tmdb.core_ui.composables.ErrorDialogView
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_ui.presenter.favorites.FavoritesViewModel
import com.davidluna.tmdb.media_ui.view.utils.UiState

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()

    FavoritesScreen(
        favoritesState = favoritesState,
    )
}

@Composable
fun FavoritesScreen(
    favoritesState: UiState<List<FavoriteItem>>,
) {
    when (favoritesState) {
        UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Failure -> {
            ErrorDialogView(favoritesState.appError) { }
        }

        is UiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = favoritesState.data,
                    key = { it.id }
                ) { favorite ->
                    Text(
                        text = favorite.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Dimens.margins.large,
                                vertical = Dimens.margins.medium
                            )
                    )
                }
            }
        }
    }
}
