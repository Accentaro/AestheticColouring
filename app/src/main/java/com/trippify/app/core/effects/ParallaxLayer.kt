package com.trippify.app.core.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.trippify.app.core.theme.LocalGlowSpec

/**
 * Decorative parallax layer that subtly responds to pointer movement. In future iterations this
 * can listen to motion sensors for tilt-based depth. The effect is kept lightweight so it never
 * blocks input on the main canvas.
 */
@Composable
fun ParallaxLayer(modifier: Modifier = Modifier) {
    val glowSpec = LocalGlowSpec.current
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    val size = size
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val relative = relativeOffset(down.position, center)
                    offsetX.animateTo(relative.x)
                    offsetY.animateTo(relative.y)
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: continue
                        val current = relativeOffset(change.position, center)
                        offsetX.snapTo(current.x)
                        offsetY.snapTo(current.y)
                        if (!change.pressed) {
                            offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
                            offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
                            break
                        }
                    }
                }
            }
    ) {
        val center = center + Offset(offsetX.value, offsetY.value) * 120f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    glowSpec.gradient.colors.first().copy(alpha = 0.35f)
                )
            ),
            radius = size.minDimension * 0.8f,
            center = center
        )
        drawCircle(
            color = glowSpec.gradient.colors.last().copy(alpha = 0.15f),
            radius = size.minDimension * 0.95f,
            center = center
        )
        // TODO: Integrate accelerometer tilt data for depth on supported devices.
    }
}

private fun relativeOffset(position: Offset, center: Offset): Offset {
    val relative = position - center
    return Offset(
        (relative.x / center.x).coerceIn(-1f, 1f),
        (relative.y / center.y).coerceIn(-1f, 1f)
    )
}
