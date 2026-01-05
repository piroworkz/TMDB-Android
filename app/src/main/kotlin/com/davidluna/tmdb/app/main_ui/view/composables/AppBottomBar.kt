package com.davidluna.tmdb.app.main_ui.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_domain.entities.MediaType.MOVIE
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import com.davidluna.tmdb.media_ui.view.utils.title

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    selectedCatalog: Catalog,
    bottomNavItems: List<Catalog>,
    onMediaCatalogSelected: (Catalog) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(BottomAppBarDefaults.ContentPadding)
            .background(colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        bottomNavItems.forEach { catalog: Catalog ->
            val title = catalog.title?.let { stringResource(it) }.orEmpty()
            TextButton(
                modifier = Modifier
                    .weight(1f),
                onClick = { onMediaCatalogSelected(catalog) },
                enabled = catalog != selectedCatalog,
                colors = ButtonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.onSurface,
                    disabledContainerColor = colorScheme.onSurfaceVariant.copy(alpha = 0.3F),
                    disabledContentColor = colorScheme.onSurfaceVariant
                )
            ) {
                Image(
                    if (catalog.mediaType == MOVIE) Icons.Outlined.Movie else Icons.Outlined.Tv,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    title,
                    maxLines = 1,
                    softWrap = false,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AppBottomBarPreview() {
    var selectedCatalog by remember { mutableStateOf(Catalog.MOVIE_NOW_PLAYING) }

    TmdbTheme {
        AppBottomBar(
            selectedCatalog = selectedCatalog,
            bottomNavItems = selectedCatalog.mediaType.bottomBarItems(),
            onMediaCatalogSelected = { selectedCatalog = it }
        )
    }
}