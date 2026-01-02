package com.davidluna.tmdb.media_ui.view.media

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescriptionExactly
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.media_domain.entities.Media
import com.davidluna.tmdb.media_framework.fakes.buildFakeMediaList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.junit.Rule
import org.junit.Test

class MediaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsTitlesAndMediaItems(): Unit = composeTestRule.run {
        setContentWithState()

        onRoot(useUnmergedTree = true).printToLog("<--")
        onNodeWithText("NOW PLAYING").assertExists().assertIsDisplayed()
        onNodeWithText("POPULAR").assertExists().assertIsDisplayed()

        onAllNodes(hasContentDescriptionExactly("FilmMaskImageView"), useUnmergedTree = true)
            .assertCountEquals(9)

        onAllNodes(hasContentDescriptionExactly("FilmMaskImageFrameView"), useUnmergedTree = true)
            .assertCountEquals(9)

        onAllNodes(hasContentDescriptionExactly("CarouselImageView"), useUnmergedTree = true)
            .assertCountEquals(3)
    }

    @Test
    fun clickingMediaItem_triggersNavigation(): Unit = composeTestRule.run {
        var navigated = false
        setContentWithState(
            navigateTo = { navigated = true }
        )

        onRoot(useUnmergedTree = true).printToLog("<-------")
        onAllNodes(
            hasContentDescriptionExactly("CarouselImageView"),
            useUnmergedTree = true
        )
            .onFirst()
            .assertExists()
            .performClick()

        assert(navigated)
    }

    @Test
    fun appError_showsErrorDialog(): Unit = composeTestRule.run {
        val error = AppError(
            code = AppErrorCode.LOCAL_ERROR,
            description = "Unexpected error"
        )
        setContentWithState(appError = error)

        onNodeWithText("Unexpected error").assertExists().assertIsDisplayed()
    }

    @Test
    fun clickingFavoriteToggle_updatesFavoriteState(): Unit = composeTestRule.run {
        val fakeMedia = buildFakeMediaList().first()
        val toggleTag = "grid-favorite-toggle-${fakeMedia.id}"
        var favoriteIds by mutableStateOf(setOf<Int>())
        setContentWithState(
            favoriteIds = favoriteIds,
            onToggleFavorite = { media ->
                favoriteIds = if (favoriteIds.contains(media.id)) {
                    favoriteIds - media.id
                } else {
                    favoriteIds + media.id
                }
            }
        )

        onNodeWithTag(toggleTag).assertExists().assertContentDescriptionEquals("Favorite")

        onNodeWithTag(toggleTag).performClick()

        onNodeWithTag(toggleTag).assertContentDescriptionEquals("Unfavorite")
    }

    private fun setContentWithState(
        appError: AppError? = null,
        navigateTo: (Any) -> Unit = {},
        favoriteIds: Set<Int> = emptySet(),
        onToggleFavorite: (Media) -> Unit = {},
    ) {
        val fakeMedia = buildFakeMediaList()

        composeTestRule.setContent {
            val pagerLazyPagingItems = fakeLazyPagingItems(fakeMedia).collectAsLazyPagingItems()
            val gridLazyPagingItems = fakeLazyPagingItems(fakeMedia).collectAsLazyPagingItems()

            MediaCatalogScreen(
                appError = appError,
                gridCatalogTitle = "POPULAR",
                gridLazyPagingItems = gridLazyPagingItems,
                lastKnownPosition = 0 to 0,
                pagerCatalogTitle = "NOW PLAYING",
                pagerLazyPagingItems = pagerLazyPagingItems,
                navigateTo = { navigateTo(it) },
                onPositionChanged = { _, _ -> },
                favoriteIds = favoriteIds,
                onToggleFavorite = onToggleFavorite
            )
        }
    }

    private fun fakeLazyPagingItems(items: List<Media>): Flow<PagingData<Media>> {
        return flowOf(PagingData.from(items))
    }

}
