package com.davidluna.tmdb.media_ui.presenter.favorites

import androidx.lifecycle.ViewModel
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.usecases.ObserveFavorites
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_ui.view.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val toggleFavorite: ToggleFavorite,
    private val observeFavorites: ObserveFavorites,
    private val scope: CoroutineScope,
) : ViewModel() {

    private val _toggleState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val toggleState: StateFlow<UiState<Boolean>> = _toggleState

    val favoritesState: StateFlow<UiState<List<FavoriteItem>>> =
        observeFavorites.favorites
            .map<List<FavoriteItem>, UiState<List<FavoriteItem>>> { UiState.Success(it) }
            .catch { emit(UiState.Failure(it.toAppError())) }
            .stateIn(scope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val favoriteIds: StateFlow<Set<Int>> = favoritesState
        .map { state ->
            when (state) {
                UiState.Loading -> emptySet()
                is UiState.Failure -> emptySet()
                is UiState.Success -> state.data.map { it.id }.toSet()
            }
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun toggleFavorite(media: Media?, mediaType: MediaType?) {
        media?.let {
            scope.launch {
                _toggleState.update { UiState.Loading }
                toggleFavorite.invoke(media.toFavoriteItem(mediaType ?: MediaType.MOVIE)).fold(
                    ifLeft = { error -> _toggleState.update { UiState.Failure(error) } },
                    ifRight = { result ->
                        _toggleState.update { UiState.Success(result) }
                    }
                )
            }
        }
    }

    private fun Media.toFavoriteItem(mediaType: MediaType) = FavoriteItem(
        id = id,
        posterPath = posterPath,
        title = title,
        mediaType = mediaType,
    )
}
