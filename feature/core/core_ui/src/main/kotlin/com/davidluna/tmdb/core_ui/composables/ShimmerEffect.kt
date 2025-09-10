package com.davidluna.tmdb.core_ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.tooling.preview.Preview
import com.davidluna.tmdb.core_ui.theme.TmdbTheme

fun Modifier.shimmer(
    enabled: Boolean,
    durationMillis: Int = 1000,
): Modifier = if (!enabled) this else composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerProgress"
    )

    val colors = listOf(
        Color.Unspecified,
        Color.Gray.copy(alpha = .5F),
        Color.Unspecified,
    )
    drawWithCache {
        val bandWidth = size.width
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(0F, 0F),
            end = Offset(bandWidth, 0F),
            tileMode = TileMode.Clamp
        )
        onDrawWithContent {
            drawContent()
            val translateX = progress * bandWidth
            drawRect(Color.LightGray.copy(alpha = .3F))
            withTransform({
                translate(left = translateX, top = 0f)
            }) {
                drawRect(brush = brush)
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ShimmerPreview() {
    TmdbTheme {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .shimmer(enabled = true)
        )
    }
}
