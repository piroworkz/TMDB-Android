package com.davidluna.tmdb.media_ui.view.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.davidluna.tmdb.media_domain.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.entities.MediaType
import com.davidluna.tmdb.media_ui.view.utils.UiState
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsFavoritesWhenStateIsSuccess(): Unit = composeTestRule.run {
        val favorites = listOf(
            FavoriteItem(
                id = 1,
                posterPath = "/one.jpg",
                title = "Favorite One",
                mediaType = MediaType.MOVIE
            ),
            FavoriteItem(
                id = 2,
                posterPath = "/two.jpg",
                title = "Favorite Two",
                mediaType = MediaType.TV
            )
        )

        setContent {
            FavoritesScreen(
                favoritesState = UiState.Success(favorites)
            )
        }

        onNodeWithText("Favorite One").assertIsDisplayed()
        onNodeWithText("Favorite Two").assertIsDisplayed()
    }
}
