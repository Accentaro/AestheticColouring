package com.trippify.app.scenes.ripple

import androidx.compose.runtime.Composable
import com.trippify.app.scenes.SceneDefinition

object NeonRippleScene : SceneDefinition {
    override val id: String = "neon_ripple"
    override val displayName: String = "Neon Ripple"
    override val description: String = "Touch-responsive neon particle ripples"

    @Composable
    override fun Preview() {
        NeonRippleEffect()
    }
}
