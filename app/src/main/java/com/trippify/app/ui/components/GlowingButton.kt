package com.trippify.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.trippify.app.core.theme.LocalGlowSpec

@Composable
fun GlowingButton(
    label: String,
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val glowSpec = LocalGlowSpec.current
    val pressedState = remember { mutableStateOf(false) }
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val intensity by animateFloatAsState(
        targetValue = if (pressedState.value) glowSpec.intensity * 1.4f else glowSpec.intensity,
        label = "GlowButtonIntensity"
    )

    Box(
        modifier = modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0x33000000))
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        listOf(
                            Color.Transparent,
                            glowSpec.gradient.colors.first().copy(alpha = intensity)
                        )
                    ),
                    alpha = intensity
                )
            }
            .semantics {
                role = Role.Button
                contentDescription = label
            }
            .clickable(
                onClick = {
                    pressedState.value = true
                    if (hapticsEnabled) {
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    }
                    onClick()
                    pressedState.value = false
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 14.dp)
                .blur(0.dp)
        )
    }
}
