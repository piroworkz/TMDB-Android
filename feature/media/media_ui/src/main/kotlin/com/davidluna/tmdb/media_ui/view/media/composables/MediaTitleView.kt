package com.davidluna.tmdb.media_ui.view.media.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.davidluna.tmdb.core_ui.composables.shimmer
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens
import kotlinx.coroutines.delay

@Composable
fun MediaTitleView(movieTitle: String?) {
    Text(
        text = movieTitle.orEmpty(),
        modifier = Modifier
            .padding(horizontal = Dimens.margins.small)
            .fillMaxWidth()
            .shimmer(movieTitle == null),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
private fun MediaTitlePreView() {
    var title: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        delay(3000)
        title = "MediaTitlePreView"
    }

    TmdbTheme {
        MediaTitleView(movieTitle = title)
    }
}
