package com.davidluna.tmdb.media_ui.view.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_ui.composables.ErrorDialogView
import com.davidluna.tmdb.core_ui.navigation.Destination
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_ui.navigation.MediaNavigation.Detail
import com.davidluna.tmdb.media_ui.presenter.media.MediaCatalogEvent
import com.davidluna.tmdb.media_ui.presenter.media.MediaCatalogEvent.LastKnownPosition
import com.davidluna.tmdb.media_ui.presenter.media.MediaCatalogEvent.ToggleFavorite
import com.davidluna.tmdb.media_ui.presenter.media.MediaCatalogViewModel
import com.davidluna.tmdb.media_ui.view.media.composables.CarouselImageView
import com.davidluna.tmdb.media_ui.view.media.composables.FavoriteButton
import com.davidluna.tmdb.media_ui.view.media.composables.FilmMaskImageView
import com.davidluna.tmdb.media_ui.view.media.composables.MediaPager
import com.davidluna.tmdb.media_ui.view.media.composables.MediaTitleView
import com.davidluna.tmdb.media_ui.view.media.composables.ReelTitleView
import com.davidluna.tmdb.media_ui.view.utils.title
import org.koin.androidx.compose.koinViewModel

@Composable
fun MediaCatalogScreen(
    viewModel: MediaCatalogViewModel = koinViewModel(),
    navigateTo: (Destination) -> Unit,
) {
    val pagerLazyPagingItems = viewModel.pagerPagingDataFlow.collectAsLazyPagingItems()
    val gridLazyPagingItems = viewModel.gridPagingDataFlow.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsStateWithLifecycle()

    MediaCatalogScreen(
        appError = state.appError,
        gridCatalog = state.gridCatalog,
        gridFavorites = state.gridFavorites,
        gridLazyPagingItems = gridLazyPagingItems,
        lastKnownPosition = state.lastKnownPosition,
        pagerCatalog = state.pagerCatalog,
        pagerFavorites = state.pagerFavorites,
        pagerLazyPagingItems = pagerLazyPagingItems,
        onEvent = viewModel::onEvent,
        navigateTo = { navigateTo(it) },
    )
}

@Composable
fun MediaCatalogScreen(
    appError: AppError?,
    gridCatalog: Catalog?,
    gridFavorites: List<Int>,
    gridLazyPagingItems: LazyPagingItems<Media>,
    lastKnownPosition: Pair<Int, Int>,
    pagerCatalog: Catalog?,
    pagerFavorites: List<Int>,
    pagerLazyPagingItems: LazyPagingItems<Media>,
    onEvent: (MediaCatalogEvent) -> Unit,
    navigateTo: (Destination) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(gridCatalog) {
        if (lazyGridState.firstVisibleItemIndex > 0) {
            lazyGridState.scrollToItem(0)
        }
    }

    LaunchedEffect(lastKnownPosition) {
        if (lastKnownPosition.first != 0) {
            lazyGridState.animateScrollToItem(lastKnownPosition.first, lastKnownPosition.second)
            onEvent(LastKnownPosition(0, 0))
        }
    }

    if (appError != null) {
        ErrorDialogView(appError) {

        }
    }

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(3)
    ) {
        if (pagerLazyPagingItems.itemCount != 0) {
            item(
                span = { GridItemSpan(3) }
            ) {
                ReelTitleView(title = pagerCatalog?.title?.let { stringResource(it) }.orEmpty())
            }
        }

        item(
            span = { GridItemSpan(3) }
        ) {
            val itemCount: Int = pagerLazyPagingItems.getOrNull { it.itemCount } ?: 3
            MediaPager(
                itemCount = itemCount,
                onClick = { index ->
                    pagerLazyPagingItems[index]?.let {
                        navigateTo(Detail(mediaId = it.id, appBarTitle = it.title))
                    }
                }
            ) { index ->
                val media = pagerLazyPagingItems.getOrNull { it[index] }
                CarouselImageView(media?.posterPath, 2F)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MediaTitleView(
                        modifier = Modifier.weight(1F),
                        movieTitle = media?.title
                    )
                    if (media != null && pagerCatalog != null) {
                        FavoriteButton(
                            isFavorite = media.id in pagerFavorites,
                        ) { onEvent(ToggleFavorite(media, pagerCatalog)) }
                    }
                }
            }
        }
        if (gridLazyPagingItems.itemCount != 0) {
            item(
                span = { GridItemSpan(3) }
            ) {
                Spacer(modifier = Modifier.padding(top = Dimens.margins.xLarge))
                ReelTitleView(title = gridCatalog?.title?.let { stringResource(it) }.orEmpty())
            }
        }

        items(
            count = gridLazyPagingItems.getOrNull { it.itemCount } ?: 9,
            key = gridLazyPagingItems.getOrNull { mediaItems -> mediaItems.itemKey { it.id } }
        ) { index ->
            val media: Media? = gridLazyPagingItems.getOrNull { it[index] }
            Column(
                modifier = Modifier
                    .clickable {
                        media?.let {
                            onEvent(
                                LastKnownPosition(
                                    lazyGridState.firstVisibleItemIndex,
                                    lazyGridState.firstVisibleItemScrollOffset
                                )
                            )
                            navigateTo(Detail(mediaId = it.id, appBarTitle = it.title))
                        }
                    }
            ) {
                FilmMaskImageView(model = media?.posterPath)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MediaTitleView(
                        modifier = Modifier.weight(1F),
                        movieTitle = media?.title
                    )
                    if (media != null && gridCatalog != null) {
                        FavoriteButton(
                            isFavorite = media.id in gridFavorites,
                        ) { onEvent(ToggleFavorite(media, gridCatalog)) }
                    }
                }
                Spacer(modifier = Modifier.padding(top = Dimens.margins.xLarge))
            }
        }
    }
}

private fun <T : Any, R : Any> LazyPagingItems<T>.getOrNull(take: (LazyPagingItems<T>) -> R?): R? =
    try {
        if (loadState.refresh is LoadState.NotLoading) take(this) else null
    } catch (_: IndexOutOfBoundsException) {
        null
    }
