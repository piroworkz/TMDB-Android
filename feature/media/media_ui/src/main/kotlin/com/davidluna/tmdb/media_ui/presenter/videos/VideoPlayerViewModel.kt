package com.davidluna.tmdb.media_ui.presenter.videos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Catalog.MOVIE_DETAIL
import com.davidluna.tmdb.media_domain.entities.Catalog.TV_DETAIL
import com.davidluna.tmdb.media_domain.entities.MediaType.MOVIE
import com.davidluna.tmdb.media_domain.entities.details.Video
import com.davidluna.tmdb.media_domain.usecases.GetCatalogVideos
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_ui.view.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class VideoPlayerViewModel(
    private val mediaId: Int,
    private val observeSelectedMediaCatalogUseCase: ObserveSelectedMediaCatalog,
    private val getCatalogVideos: GetCatalogVideos
) : ViewModel() {

    val mediaVideos: StateFlow<UiState<List<Video>>> = fetchMediaVideos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    private fun fetchMediaVideos(): Flow<UiState<List<Video>>> {
        return observeSelectedMediaCatalogUseCase.selectedCatalog
            .distinctUntilChanged()
            .map { catalog: Catalog ->
                val selected = if (catalog.mediaType == MOVIE) MOVIE_DETAIL else TV_DETAIL
                getCatalogVideos(selected, mediaId).fold(
                    ifLeft = { UiState.Failure(it) },
                    ifRight = { UiState.Success(it) }
                )
            }
    }
}