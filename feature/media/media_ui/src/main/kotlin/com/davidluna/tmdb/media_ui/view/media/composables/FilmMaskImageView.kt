package com.davidluna.tmdb.media_ui.view.media.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidluna.tmdb.core_ui.R
import com.davidluna.tmdb.core_ui.composables.shimmer
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import kotlinx.coroutines.delay

@Composable
fun FilmMaskImageView(
    model: String?,
    aspectRatio: Float = 1F,
) {

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(18.dp))
            .aspectRatio(aspectRatio)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(500)
                .build(),
            contentDescription = "FilmMaskImageView",
            modifier = Modifier
                .aspectRatio(aspectRatio)
                .shimmer(model == null),
            alignment = Alignment.Center,
            placeholder = painterResource(R.drawable.logo_v1),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(id = R.drawable.film_mask),
            contentDescription = "FilmMaskImageFrameView",
            modifier = Modifier
                .aspectRatio(aspectRatio),
            contentScale = ContentScale.FillBounds
        )
    }

}


@Preview
@Composable
private fun FilmMaskImagePreview() {
    var model: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        delay(5000)
        model = "/uLXxpWRfoIPfB2fwM8hsAMIjSWf.jpg".buildModel("w500")
    }

    TmdbTheme {
        FilmMaskImageView(
            model = model
        )
    }
}

fun String.buildModel(width: String = "w185"): String =
    "https://image.tmdb.org/t/p/$width$this"