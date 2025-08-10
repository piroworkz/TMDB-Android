package com.davidluna.tmdb.media_ui.presenter.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidluna.tmdb.core_domain.entities.toAppError
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails
import com.davidluna.tmdb.media_domain.usecases.GetMediaDetailsUseCase
import com.davidluna.tmdb.media_domain.usecases.GetSelectedMediaCatalog
import com.davidluna.tmdb.media_ui.di.DetailsMediaId
import com.davidluna.tmdb.media_ui.view.utils.UiState
import com.davidluna.tmdb.media_ui.view.utils.getMediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MediaDetailsViewModel @Inject constructor(
    private val getSelectedMediaCatalogUseCase: GetSelectedMediaCatalog,
    private val getMediaDetails: GetMediaDetailsUseCase,
    @param:DetailsMediaId private val mediaId: Int,
) : ViewModel() {

    val mediaDetails: StateFlow<UiState<MediaDetails>> = fetchMediaDetail().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    private fun fetchMediaDetail(): Flow<UiState<MediaDetails>> =
        getSelectedMediaCatalogUseCase.selectedCatalog
            .distinctUntilChanged()
            .catch { UiState.Failure(it.toAppError()) }
            .map { mediaCatalog ->
                val selectedEndpoint =
                    if (mediaCatalog.getMediaType() == MediaType.MOVIE) Catalog.MOVIE_DETAIL else Catalog.TV_DETAIL
                getMediaDetails(selectedEndpoint, mediaId)
                    .fold(
                        ifLeft = { UiState.Failure(it) },
                        ifRight = { UiState.Success(it) }
                    )
            }

}