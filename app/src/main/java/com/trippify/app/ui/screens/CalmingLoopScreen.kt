package com.trippify.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.trippify.app.audio.SoundscapeEngine
import com.trippify.app.audio.defaultSoundscapes
import com.trippify.app.common.ShareUtils
import com.trippify.app.core.theme.LocalGlowSpec
import com.trippify.app.ui.components.GlowingButton
import com.trippify.app.ui.components.NeonScreenContainer
import com.trippify.app.ui.components.NeonToggle
import com.trippify.app.R

@Composable
fun CalmingLoopScreen(
    soundscapeEngine: SoundscapeEngine,
    selectedSoundscapeId: String,
    onSoundscapeSelected: (String) -> Unit,
    isPremium: Boolean,
    onRequestPremium: () -> Unit
) {
    val glowSpec = LocalGlowSpec.current
    val soundscapes = remember { defaultSoundscapes() }
    val activeSoundscape = soundscapes.firstOrNull { it.id == selectedSoundscapeId } ?: soundscapes.first()
    var playing by remember { mutableStateOf(true) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    val context = LocalContext.current

    LaunchedEffect(activeSoundscape.id) {
        onSoundscapeSelected(activeSoundscape.id)
    }

    LaunchedEffect(activeSoundscape.id, playing) {
        if (playing) {
            soundscapeEngine.play(activeSoundscape)
        } else {
            soundscapeEngine.stop()
        }
    }

    LaunchedEffect(volume) {
        soundscapeEngine.setVolume(volume)
    }

    DisposableEffect(Unit) {
        onDispose { soundscapeEngine.stop() }
    }

    NeonScreenContainer {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.calming_loops_title),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.calming_loops_subtitle),
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    GlowingButton(
                        label = stringResource(R.string.calming_loops_share),
                        hapticsEnabled = false,
                        onClick = { ShareUtils.shareScreenshotPlaceholder(context, "Calming Loop") }
                    )
                }
                soundscapes.forEach { soundscape ->
                    val locked = soundscape.requiresPremium && !isPremium
                    NeonToggle(
                        label = soundscape.title,
                        checked = soundscape.id == activeSoundscape.id,
                        description = soundscape.description,
                        enabled = !locked,
                        locked = locked,
                        lockLabel = stringResource(R.string.calming_loops_premium_exclusive),
                        onCheckedChange = {
                            onSoundscapeSelected(soundscape.id)
                            if (playing) {
                                soundscapeEngine.play(soundscape)
                            }
                        },
                        onLockedClick = onRequestPremium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.calming_loops_master_volume),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = volume,
                    onValueChange = { volume = it },
                    colors = SliderDefaults.colors(
                        thumbColor = glowSpec.gradient.colors.first(),
                        activeTrackColor = glowSpec.gradient.colors.first().copy(alpha = 0.8f),
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (playing) {
                            stringResource(R.string.calming_loops_now_playing, activeSoundscape.title)
                        } else {
                            stringResource(R.string.calming_loops_paused)
                        },
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    GlowingButton(
                        label = if (playing) stringResource(R.string.calming_loops_pause) else stringResource(R.string.calming_loops_play),
                        onClick = {
                            playing = !playing
                            if (!playing) {
                                soundscapeEngine.stop()
                            } else {
                                soundscapeEngine.play(activeSoundscape)
                            }
                        }
                    )
                }
                if (!isPremium) {
                    Text(
                        text = stringResource(R.string.calming_loops_unlock_message),
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
