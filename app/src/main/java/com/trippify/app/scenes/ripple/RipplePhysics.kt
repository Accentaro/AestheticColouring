package com.trippify.app.scenes.ripple

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Offset
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

internal const val RIPPLE_BASE_DURATION = 1600L
internal const val MAX_PRESS_BONUS_MS = 1200L
internal const val VELOCITY_BONUS_SCALE = 420f
internal const val AFTERGLOW_DURATION = 700L
internal const val TRAIL_PARTICLE_LIFETIME = 520L
internal const val TRAIL_PARTICLE_BASE_RADIUS = 36f

/**
 * Computes the lifetime of a ripple based on how long the user pressed and the velocity of their
 * motion. The longer the press or faster the swipe, the longer the neon wave lingers.
 */
@VisibleForTesting
internal fun calculateRippleLifetime(
    pressDurationMs: Long,
    velocityMagnitude: Float
): Long {
    val clampedPress = min(pressDurationMs, MAX_PRESS_BONUS_MS)
    val velocityBonus = (velocityMagnitude.coerceAtLeast(0f) * VELOCITY_BONUS_SCALE).toLong()
    return RIPPLE_BASE_DURATION + (clampedPress * 0.6f).toLong() + velocityBonus
}

/**
 * Converts a delta offset and time span into a velocity vector per millisecond. Consumers should
 * pass raw pointer deltas; the helper will guard against divide-by-zero states.
 */
internal fun calculateVelocity(delta: Offset, elapsedMs: Long): Offset {
    if (elapsedMs <= 0) return Offset.Zero
    return Offset(delta.x / elapsedMs, delta.y / elapsedMs)
}

/**
 * Derived helper for the magnitude of a velocity vector. Values are clamped to avoid extreme
 * bursts that could be produced by noisy pointer events on low-end devices.
 */
@VisibleForTesting
internal fun calculateVelocityMagnitude(velocity: Offset): Float {
    val magnitude = sqrt(velocity.x.pow(2) + velocity.y.pow(2))
    return magnitude.coerceIn(0f, 2.2f)
}

/**
 * Eases particle fade-outs with a smooth power curve so trails feel organic.
 */
internal fun trailAlpha(progress: Float): Float = (1f - progress.coerceIn(0f, 1f)).pow(1.5f)
