package com.davidluna.tmdb.core_ui.composables

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import org.junit.Rule
import org.junit.Test

class ErrorDialogViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorDialogView(): Unit = composeTestRule.run {
        setContent { ErrorDialogPreView() }
        onRoot(true)

        val dialogTitleText = onNodeWithText("Something went wrong")
        val dialogMessageText = onNodeWithText("Invalid API key: You must be granted a valid key.")
        val dialogDismissButton = onNodeWithText("DISMISS")

        dialogTitleText.apply {
            assertExists()
            assertIsDisplayed()
        }

        dialogMessageText.apply {
            assertExists()
            assertIsDisplayed()
        }

        dialogDismissButton.apply {
            assertExists()
            assertIsDisplayed()
            assertHasClickAction()
        }

        dialogDismissButton.performClick()

        onRoot(useUnmergedTree = true).assert(hasAnyChild(isRoot()).not())
    }

}