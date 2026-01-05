package com.davidluna.tmdb.app.main_ui.view.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.davidluna.tmdb.app.main_ui.model.DrawerItem
import com.davidluna.tmdb.app.main_ui.model.DrawerItem.CloseSession
import com.davidluna.tmdb.app.main_ui.model.DrawerItem.Movies
import com.davidluna.tmdb.app.main_ui.model.DrawerItem.TvShows
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.core_ui.R
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.core_ui.theme.dimens.Dimens
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_ui.view.media.composables.rememberItemWidth

@Composable
fun NavDrawerView(
    selectedEndpoint: Catalog,
    userAccount: UserAccount?,
    onSelected: (DrawerItem) -> Unit,
) {

    val size = rememberItemWidth(maxItemsPerScreen = 5)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75F)
            .background(colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(Dimens.margins.xLarge))

        Card(
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            shape = CircleShape
        ) {
            if (userAccount == null) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "guest photo",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center
                )
            } else {
                AsyncImage(
                    model = userAccount.avatarPath,
                    contentDescription = "user photo",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.margins.large))

        Text(
            text = userAccount?.name ?: "Guest User",
            color = colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(Dimens.margins.large))

        HorizontalDivider(
            thickness = Dimens.margins.thin,
            color = colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(Dimens.margins.large))

        DrawerItem.list.forEach { item ->
            val isSelected by remember(selectedEndpoint) { mutableStateOf(item.startEndpoint?.mediaType == selectedEndpoint.mediaType) }

            Row(
                modifier = Modifier
                    .padding(Dimens.margins.medium)
                    .fillMaxWidth()
                    .clickable(enabled = !isSelected) { onSelected(item) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.iconResource,
                    contentDescription = item.iconResource.name,
                    tint = if (isSelected) colorScheme.primaryContainer else colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(Dimens.margins.large))

                Text(
                    text = stringResource(id = item.titleResource),
                    modifier = Modifier
                        .padding(vertical = Dimens.margins.medium),
                    color = if (isSelected) colorScheme.primaryContainer else colorScheme.onBackground
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
fun NavDrawerViewPreview() {
    var selectedItem: Catalog? by remember { mutableStateOf(Catalog.MOVIE_NOW_PLAYING) }
    TmdbTheme {
        selectedItem?.let {
            NavDrawerView(
                selectedEndpoint = it,
                userAccount = UserAccount(
                    userId = 4253,
                    name = "Karina Dorsey",
                    username = "Carolina Nash",
                    avatarPath = "pulvinar"
                ),
                onSelected = { item: DrawerItem ->
                    selectedItem = when (item) {
                        CloseSession -> null
                        Movies -> Catalog.MOVIE_NOW_PLAYING
                        TvShows -> Catalog.TV_ON_THE_AIR
                    }
                }
            )
        }
    }
}
