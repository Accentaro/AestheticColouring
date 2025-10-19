package com.trippify.app.scenes

import androidx.compose.runtime.Composable

interface SceneDefinition {
    val id: String
    val displayName: String
    val description: String

    @Composable
    fun Preview()
}
