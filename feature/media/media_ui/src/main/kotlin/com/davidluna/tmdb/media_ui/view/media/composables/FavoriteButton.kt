package com.davidluna.tmdb.media_ui.view.media.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
    toggleFavorite: () -> Unit,
) {
    val icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    val contentDescription =
        if (isFavorite) Icons.Default.Favorite.name else Icons.Default.FavoriteBorder.name

    IconButton(
        onClick = toggleFavorite,
        modifier = modifier
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}