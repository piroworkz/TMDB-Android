package com.davidluna.tmdb.media_ui.view.media.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidluna.tmdb.core_ui.R
import com.davidluna.tmdb.core_ui.composables.shimmer
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens

@Composable
fun FilmMaskImageView(
    model: String?,
    aspectRatio: Float = .66F,
) {
    CinemaFilmFrame {
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

    }
}

@Composable
private fun CinemaFilmFrame(
    modifier: Modifier = Modifier,
    squareCount: Int = 6,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit = {}
) {
    Box(
        modifier = modifier
            .padding(Dimens.margins.small)
            .clip(RoundedCornerShape(Dimens.margins.medium))
            .background(Color.Black)
            .drawWithCache {
                onDrawWithContent {
                    val spacing = size.width / (squareCount * (squareCount / 2))
                    val squareSize = (size.width / squareCount.toFloat()) - spacing
                    drawContent()
                    drawRect(
                        color = backgroundColor,
                        size = Size(size.width, squareSize + spacing)
                    )

                    drawRect(
                        color = backgroundColor,
                        topLeft = Offset(0F, size.height - (squareSize + spacing)),
                        size = Size(size.width, squareSize + spacing)
                    )

                    repeat(squareCount) { index: Int ->
                        val xOffset = (squareSize + spacing) * index + (spacing / 2)
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(xOffset, spacing / 2),
                            size = Size(squareSize, squareSize),
                            cornerRadius = CornerRadius(10f)
                        )

                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(xOffset, size.height - (squareSize + (spacing / 2))),
                            size = Size(squareSize, squareSize),
                            cornerRadius = CornerRadius(10f)
                        )
                    }
                }
            }
    ) {
        content()
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun CinemaFilmFramePreview() {
    TmdbTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3)
        ) {

            items(12) {
                CinemaFilmFrame {
                    Image(
                        painterResource(R.drawable.demo_thumb), contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(.66F),
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }
    }
}

fun String.buildModel(width: String = "w185"): String =
    "https://image.tmdb.org/t/p/$width$this"