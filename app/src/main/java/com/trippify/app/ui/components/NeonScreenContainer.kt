package com.trippify.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.trippify.app.core.effects.ParallaxLayer

@Composable
fun NeonScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(animationSpec = tween(600)) + scaleIn(initialScale = 0.98f, animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 1.02f, animationSpec = tween(300))
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            ParallaxLayer()
            content()
        }
    }
}
