package io.zenandroid.greenfield.feed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FeedActivity>()

    @Test
    fun testActivityLoads() {
        composeTestRule.onNodeWithText("Public images").assertIsDisplayed()
    }
}