package com.trippify.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.trippify.app.ui.components.NeonScreenContainer

@Composable
fun ColoringZoneScreen() {
    NeonScreenContainer {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // SCENE LOGIC STARTS HERE – DO NOT REMOVE
            // TODO: Effect logic here. Implement psychedelic drawing brushes reacting to multi-touch.
            drawRect(Color.Transparent)
        }
    }
}
