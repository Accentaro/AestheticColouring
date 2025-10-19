package com.trippify.app.scenes

import com.trippify.app.scenes.ripple.NeonRippleScene

object SceneRegistry {
    private val scenes: List<SceneDefinition> = listOf(
        NeonRippleScene
        // TODO: Register additional scenes here as they are created.
    )

    fun allScenes(): List<SceneDefinition> = scenes

    fun findScene(id: String): SceneDefinition? = scenes.firstOrNull { it.id == id }
}
