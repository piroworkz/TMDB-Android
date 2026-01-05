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
import com.davidluna.tmdb.media_ui.di.VideosMediaId
import com.davidluna.tmdb.media_ui.view.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val observeSelectedMediaCatalogUseCase: ObserveSelectedMediaCatalog,
    private val getCatalogVideos: GetCatalogVideos,
    @param:VideosMediaId private val mediaId: Int,
) : ViewModel() {

    val mediaVideos: StateFlow<UiState<List<Video>>> = fetchMediaVideos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    private fun fetchMediaVideos(): Flow<UiState<List<Video>>> = observeSelectedMediaCatalogUseCase.selectedCatalog
        .distinctUntilChanged()
        .map { catalog: Catalog ->
            val selected = if (catalog.mediaType == MOVIE) MOVIE_DETAIL else TV_DETAIL
            getCatalogVideos(selected, mediaId).fold(
                ifLeft = { UiState.Failure(it) },
                ifRight = { UiState.Success(it) }
            )
        }
}