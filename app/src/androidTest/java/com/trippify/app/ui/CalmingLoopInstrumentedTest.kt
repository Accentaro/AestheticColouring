package com.trippify.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.trippify.app.MainActivity
import com.trippify.app.R
import com.trippify.app.audio.defaultSoundscapes
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
class CalmingLoopInstrumentedTest {

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
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun calmingLoopTogglesPlaybackState() {
        val loopsLabel = composeRule.getString(R.string.menu_loops)
        composeRule.onNodeWithText(loopsLabel).performClick()
        val pauseText = composeRule.getString(R.string.calming_loops_pause)
        composeRule.onNodeWithText(pauseText).performClick()
        composeRule.onNodeWithText(composeRule.getString(R.string.calming_loops_paused)).assertIsDisplayed()
        val playText = composeRule.getString(R.string.calming_loops_play)
        composeRule.onNodeWithText(playText).performClick()
        val nowPlaying = composeRule.getString(
            R.string.calming_loops_now_playing,
            defaultSoundscapes().first().title
        )
        composeRule.onNodeWithText(nowPlaying).assertIsDisplayed()
    }
}

private fun androidx.compose.ui.test.junit4.AndroidComposeTestRule<*, *>.getString(resId: Int): String = activity.getString(resId)
