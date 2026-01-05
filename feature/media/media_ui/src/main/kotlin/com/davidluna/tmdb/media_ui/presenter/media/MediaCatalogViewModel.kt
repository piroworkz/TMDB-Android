package com.davidluna.tmdb.media_ui.presenter.media

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog.MOVIE_UPCOMING
import com.davidluna.tmdb.media_domain.entities.Catalog.TV_AIRING_TODAY
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_domain.entities.MediaType.MOVIE
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalogUseCase
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_ui.view.utils.title
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

typealias MediaIndex = Int
typealias MediaOffset = Int

@HiltViewModel
class MediaCatalogViewModel @Inject constructor(
    private val observeSelectedMediaCatalogUseCase: ObserveSelectedMediaCatalog,
    private val observeMediaCatalogUseCase: ObserveMediaCatalogUseCase,
) : ViewModel() {


    private val _state = MutableStateFlow(State())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = State()
    )

    val pagerPagingDataFlow: StateFlow<PagingData<Media>> = getPagerFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PagingData.empty()
    )

    val gridPagingDataFlow: StateFlow<PagingData<Media>> = getGridFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PagingData.empty()
    )

    @Stable
    data class State(
        val appError: AppError? = null,
        val gridCatalogTitle: Int? = null,
        val pagerCatalogTitle: Int? = null,
        val lastKnownPosition: Pair<MediaIndex, MediaOffset> = 0 to 0,
    )

    fun updateLastKnownPosition(index: MediaIndex, offset: MediaOffset) {
        _state.update { it.copy(lastKnownPosition = index to offset) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getGridFlow() = observeSelectedMediaCatalogUseCase.selectedCatalog
        .distinctUntilChanged()
        .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
        .flatMapLatest { mediaCatalog ->
            _state.update { it.copy(gridCatalogTitle = mediaCatalog.title) }
            observeMediaCatalogUseCase(mediaCatalog, viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getPagerFlow() = observeSelectedMediaCatalogUseCase.selectedCatalog
        .distinctUntilChanged()
        .catch { e -> _state.update { it.copy(appError = e.toAppError()) } }
        .flatMapLatest { catalog ->
            val pagerCatalog =
                if (catalog.mediaType == MOVIE) MOVIE_UPCOMING else TV_AIRING_TODAY
            _state.update { it.copy(pagerCatalogTitle = pagerCatalog.title) }
            observeMediaCatalogUseCase(pagerCatalog, viewModelScope)
        }
}