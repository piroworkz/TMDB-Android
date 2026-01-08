package com.davidluna.tmdb.media_ui.view.details

import android.annotation.SuppressLint
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.davidluna.tmdb.media_data.fakes.fakeMediaDetails
import com.davidluna.tmdb.media_domain.entities.details.MediaDetails
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class MediaDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val details = fakeMediaDetails

    @Test
    fun whenMediaDetailsProvided_displaysAllInformation(): Unit = composeTestRule.run {
        setContent(details)

        onNodeWithText(details.overview)
            .assertExists()
            .assertIsDisplayed()

        onNode(hasText(details.genres.joinToString(", ") { it.name }, substring = true))
            .assertExists()
            .assertIsDisplayed()

        onNode(hasText(details.releaseDate, substring = true))
            .assertExists()
            .assertIsDisplayed()

        onNodeWithText(details.tagline)
            .assertExists()
            .assertIsDisplayed()

        onNodeWithText(details.voteAveragePercentage)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun whenCastListIsEmpty_doesNotShowCastSection(): Unit = composeTestRule.run {
        setContent(details.copy(castList = emptyList()))
        onNodeWithText("Cast").assertDoesNotExist()
    }

    @SuppressLint("CheckResult")
    @Test
    fun whenCastListProvided_CastSectionExists(): Unit = composeTestRule.run {
        setContent(details)

        onRoot(useUnmergedTree = true)

        repeat(3) {
            val cast = details.castList[it]
            val text = if (cast.name.isNotEmpty() && cast.character.isNotEmpty()) {
                "${cast.name} as ${cast.character}"
            } else {
                cast.name
            }
            onNodeWithText(text)
                .assertExists()
        }

    }

    @Test
    fun whenImagesProvided_showsCarouselImages(): Unit = composeTestRule.run {
        setContent(details)
        onAllNodesWithContentDescription("CarouselImageView", useUnmergedTree = true)
            .assertCountEquals(4)
    }

    @SuppressLint("CheckResult")
    @Test
    fun whenPlayTrailerClicked_invokesNavigate(): Unit = composeTestRule.run {
        var playedTrailer = false
        setContent(details.copy(hasVideo = true)) {
            playedTrailer = true
        }
        onRoot(useUnmergedTree = true)
        onNodeWithContentDescription("Outlined.PlayArrow")
            .performClick()
        assertTrue(playedTrailer)
    }

    private fun ComposeContentTestRule.setContent(
        details: MediaDetails,
        playTrailer: () -> Unit = {},
    ) {
        setContent {
            MediaDetailScreen(
                backdropPath = details.backdropPath,
                castList = details.castList,
                genres = details.genres,
                images = details.images,
                overview = details.overview,
                releaseDate = details.releaseDate,
                tagline = details.tagline,
                score = details.score,
                voteAveragePercentage = details.voteAveragePercentage,
                playTrailer = { playTrailer() }
            )
        }
    }
}