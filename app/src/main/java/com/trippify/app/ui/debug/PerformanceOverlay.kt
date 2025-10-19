package com.trippify.app.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import com.trippify.app.R
import kotlinx.coroutines.isActive
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.mutableStateOf

@Composable
fun PerformanceOverlay(modifier: Modifier = Modifier) {
    var fps by remember { mutableFloatStateOf(0f) }
    var recompositions by remember { mutableIntStateOf(0) }
    var frameCount by remember { mutableIntStateOf(0) }
    var frameWindowStart by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        frameWindowStart = 0L
        while (isActive) {
            val frameTime = withFrameNanos { it }
            if (frameWindowStart == 0L) {
                frameWindowStart = frameTime
            }
            frameCount++
            val elapsed = frameTime - frameWindowStart
            if (elapsed >= 1_000_000_000L) {
                fps = frameCount * 1_000_000_000f / elapsed
                frameCount = 0
                frameWindowStart = frameTime
            }
        }
    }

    SideEffect { recompositions++ }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.performance_overlay_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.performance_overlay_fps, fps),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.performance_overlay_recompositions, recompositions),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
