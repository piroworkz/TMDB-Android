package com.davidluna.tmdb.app.main_ui.view.composables

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.davidluna.tmdb.core_ui.theme.TmdbTheme
import com.davidluna.tmdb.media_domain.entities.Catalog
import com.davidluna.tmdb.media_ui.view.utils.bottomBarItems
import org.junit.Rule
import org.junit.Test

class AppBottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialRender_showsNowPlayingSelected(): Unit = composeTestRule.run {
        var selectedCatalog by mutableStateOf(Catalog.MOVIE_NOW_PLAYING)
        setContent {
            TmdbTheme {
                AppBottomBar(
                    selectedCatalog = selectedCatalog,
                    bottomNavItems = selectedCatalog.mediaType.bottomBarItems(),
                    onMediaCatalogSelected = { selectedCatalog = it }
                )
            }
        }

        onNodeWithText("NOW PLAYING").assertExists().assertIsDisplayed().assertIsNotEnabled()
        onNodeWithText("POPULAR").assertExists().assertIsDisplayed().assertIsEnabled()
        onNodeWithText("TOP RATED").assertExists().assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun clickingPopular_selectsPopular(): Unit = composeTestRule.run {
        var selectedCatalog by mutableStateOf(Catalog.MOVIE_NOW_PLAYING)
        setContent {
            TmdbTheme {
                AppBottomBar(
                    selectedCatalog = selectedCatalog,
                    bottomNavItems = selectedCatalog.mediaType.bottomBarItems(),
                    onMediaCatalogSelected = { selectedCatalog = it }
                )
            }
        }

        onNodeWithText("POPULAR").performClick()

        onNodeWithText("NOW PLAYING").assertIsEnabled()
        onNodeWithText("POPULAR").assertIsNotEnabled()
        onNodeWithText("TOP RATED").assertIsEnabled()
    }

    @Test
    fun clickingTopRated_selectsTopRated(): Unit = composeTestRule.run {
        var selectedCatalog by mutableStateOf(Catalog.MOVIE_NOW_PLAYING)
        setContent {
            TmdbTheme {
                AppBottomBar(
                    selectedCatalog = selectedCatalog,
                    bottomNavItems = selectedCatalog.mediaType.bottomBarItems(),
                    onMediaCatalogSelected = { selectedCatalog = it }
                )
            }
        }

        onNodeWithText("TOP RATED").performClick()

        onNodeWithText("NOW PLAYING").assertIsEnabled()
        onNodeWithText("POPULAR").assertIsEnabled()
        onNodeWithText("TOP RATED").assertIsNotEnabled()
    }
}
