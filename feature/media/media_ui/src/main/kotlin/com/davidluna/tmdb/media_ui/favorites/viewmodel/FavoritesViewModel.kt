package com.davidluna.tmdb.media_ui.favorites.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    fun onToggleFavorite(item: FavoriteItem) {
        val previous = _favoriteIds.value
        val updated = if (previous.contains(item.id)) previous - item.id else previous + item.id
        _favoriteIds.value = updated

        viewModelScope.launch {
            val result = toggleFavoriteUseCase(item)
            if (result.isLeft()) {
                _favoriteIds.value = previous
            }
        }
    }
}
