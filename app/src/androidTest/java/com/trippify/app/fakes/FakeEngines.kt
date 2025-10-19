package com.trippify.app.fakes

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.trippify.app.audio.AmbientAudioEngine
import com.trippify.app.audio.NeonAudioEngine
import com.trippify.app.audio.Soundscape
import com.trippify.app.audio.SoundscapeEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FakeNeonAudioEngine : NeonAudioEngine(CoroutineScope(Dispatchers.Unconfined)) {
    override fun startReactiveMode() {
        enableDebugOverride(true)
        setDebugLevels(0.5f, 0.5f, 0.5f)
    }

    override fun stopReactiveMode() {
        enableDebugOverride(false)
        setDebugLevels(0f, 0f, 0f)
    }
}

class FakeSoundscapeEngine : SoundscapeEngine(
    context = ApplicationProvider.getApplicationContext<Context>(),
    playerFactory = { throw IllegalStateException("Tests should not instantiate ExoPlayer") }
) {
    var lastPlayed: Soundscape? = null
    var playing: Boolean = false
    var volume: Float = 1f

    override fun play(soundscape: Soundscape, crossfadeMillis: Long) {
        lastPlayed = soundscape
        playing = true
    }

    override fun stop() {
        playing = false
    }

    override fun setVolume(volume: Float) {
        this.volume = volume
    }
}

class FakeAmbientAudioEngine : AmbientAudioEngine() {
    var started = false
    override fun start(context: Context) {
        started = true
    }

    override fun stop() {
        started = false
    }
}
