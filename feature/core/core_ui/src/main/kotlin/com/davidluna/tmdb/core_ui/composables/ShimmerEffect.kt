package com.davidluna.tmdb.core_ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
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
        Color.LightGray,
        Color.Unspecified,
    )
    drawWithCache {
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(0F, 0F),
            end = Offset(size.width, 0F),
            tileMode = TileMode.Clamp
        )
        onDrawWithContent {
            drawContent()
            val translateX = (progress * size.width) / 2
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
        Row(
            modifier = Modifier
                .padding(24.dp)
        ){
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .shimmer(enabled = true)
            )

            Column {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .height(16.dp)
                            .clip(CircleShape)
                            .shimmer(enabled = true)
                    )
                }
            }

        }
    }
}
