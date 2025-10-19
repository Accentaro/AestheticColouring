package com.trippify.app.audio

import android.media.audiofx.Visualizer
import com.trippify.app.common.CrashReporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

/**
 * Captures live audio energy to drive neon visualisations. In production the implementation
 * should bridge into a dedicated audio analysis pipeline (Visualizer, AudioRecord, or a custom
 * DSP). The scaffold keeps things resilient when permissions are denied by falling back to a
 * synthetic pulse so the UI never locks up.
 */
open class NeonAudioEngine(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _levelLow = MutableStateFlow(0f)
    private val _levelMid = MutableStateFlow(0f)
    private val _levelHigh = MutableStateFlow(0f)

    val levelLow: StateFlow<Float> = _levelLow.asStateFlow()
    val levelMid: StateFlow<Float> = _levelMid.asStateFlow()
    val levelHigh: StateFlow<Float> = _levelHigh.asStateFlow()

    private var visualizer: Visualizer? = null
    private var monitoringJob: Job? = null
    private var permissionGranted: Boolean = false
    private val debugOverrideEnabled = MutableStateFlow(false)
    private val debugLevels = MutableStateFlow(Triple(0.4f, 0.6f, 0.3f))

    fun updatePermission(granted: Boolean) {
        permissionGranted = granted
        if (!granted) {
            stopReactiveMode()
        }
    }

    open fun startReactiveMode() {
        if (monitoringJob != null) return
        monitoringJob = scope.launch {
            if (permissionGranted) {
                startVisualizer()
            }
            // Fallback synthetic pulse so designers can iterate before hooking real audio.
            var tick = 0
            while (isActive) {
                if (debugOverrideEnabled.value) {
                    val (low, mid, high) = debugLevels.value
                    setLevels(low, mid, high)
                } else if (!permissionGranted || visualizer == null) {
                    val low = (0.3f + 0.2f * kotlin.math.sin(tick / 8f)).coerceIn(0f, 1f)
                    val mid = (0.2f + 0.15f * kotlin.math.sin(tick / 10f + 1)).coerceIn(0f, 1f)
                    val high = (0.1f + 0.1f * kotlin.math.sin(tick / 12f + 2)).coerceIn(0f, 1f)
                    setLevels(low, mid, high)
                }
                tick++
                delay(32)
            }
        }
    }

    fun enableDebugOverride(enable: Boolean) {
        debugOverrideEnabled.value = enable
        if (!enable) {
            debugLevels.value = Triple(0.4f, 0.6f, 0.3f)
        }
    }

    fun setDebugLevels(low: Float, mid: Float, high: Float) {
        debugLevels.value = Triple(
            low.coerceIn(0f, 1f),
            mid.coerceIn(0f, 1f),
            high.coerceIn(0f, 1f)
        )
        if (debugOverrideEnabled.value) {
            val (l, m, h) = debugLevels.value
            setLevels(l, m, h)
        }
    }

    fun isDebugOverrideActive(): Boolean = debugOverrideEnabled.value

    fun isMonitoring(): Boolean = monitoringJob != null

    private fun setLevels(low: Float, mid: Float, high: Float) {
        _levelLow.value = low
        _levelMid.value = mid
        _levelHigh.value = high
    }

    private fun startVisualizer() {
        if (visualizer != null) return
        try {
            visualizer = Visualizer(0).apply {
                enabled = false
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(
                    object : Visualizer.OnDataCaptureListener {
                        override fun onFftDataCapture(
                            visualizer: Visualizer?,
                            fft: ByteArray?,
                            samplingRate: Int
                        ) {
                            if (fft == null) return
                            val bins = fft.size / 2
                            if (bins <= 0) return
                            var low = 0f
                            var mid = 0f
                            var high = 0f
                            for (i in 2 until bins) {
                                val magnitude = abs(fft[i].toInt()).toFloat()
                                val energy = if (magnitude <= 0) 0f else log10(magnitude)
                                when {
                                    i < bins * 0.25f -> low = max(low, energy)
                                    i < bins * 0.6f -> mid = max(mid, energy)
                                    else -> high = max(high, energy)
                                }
                            }
                            val normalizer = 5f
                            setLevels(
                                (low / normalizer).coerceIn(0f, 1f),
                                (mid / normalizer).coerceIn(0f, 1f),
                                (high / normalizer).coerceIn(0f, 1f)
                            )
                        }

                        override fun onWaveFormDataCapture(
                            visualizer: Visualizer?,
                            waveform: ByteArray?,
                            samplingRate: Int
                        ) {
                            if (waveform == null) return
                            val peak = waveform.maxOf { abs(it.toInt()) }
                            val level = (peak / 128f).coerceIn(0f, 1f)
                            setLevels(level, level * 0.8f, level * 0.6f)
                        }
                    },
                    Visualizer.getMaxCaptureRate() / 2,
                    true,
                    true
                )
                enabled = true
            }
        } catch (error: Throwable) {
            // Devices without audio capture support (or denied permissions) fall back silently.
            visualizer = null
            CrashReporter.log(error, "Visualizer initialisation failed")
        }
    }

    open fun stopReactiveMode() {
        monitoringJob?.cancel()
        monitoringJob = null
        releaseVisualizer()
        setLevels(0f, 0f, 0f)
    }

    private fun releaseVisualizer() {
        visualizer?.let {
            try {
                it.release()
            } catch (error: Throwable) {
                CrashReporter.log(error, "Visualizer release failed")
            }
        }
        visualizer = null
    }
}
