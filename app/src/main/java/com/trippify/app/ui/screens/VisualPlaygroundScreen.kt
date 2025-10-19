package com.trippify.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.mergeDescendants
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.trippify.app.common.ShareUtils
import com.trippify.app.audio.NeonAudioEngine
import com.trippify.app.scenes.SceneRegistry
import com.trippify.app.scenes.ripple.NeonRippleEffect
import com.trippify.app.scenes.ripple.NeonRippleScene
import com.trippify.app.ui.components.GlowingButton
import com.trippify.app.ui.components.NeonScreenContainer
import com.trippify.app.R

@Composable
fun VisualPlaygroundScreen(
    hapticsEnabled: Boolean,
    particleTrailsEnabled: Boolean,
    audioReactiveEnabled: Boolean,
    multiColorRipplesEnabled: Boolean,
    selectedSceneId: String,
    neonAudioEngine: NeonAudioEngine
) {
    val audioLevel by neonAudioEngine.levelLow.collectAsState(initial = 0f)
    val context = LocalContext.current

    LaunchedEffect(audioReactiveEnabled) {
        if (audioReactiveEnabled) {
            neonAudioEngine.startReactiveMode()
        } else {
            neonAudioEngine.stopReactiveMode()
        }
    }

    DisposableEffect(Unit) {
        onDispose { neonAudioEngine.stopReactiveMode() }
    }

    NeonScreenContainer {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics(mergeDescendants = true) {
                    contentDescription = stringResource(R.string.visual_playground_talkback)
                }
        ) {
            GlowingButton(
                label = stringResource(R.string.visual_playground_share),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                hapticsEnabled = false,
                onClick = { ShareUtils.shareScreenshotPlaceholder(context, "Visual Playground") }
            )
            val scene = SceneRegistry.findScene(selectedSceneId)
            when (scene?.id) {
                NeonRippleScene.id -> {
                    NeonRippleEffect(
                        hapticsEnabled = hapticsEnabled,
                        particleTrailsEnabled = particleTrailsEnabled,
                        audioReactiveLevel = if (audioReactiveEnabled) audioLevel else 0f,
                        multiColorEnabled = multiColorRipplesEnabled
                    )
                }
                null -> NeonRippleEffect(hapticsEnabled = hapticsEnabled)
                else -> scene.Preview()
            }
            Text(
                text = stringResource(R.string.visual_playground_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )
        }
    }
}
