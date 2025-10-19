package com.trippify.app.core.config

import androidx.compose.ui.graphics.Color

/**
 * Centralized configuration surface that mirrors editable knobs listed in CONFIG.md.
 * Designers can tweak these values without hunting through the codebase.
 */
object AppConfiguration {
    var appName: String = "Trippify"

    val neonTheme = NeonThemeConfig(
        background = Color(0xFF050505),
        primaryAccent = Color(0xFFFF00FF),
        secondaryAccent = Color(0xFF00FFFF),
        tertiaryAccent = Color(0xFFAA00FF),
        glowIntensity = 0.75f,
        animationSpeedMultiplier = 1.0f
    )

    var bannerAdUnitId: String = "ca-app-pub-xxxxxxxxxxxxxxxx/banner"
    var audioEnabledByDefault: Boolean = true
    var hapticsEnabledByDefault: Boolean = true
    var defaultSceneId: String = "neon_ripple"
}

data class NeonThemeConfig(
    val background: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val glowIntensity: Float,
    val animationSpeedMultiplier: Float
)
