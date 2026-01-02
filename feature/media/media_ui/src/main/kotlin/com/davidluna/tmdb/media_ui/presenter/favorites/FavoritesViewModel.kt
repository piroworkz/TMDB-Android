package com.davidluna.tmdb.media_ui.presenter.favorites

import androidx.lifecycle.ViewModel
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_ui.view.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val toggleFavorite: ToggleFavorite,
    private val scope: CoroutineScope,
) : ViewModel() {

    private val _toggleState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val toggleState: StateFlow<UiState<Boolean>> = _toggleState

    fun toggleFavorite(item: FavoriteItem) {
        scope.launch {
            _toggleState.update { UiState.Loading }
            toggleFavorite.invoke(item).fold(
                ifLeft = { error -> _toggleState.update { UiState.Failure(error) } },
                ifRight = { result -> _toggleState.update { UiState.Success(result) } }
            )
        }
    }
}
