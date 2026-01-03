package com.davidluna.tmdb.media_ui.view.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidluna.tmdb.core_ui.composables.ErrorDialogView
import com.davidluna.tmdb.core_ui.composables.LoadingIndicator
import com.davidluna.tmdb.core_ui.navigation.Destination
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens
import com.davidluna.tmdb.media_domain.entities.details.Cast
import com.davidluna.tmdb.media_domain.entities.details.Genre
import com.davidluna.tmdb.media_domain.entities.details.Image
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails
import com.davidluna.tmdb.media_ui.navigation.MediaNavigation
import com.davidluna.tmdb.media_ui.presenter.detail.MediaDetailsViewModel
import com.davidluna.tmdb.media_ui.view.details.composables.MediaCastView
import com.davidluna.tmdb.media_ui.view.details.composables.MediaDetailsView
import com.davidluna.tmdb.media_ui.view.media.composables.CarouselImageView
import com.davidluna.tmdb.media_ui.view.media.composables.MediaPager
import com.davidluna.tmdb.media_ui.view.utils.UiState

@Composable
fun MediaDetailScreen(
    viewModel: MediaDetailsViewModel = hiltViewModel(),
    navigate: (Destination) -> Unit,
) {
    val uiState by viewModel.mediaDetails.collectAsStateWithLifecycle()

    Crossfade(targetState = uiState) {
        when (val state = it) {
            is UiState.Failure -> ErrorDialogView(appError = state.appError) { }
            UiState.Loading -> LoadingIndicator()
            is UiState.Success<MediaDetails> ->
                MediaDetailScreen(
                    backdropPath = state.data.backdropPath,
                    castList = state.data.castList,
                    genres = state.data.genres,
                    images = state.data.images,
                    overview = state.data.overview,
                    releaseDate = state.data.releaseDate,
                    tagline = state.data.tagline,
                    score = state.data.score,
                    voteAveragePercentage = state.data.voteAveragePercentage,
                    playTrailer = {
                        navigate(
                            MediaNavigation.Video(
                                mediaId = state.data.id,
                                appBarTitle = state.data.title
                            )
                        )
                    }
                )
        }
    }

}

@Composable
fun MediaDetailScreen(
    backdropPath: String,
    castList: List<Cast>,
    genres: List<Genre>,
    images: List<Image>,
    overview: String,
    releaseDate: String,
    tagline: String,
    score: Float,
    voteAveragePercentage: String,
    playTrailer: () -> Unit,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(backdropPath)
            .crossfade(500)
            .build(),
        contentDescription = "",
        modifier = Modifier
            .fillMaxSize(),
        alignment = Alignment.Center,
        contentScale = ContentScale.Crop,
        alpha = .3F
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        MediaPager(
            modifier = Modifier
                .padding(vertical = Dimens.margins.large),
            itemCount = images.size
        ) {
            val media = images[it].filePath
            CarouselImageView(
                model = media,
                aspectRatio = .66F,
                isFavorite = false,
                onToggleFavorite = {}
            )
        }

        MediaDetailsView(
            releaseDate = releaseDate,
            genres = genres,
            tagline = tagline,
            overview = overview,
            score = score,
            voteAveragePercentage = voteAveragePercentage
        ) { playTrailer() }
        Spacer(modifier = Modifier.padding(all = 16.dp))
        MediaCastView(castList)
        Spacer(modifier = Modifier.padding(top = Dimens.margins.large))
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun MediaDetailScreenPreview() {
    TmdbTheme {
        MediaDetailScreen(
            backdropPath = "",
            castList = emptyList(),
            genres = emptyList(),
            images = emptyList(),
            overview = "",
            releaseDate = "",
            tagline = "",
            score = 0F,
            voteAveragePercentage = "0%",
            playTrailer = {}
        )
    }
}