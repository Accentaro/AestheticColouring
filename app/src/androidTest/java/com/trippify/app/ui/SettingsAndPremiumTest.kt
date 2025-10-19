package com.trippify.app.ui

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.trippify.app.MainActivity
import com.trippify.app.R
import com.trippify.app.fakes.FakeAmbientAudioEngine
import com.trippify.app.fakes.FakeNeonAudioEngine
import com.trippify.app.fakes.FakeSoundscapeEngine
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsAndPremiumTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @BindValue
    @JvmField
    val fakeNeonAudioEngine: FakeNeonAudioEngine = FakeNeonAudioEngine()

    @BindValue
    @JvmField
    val fakeSoundscapeEngine: FakeSoundscapeEngine = FakeSoundscapeEngine()

    @BindValue
    @JvmField
    val fakeAmbientAudioEngine: FakeAmbientAudioEngine = FakeAmbientAudioEngine()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun hapticsTogglePersistsAfterActivityRecreation() {
        openSettings()
        val toggleTag = "settings_toggle_haptics"
        composeRule.onNodeWithTag(toggleTag).performClick()
        composeRule.waitForIdle()
        composeRule.activityRule.scenario.recreate()
        openSettings()
        val expected = composeRule.getString(R.string.settings_toggle_haptics) + " off"
        composeRule.onNodeWithTag(toggleTag)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, expected))
    }

    @Test
    fun premiumOverrideUnlocksMulticolorToggle() {
        openSettings()
        val toggleTag = "settings_toggle_multicolor"
        val lockedDescription = composeRule.getString(R.string.settings_toggle_multicolor_locked) + " locked"
        composeRule.onNodeWithTag(toggleTag)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, lockedDescription))
        composeRule.onNodeWithText(composeRule.getString(R.string.settings_developer_tools)).performClick()
        composeRule.onNodeWithText(composeRule.getString(R.string.devtools_force_premium)).performClick()
        composeRule.waitForIdle()
        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        val unlockedDescription = composeRule.getString(R.string.settings_toggle_multicolor) + " on"
        composeRule.onNodeWithTag(toggleTag)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, unlockedDescription))
    }

    private fun openSettings() {
        composeRule.onNodeWithText(composeRule.getString(R.string.menu_settings)).performClick()
    }
}

private fun androidx.compose.ui.test.junit4.AndroidComposeTestRule<*, *>.getString(resId: Int): String = activity.getString(resId)
