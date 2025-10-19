package com.trippify.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.util.Log
import com.trippify.app.common.CrashReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Lightweight ambient audio scaffold that simulates a low-frequency drone and occasional piano notes.
 * // SCENE LOGIC STARTS HERE – DO NOT REMOVE
 * TODO: Replace synthetic sine generator with high fidelity assets or streaming audio.
 */
open class AmbientAudioEngine {
    private var playingJob: Job? = null
    private var noteJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private var enabled = false

    @Suppress("UNUSED_PARAMETER")
    open fun start(context: Context) {
        if (enabled) return
        enabled = true
        playingJob = scope.launch {
            runCatching {
                // TODO: Replace with ExoPlayer or MediaPlayer backed drone loop.
                simulateDrone()
            }.onFailure { CrashReporter.log(it, "Ambient drone failed") }
        }
        noteJob = scope.launch {
            runCatching { simulatePianoNotes() }
                .onFailure { CrashReporter.log(it, "Ambient piano note scheduler failed") }
        }
        Log.d(TAG, "Ambient audio engine started")
    }

    open fun stop() {
        enabled = false
        playingJob?.cancel()
        noteJob?.cancel()
        playingJob = null
        noteJob = null
        Log.d(TAG, "Ambient audio engine stopped")
    }

    private suspend fun simulateDrone() {
        val sampleRate = 44100
        val frequency = 110.0 // Placeholder low-frequency drone
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build(),
            bufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        runCatching {
            audioTrack.play()
            val buffer = ShortArray(bufferSize)
            var sampleIndex = 0
            while (enabled) {
                for (i in buffer.indices) {
                    val angle = 2.0 * PI * frequency * sampleIndex / sampleRate
                    buffer[i] = (sin(angle) * Short.MAX_VALUE * 0.2).toInt().toShort()
                    sampleIndex++
                }
                audioTrack.write(buffer, 0, buffer.size)
            }
        }.onFailure { CrashReporter.log(it, "Ambient drone streaming failed") }
        audioTrack.stop()
        audioTrack.release()
    }

    private suspend fun simulatePianoNotes() {
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 30)
        runCatching {
            while (enabled) {
                delay(6000)
                if (!enabled) break
                toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 800)
                // TODO: Replace DTMF tone with sampled piano note playback.
            }
        }.onFailure { CrashReporter.log(it, "Ambient tone scheduling failed") }
        toneGenerator.release()
    }

    companion object {
        private const val TAG = "AmbientAudioEngine"
    }
}
