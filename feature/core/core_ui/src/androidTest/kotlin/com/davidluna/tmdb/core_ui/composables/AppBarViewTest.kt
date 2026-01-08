package com.davidluna.tmdb.core_ui.composables

import android.annotation.SuppressLint
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AppBarViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @SuppressLint("CheckResult")
    @Test
    fun appBarPreView(): Unit = composeTestRule.run {
        setContent { AppBarPreView() }
        onRoot()
        val navigationButton = onNodeWithContentDescription("Filled.Menu")
        val title = onNodeWithText("Title")

        navigationButton.apply {
            assertExists()
            assertIsDisplayed()
            assertHasClickAction()
        }

        title.apply {
            assertExists()
            assertIsDisplayed()
        }

        navigationButton.performClick()
        onRoot()

        val navigationButtonAfterClick = onNodeWithContentDescription("AutoMirrored.Rounded.ArrowBack")

        navigationButtonAfterClick.apply {
            assertExists()
            assertIsDisplayed()
            assertHasClickAction()
        }
    }

}