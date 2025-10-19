package com.trippify.app.ui

import androidx.compose.ui.test.fetchSemanticsNode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.waitUntil
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.trippify.app.MainActivity
import com.trippify.app.R
import com.trippify.app.fakes.FakeAmbientAudioEngine
import com.trippify.app.fakes.FakeNeonAudioEngine
import com.trippify.app.fakes.FakeSoundscapeEngine
import com.trippify.app.scenes.ripple.RIPPLE_CANVAS_TAG
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PlaygroundRippleInstrumentedTest {

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
    fun rippleAppearsAfterTap() {
        composeRule.onNodeWithText(composeRule.getString(R.string.menu_playground)).performClick()
        val rippleNode = composeRule.onNodeWithTag(RIPPLE_CANVAS_TAG)
        rippleNode.performTouchInput {
            down(center)
            advanceEventTime(100L)
            up()
        }
        composeRule.waitUntil(timeoutMillis = 2_000) {
            rippleNode.fetchSemanticsNode().config.getOrNull(SemanticsProperties.StateDescription)
                ?.let { !it.endsWith("0") } == true
        }
    }
}

private fun androidx.compose.ui.test.junit4.AndroidComposeTestRule<*, *>.getString(resId: Int): String = activity.getString(resId)
