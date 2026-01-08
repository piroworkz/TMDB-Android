package com.davidluna.tmdb.media_ui.view.videos

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidluna.tmdb.core_ui.composables.ErrorDialogView
import com.davidluna.tmdb.core_ui.composables.LoadingIndicator
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.media_domain.entities.details.Video
import com.davidluna.tmdb.media_ui.presenter.videos.VideoPlayerViewModel
import com.davidluna.tmdb.media_ui.view.utils.UiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VideoPlayerScreen(
    mediaId: Int,
    viewModel: VideoPlayerViewModel = koinViewModel { parametersOf(mediaId) }
) {
    val uiState by viewModel.mediaVideos.collectAsStateWithLifecycle()

    Crossfade(targetState = uiState) {
        when (it) {
            is UiState.Failure -> ErrorDialogView(appError = it.appError) { }
            UiState.Loading -> LoadingIndicator()
            is UiState.Success<List<Video>> -> VideoPlayerScreen(videos = it.data)
        }
    }


}

@Composable
fun VideoPlayerScreen(videos: List<Video>) {
    val state = rememberVideoPlayerState()

    VideoPlayerControllerEffect(playerState = state)

    AndroidView(
        factory = { context: Context ->
            val webView = state.buildWebView(context)
            webView.loadData(state.loadHtml(context, videos), "text/html", "UTF-8")
            webView
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
private fun VideoPlayerControllerEffect(playerState: VideoPlayerState) {
    LaunchedEffect(playerState.showAppBar.value) {
        if (playerState.showAppBar.value) {
            playerState.exitFullscreen()
        } else {
            playerState.enterFullscreen()
        }
    }
}


@Preview
@Composable
private fun VideoPlayerScreenPreview() {
    TmdbTheme {
        VideoPlayerScreen(videos = emptyList())
    }
}