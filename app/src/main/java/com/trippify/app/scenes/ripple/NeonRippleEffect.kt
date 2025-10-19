package com.trippify.app.scenes.ripple

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.awaitPointerEvent
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import com.trippify.app.core.theme.LocalGlowSpec
import com.trippify.app.scenes.ripple.AFTERGLOW_DURATION
import com.trippify.app.scenes.ripple.TRAIL_PARTICLE_BASE_RADIUS
import com.trippify.app.scenes.ripple.TRAIL_PARTICLE_LIFETIME
import com.trippify.app.scenes.ripple.calculateRippleLifetime
import com.trippify.app.scenes.ripple.calculateVelocity
import com.trippify.app.scenes.ripple.calculateVelocityMagnitude
import com.trippify.app.scenes.ripple.trailAlpha
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.max
import kotlin.math.pow

private data class Ripple(
    val id: Int,
    val origin: Offset,
    val timestamp: Long,
    val baseColor: Color,
    var pressDuration: Long = 0L,
    var velocity: Offset = Offset.Zero
)

private data class TrailParticle(
    val rippleId: Int,
    val position: Offset,
    val color: Color,
    val timestamp: Long
)

@Composable
fun NeonRippleEffect(
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    particleTrailsEnabled: Boolean = false,
    audioReactiveLevel: Float = 0f,
    multiColorEnabled: Boolean = true
) {
    val glowSpec = LocalGlowSpec.current
    val haptics = LocalHapticFeedback.current
    val ripples = remember { mutableStateListOf<Ripple>() }
    val trailParticles = remember { mutableStateListOf<TrailParticle>() }
    var frameTime by remember { mutableStateOf(0L) }
    var colorIndex by remember { mutableStateOf(0) }
    val colors = remember {
        listOf(
            Color(0xFF00FFFF),
            Color(0xFFFF00FF),
            Color(0xFFAA00FF),
            Color(0xFF39FF14),
            Color(0xFFF5FF00)
        )
    }
    var rippleId by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            frameTime = awaitFrame()
            ripples.removeAll { ripple ->
                val lifetime = rippleLifetime(ripple)
                frameTime - ripple.timestamp > lifetime + AFTERGLOW_DURATION
            }
            trailParticles.removeAll { particle ->
                frameTime - particle.timestamp > TRAIL_PARTICLE_LIFETIME
            }
        }
    }

    val view = LocalView.current

    // PLACEHOLDER: Drop Lottie JSON in /assets/lottie/neon_pulse.json and render via Compose
    // animation APIs once shader experimentation begins.

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag(RIPPLE_CANVAS_TAG)
            .semantics {
                stateDescription = "ripples:${ripples.size}"
            }
            .pointerInput(hapticsEnabled, particleTrailsEnabled, multiColorEnabled) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val downTime = System.currentTimeMillis()
                    val nextColor = if (multiColorEnabled) {
                        colors[colorIndex++ % colors.size]
                    } else {
                        colors.first()
                    }
                    val newRipple = Ripple(
                        id = rippleId++,
                        origin = down.position,
                        timestamp = downTime,
                        baseColor = nextColor
                    )
                    ripples.add(newRipple)
                    if (hapticsEnabled) {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    }
                    var lastPosition = down.position
                    var lastTimestamp = downTime
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: continue
                        val now = System.currentTimeMillis()
                        val delta = change.position - lastPosition
                        val dt = max(1L, now - lastTimestamp)
                        val velocity = calculateVelocity(delta, dt)
                        newRipple.velocity = velocity
                        if (particleTrailsEnabled) {
                            trailParticles += TrailParticle(
                                rippleId = newRipple.id,
                                position = change.position,
                                color = newRipple.baseColor,
                                timestamp = now
                            )
                        }
                        lastPosition = change.position
                        lastTimestamp = now
                        if (change.changedToUpIgnoreConsumed()) {
                            newRipple.pressDuration = now - downTime
                            if (hapticsEnabled) {
                                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.GestureEnd)
                            }
                            change.consume()
                            break
                        }
                        change.consume()
                    }
                    view.performClick()
                }
            }
    ) {
        val maxDimension = max(size.width, size.height)
        trailParticles.forEach { particle ->
            val age = frameTime - particle.timestamp
            val progress = (age / TRAIL_PARTICLE_LIFETIME.toFloat()).coerceIn(0f, 1f)
            val alpha = trailAlpha(progress)
            drawCircle(
                color = particle.color.copy(alpha = 0.45f * alpha),
                radius = TRAIL_PARTICLE_BASE_RADIUS * (1f - 0.5f * progress),
                center = particle.position
            )
        }
        ripples.forEach { ripple ->
            val elapsed = frameTime - ripple.timestamp
            val velocityMagnitude = calculateVelocityMagnitude(ripple.velocity)
            val lifetime = calculateRippleLifetime(ripple.pressDuration, velocityMagnitude)
            val afterglow = (elapsed - lifetime).coerceAtLeast(0L)
            val mainProgress = (elapsed / lifetime.toFloat()).coerceIn(0f, 1f)
            val afterglowFactor = 1f - (afterglow / AFTERGLOW_DURATION.toFloat()).coerceIn(0f, 1f)
            val audioBoost = 1f + audioReactiveLevel.coerceIn(0f, 1f) * 0.65f
            val radius = (mainProgress * maxDimension * (0.6f + velocityMagnitude * 0.4f)) * audioBoost
            val alpha = ((1f - mainProgress).pow(1.25f) * afterglowFactor)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ripple.baseColor.copy(alpha = alpha * glowSpec.intensity * (1.1f + audioReactiveLevel * 0.5f)),
                        ripple.baseColor.copy(alpha = alpha * 0.6f),
                        Color.Transparent
                    ),
                    center = ripple.origin,
                    radius = radius
                ),
                radius = radius,
                center = ripple.origin
            )
        }
    }
}

const val RIPPLE_CANVAS_TAG = "neon_ripple_canvas"
