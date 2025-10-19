package com.trippify.app.scenes.ripple

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RipplePhysicsTest {
    @Test
    fun `longer press increases ripple lifetime`() {
        val slow = calculateRippleLifetime(pressDurationMs = 50, velocityMagnitude = 0f)
        val longPress = calculateRippleLifetime(pressDurationMs = 1200, velocityMagnitude = 0f)
        assertTrue(longPress > slow)
    }

    @Test
    fun `velocity bonus clamps to avoid runaway values`() {
        val base = calculateRippleLifetime(pressDurationMs = 0, velocityMagnitude = 0f)
        val moderate = calculateRippleLifetime(pressDurationMs = 0, velocityMagnitude = 1f)
        val excessive = calculateRippleLifetime(pressDurationMs = 0, velocityMagnitude = 10f)
        // 1f velocity should add exactly one scale unit.
        assertEquals((VELOCITY_BONUS_SCALE * 1f).toLong(), moderate - base)
        // Upper bound should be capped at 2.2f inside the helper.
        assertTrue(excessive - base <= (VELOCITY_BONUS_SCALE * 2.2f).toLong())
    }

    @Test
    fun `calculate velocity guards divide by zero`() {
        val velocity = calculateVelocity(Offset(10f, 10f), elapsedMs = 0)
        assertEquals(Offset.Zero, velocity)
    }
}
