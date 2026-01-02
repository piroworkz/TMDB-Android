package com.davidluna.tmdb.app.main_ui.view.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.davidluna.tmdb.app.main_ui.view.composables.NavDrawerViewPreview
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun drawerContainsFavoritesOption(): Unit = composeTestRule.run {
        setContent { NavDrawerViewPreview() }

        onNodeWithText("Favorites").assertIsDisplayed()
    }
}
