package com.davidluna.tmdb.media_ui.presenter.media

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Catalog.MOVIE_UPCOMING
import com.davidluna.tmdb.media_domain.entities.Catalog.TV_AIRING_TODAY
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType.MOVIE
import com.davidluna.tmdb.media_domain.usecases.ObserveFavoriteMedia
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias MediaIndex = Int
typealias MediaOffset = Int

class MediaCatalogViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val observeFavoriteMedia: ObserveFavoriteMedia,
    private val observeMediaCatalog: ObserveMediaCatalog,
    private val observeSelectedMediaCatalogUseCase: ObserveSelectedMediaCatalog,
    private val toggleFavorite: ToggleFavorite
) : ViewModel() {


    private val _state = MutableStateFlow(State())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = initState()
    )

    val pagerPagingDataFlow: StateFlow<PagingData<Media>> = getPagerFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PagingData.empty()
    )

    val gridPagingDataFlow: StateFlow<PagingData<Media>> = getGridFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PagingData.empty()
    )

    @Stable
    data class State(
        val appError: AppError? = null,
        val gridCatalog: Catalog? = null,
        val pagerCatalog: Catalog? = null,
        val gridFavorites: List<Int> = emptyList(),
        val pagerFavorites: List<Int> = emptyList(),
        val lastKnownPosition: Pair<MediaIndex, MediaOffset> = 0 to 0
    )

    fun onEvent(event: MediaCatalogEvent) {
        when (event) {
            MediaCatalogEvent.ClearAppError -> clearAppError()
            is MediaCatalogEvent.LastKnownPosition ->
                updateLastKnownPosition(event.index, event.offset)

            is MediaCatalogEvent.ToggleFavorite -> toggleFavorite(event.media, event.catalog)
        }
    }

    private fun toggleFavorite(media: Media, catalog: Catalog) {
        viewModelScope.launch {
            toggleFavorite.toggle(mediaId = media.id, catalog = catalog)
        }
    }

    private fun updateLastKnownPosition(index: MediaIndex, offset: MediaOffset) {
        _state.update { it.copy(lastKnownPosition = index to offset) }
    }

    private fun clearAppError() {
        _state.update { it.copy(appError = null) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getGridFlow() = observeSelectedMediaCatalogUseCase.selectedCatalog
        .distinctUntilChanged()
        .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
        .flatMapLatest { mediaCatalog ->
            _state.update { it.copy(gridCatalog = mediaCatalog) }
            observeMediaCatalog(mediaCatalog, viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getPagerFlow() = observeSelectedMediaCatalogUseCase.selectedCatalog
        .distinctUntilChanged()
        .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
        .flatMapLatest { catalog ->
            val pagerCatalog = if (catalog.mediaType == MOVIE) MOVIE_UPCOMING else TV_AIRING_TODAY
            _state.update { it.copy(pagerCatalog = pagerCatalog) }
            observeMediaCatalog(pagerCatalog, viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initState(): State {
        viewModelScope.launch {
            observeSelectedMediaCatalogUseCase.selectedCatalog
                .flatMapLatest { observeFavoriteMedia.observe(it) }
                .collect { _state.update { state -> state.copy(gridFavorites = it.map { favorite -> favorite.id }) } }
        }
        viewModelScope.launch {
            observeSelectedMediaCatalogUseCase.selectedCatalog
                .flatMapLatest { catalog ->
                    val pagerCatalog =
                        if (catalog.mediaType == MOVIE) MOVIE_UPCOMING else TV_AIRING_TODAY
                    observeFavoriteMedia.observe(pagerCatalog)
                }
                .collect { _state.update { state -> state.copy(pagerFavorites = it.map { favorite -> favorite.id }) } }
        }
        return State()
    }
}