package com.trippify.app.audio

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.audio.AudioAttributes
import com.trippify.app.common.CrashReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Handles playback of ambient soundscapes with soft fades to avoid abrupt transitions. Developers
 * should replace the placeholder URIs with actual assets placed under `res/raw/` or streamed from
 * the network.
 */
open class SoundscapeEngine(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate),
    private val playerFactory: (() -> ExoPlayer)? = null
) {
    private val player: ExoPlayer by lazy {
        (playerFactory ?: {
            ExoPlayer.Builder(context)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.CONTENT_TYPE_MUSIC)
                        .build(),
                    true
                )
                .build()
        })()
    }
    private var fadeJob: Job? = null

    var currentSoundscape: Soundscape? = null
        private set

    open fun play(soundscape: Soundscape, crossfadeMillis: Long = DEFAULT_CROSSFADE) {
        if (currentSoundscape?.id == soundscape.id) return
        currentSoundscape = soundscape
        fadeJob?.cancel()
        fadeJob = scope.launch {
            runCatching {
                fadeVolume(target = 0f, durationMillis = crossfadeMillis / 2)
                player.setMediaItem(MediaItem.fromUri(Uri.parse(soundscape.uri)))
                player.repeatMode = ExoPlayer.REPEAT_MODE_ALL
                player.prepare()
                player.playWhenReady = true
                fadeVolume(target = 1f, durationMillis = max(300L, crossfadeMillis / 2))
            }.onFailure { CrashReporter.log(it, "Soundscape play failed for ${soundscape.id}") }
        }
    }

    open fun setVolume(volume: Float) {
        runCatching { player.volume = volume.coerceIn(0f, 1f) }
            .onFailure { CrashReporter.log(it, "Soundscape volume update failed") }
    }

    open fun stop() {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            runCatching {
                fadeVolume(target = 0f, durationMillis = 400L)
                player.pause()
            }.onFailure { CrashReporter.log(it, "Soundscape stop failed") }
        }
    }

    open fun release() {
        fadeJob?.cancel()
        runCatching { player.release() }
            .onFailure { CrashReporter.log(it, "Soundscape release failed") }
    }

    protected open suspend fun fadeVolume(target: Float, durationMillis: Long) {
        val start = player.volume
        val steps = 12
        if (durationMillis <= 0) {
            player.volume = target
            return
        }
        val stepDuration = durationMillis / steps
        for (i in 1..steps) {
            val fraction = i / steps.toFloat()
            player.volume = start + (target - start) * fraction
            delay(max(16L, stepDuration))
        }
    }

    open fun isLooping(): Boolean = runCatching { player.isPlaying }.getOrDefault(false)

    companion object {
        private const val DEFAULT_CROSSFADE = 1800L
    }
}

/**
 * Domain model describing a soundscape option.
 */
data class Soundscape(
    val id: String,
    val title: String,
    val description: String,
    val uri: String,
    val requiresPremium: Boolean = false
)

fun defaultSoundscapes(): List<Soundscape> = listOf(
    Soundscape(
        id = "soundscape_neon_hum",
        title = "Neon Hum",
        description = "Gentle low-frequency glow.",
        uri = "asset://neon_hum"
    ),
    Soundscape(
        id = "soundscape_electric_rain",
        title = "Electric Rain",
        description = "Sparkling plucks drifting in stereo.",
        uri = "asset://electric_rain",
        requiresPremium = true
    ),
    Soundscape(
        id = "soundscape_crystal_echoes",
        title = "Crystal Echoes",
        description = "Glass-like pads for deep focus.",
        uri = "asset://crystal_echoes",
        requiresPremium = true
    )
)
// TODO: Place actual loopable audio files in `app/src/main/res/raw/` and update the URIs.
