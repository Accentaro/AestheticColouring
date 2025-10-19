package com.trippify.app.core.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.trippify.app.core.config.AppConfiguration

private val DarkNeonColorScheme = darkColorScheme(
    primary = AppConfiguration.neonTheme.primaryAccent,
    secondary = AppConfiguration.neonTheme.secondaryAccent,
    tertiary = AppConfiguration.neonTheme.tertiaryAccent,
    background = AppConfiguration.neonTheme.background,
    surface = Color(0xFF0D0D0D),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Immutable
data class GlowSpec(
    val gradient: Brush,
    val intensity: Float,
    val animationSpeedMultiplier: Float
)

val LocalGlowSpec = staticCompositionLocalOf {
    GlowSpec(
        gradient = Brush.linearGradient(
            listOf(
                AppConfiguration.neonTheme.primaryAccent.copy(alpha = 0.85f),
                AppConfiguration.neonTheme.secondaryAccent.copy(alpha = 0.65f)
            )
        ),
        intensity = 0.5f,
        animationSpeedMultiplier = 1f
    )
}

@Composable
fun TrippifyTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkNeonColorScheme else DarkNeonColorScheme
    val glowSpec = GlowSpec(
        gradient = Brush.radialGradient(
            listOf(
                AppConfiguration.neonTheme.primaryAccent.copy(alpha = 0.7f),
                AppConfiguration.neonTheme.secondaryAccent.copy(alpha = 0.2f),
                Color.Transparent
            )
        ),
        intensity = AppConfiguration.neonTheme.glowIntensity,
        animationSpeedMultiplier = AppConfiguration.neonTheme.animationSpeedMultiplier
    )

    CompositionLocalProvider(LocalGlowSpec provides glowSpec) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun InfiniteTransition.glowBreathingAnimation(): Float = animateFloat(
    initialValue = 0.9f,
    targetValue = 1.1f,
    animationSpec = repeatable(
        iterations = InfiniteTransition.INFINITE,
        animation = tween(
            durationMillis = (2400 / AppConfiguration.neonTheme.animationSpeedMultiplier).toInt(),
            easing = FastOutSlowInEasing
        )
    )
).value

@Composable
fun rememberGlowBreathingTransition(): Pair<InfiniteTransition, Float> {
    val transition = rememberInfiniteTransition(label = "GlowBreathing")
    val value = transition.glowBreathingAnimation()
    return transition to value
}
