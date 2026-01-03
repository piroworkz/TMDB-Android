package com.davidluna.tmdb.media_ui.view.media.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidluna.tmdb.core_ui.composables.shimmer
import com.davidluna.tmdb.core_ui.theme.TmdbTheme

@Composable
fun CarouselImageView(
    model: String?,
    aspectRatio: Float = 1.7F,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Box {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(1000)
                .build(),
            contentDescription = "CarouselImageView",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .shimmer(model == null),
            placeholder = painterResource(com.davidluna.tmdb.core_ui.R.drawable.logo_v1),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop
        )

        FavoriteToggleButton(
            isFavorite = isFavorite,
            onToggle = onToggleFavorite
        )
    }
}

@Preview
@Composable
fun CarouselImagePreView() {
    var model: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        model = "/uLXxpWRfoIPfB2fwM8hsAMIjSWf.jpg".buildModel("w500")
    }

    TmdbTheme {
        CarouselImageView(
            model = model,
            isFavorite = false,
            onToggleFavorite = {}
        )
    }
}
