package com.davidluna.tmdb.media_ui.view.media.composables

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role

@Composable
fun BoxScope.FavoriteToggleButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onToggle: () -> Unit
) {
    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder

    IconButton(
        modifier = modifier
            .align(Alignment.TopEnd)
            .clearAndSetSemantics {
                contentDescription = icon.name
                role = Role.Button
                onClick {
                    onToggle()
                    true
                }
            },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .3F),
        ),
        onClick = { onToggle() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name
        )
    }
}