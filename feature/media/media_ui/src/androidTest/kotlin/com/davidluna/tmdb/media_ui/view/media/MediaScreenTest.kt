package com.davidluna.tmdb.media_ui.view.media

import android.annotation.SuppressLint
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescriptionExactly
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.media_data.fakes.fakeMediaList
import com.davidluna.tmdb.media_domain.entities.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class MediaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @SuppressLint("CheckResult")
    @Test
    fun showsTitlesAndMediaItems(): Unit = composeTestRule.run {
        setContentWithState()

        onRoot(useUnmergedTree = true)
        onNodeWithText("NOW PLAYING").assertExists().assertIsDisplayed()
        onNodeWithText("POPULAR").assertExists().assertIsDisplayed()

        onAllNodes(hasContentDescriptionExactly("FilmMaskImageView"), useUnmergedTree = true)
            .assertCountEquals(9)

        onAllNodes(hasContentDescriptionExactly("FilmMaskImageFrameView"), useUnmergedTree = true)
            .assertCountEquals(9)

        onAllNodes(hasContentDescriptionExactly("CarouselImageView"), useUnmergedTree = true)
            .assertCountEquals(3)
    }

    @SuppressLint("CheckResult")
    @Test
    fun clickingMediaItem_triggersNavigation(): Unit = composeTestRule.run {
        var navigated = false
        setContentWithState(
            navigateTo = { navigated = true }
        )

        onRoot(useUnmergedTree = true)
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

    private fun setContentWithState(
        appError: AppError? = null,
        navigateTo: (Any) -> Unit = {},
    ) {
        val fakeMedia = fakeMediaList

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
                onPositionChanged = { _, _ -> }
            )
        }
    }

    private fun fakeLazyPagingItems(items: List<Media>): Flow<PagingData<Media>> {
        return flowOf(PagingData.from(items))
    }

}