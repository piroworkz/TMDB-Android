package com.davidluna.tmdb.app.main_ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.davidluna.tmdb.app.rules.MockWebServerRule
import com.davidluna.tmdb.media_framework.data.remote.model.RemoteMedia
import com.davidluna.tmdb.media_framework.data.remote.model.RemoteResults
import com.davidluna.tmdb.test_shared.reader.Reader
import com.davidluna.tmdb.test_shared.reader.Reader.UPCOMING_MOVIES
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TmdbAppEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mockWebServerRule = MockWebServerRule()

    @get:Rule(order = 2)
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, POST_NOTIFICATIONS)

    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val upcomingMoviesResponse =
        Reader.fromJson<RemoteResults<RemoteMedia>>(UPCOMING_MOVIES).results
    private val firstMovie = upcomingMoviesResponse.first()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testLoginAndNavigateToMovieDetails(): Unit =
        composeTestRule.run {
            val usernameTextField = onNodeWithContentDescription("Filled.Person")
            val passwordTextField = onNodeWithContentDescription("Filled.Lock")
            val loginButton = onNodeWithText("Login")

            usernameTextField.assertExists()
            passwordTextField.assertExists()
            loginButton.assertExists()

            usernameTextField.performTextInput("some_username@mail.com")
            passwordTextField.performTextInput("some_very_secret_and_safe_password_123")
            loginButton.performClick()

            waitUntilAtLeastOneExists(hasText(firstMovie.title.orEmpty()))

            onRoot(useUnmergedTree = true).printToLog("<-- CarouselImageView")
            onNode(hasAnyChild(hasContentDescription("CarouselImageView") and hasText(firstMovie.title.orEmpty())))
                .performClick()

            waitUntilAtLeastOneExists(hasText("Overview", substring = true, ignoreCase = true))

            onNodeWithText("Overview", substring = true, ignoreCase = true)
                .assertExists()
        }
}