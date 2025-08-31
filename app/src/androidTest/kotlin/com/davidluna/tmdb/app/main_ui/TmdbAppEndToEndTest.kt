package com.davidluna.tmdb.app.main_ui

import android.Manifest
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.davidluna.tmdb.app.rules.MockWebServerRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TmdbAppEndToEndTest {

    @get:Rule(order = 0)
    val temporaryFolder = TemporaryFolder()
    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 3)
    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )

    @get:Rule(order = 4)
    val mockWebServerRule = MockWebServerRule()

    @Before
    fun setup() {
        permissionsRule
        temporaryFolder.delete()
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        temporaryFolder.delete()
    }

    @Test
    fun test_whenAppIsLaunchedForFirstTime_ShouldShowPermissionsPromptScreenAfterSplash_thenEnablePermissions_thenShowLoginScreen(): Unit =
        composeRule.run {
            onRoot(useUnmergedTree = true).printToLog("<-- ")
        }
}