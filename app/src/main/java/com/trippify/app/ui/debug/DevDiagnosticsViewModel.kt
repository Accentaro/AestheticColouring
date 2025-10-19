package com.trippify.app.ui.debug

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trippify.app.audio.NeonAudioEngine
import com.trippify.app.audio.SoundscapeEngine
import com.trippify.app.billing.PremiumManager
import com.trippify.app.data.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DevDiagnosticsViewModel @Inject constructor(
    private val premiumManager: PremiumManager,
    private val settingsStore: SettingsStore,
    private val neonAudioEngine: NeonAudioEngine,
    private val soundscapeEngine: SoundscapeEngine,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val micPermissionState = MutableStateFlow(readMicPermission())

    private val _uiState = MutableStateFlow(DevDiagnosticsUiState())
    val uiState: StateFlow<DevDiagnosticsUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                premiumManager.isPremium,
                settingsStore.settings,
                micPermissionState,
                tickerFlow(750L)
            ) { premium, snapshot, micGranted, _ ->
                DevDiagnosticsUiState(
                    premiumEnabled = premium,
                    adsActive = snapshot.adsEnabled && !premium,
                    micPermissionGranted = micGranted,
                    audioEngineMonitoring = neonAudioEngine.isMonitoring(),
                    soundscapeLooping = soundscapeEngine.isLooping(),
                    billingStatusLabel = when {
                        premiumManager.isOverrideActive() -> "Override"
                        else -> "Stub"
                    }
                )
            }.collectLatest { state ->
                _uiState.value = state
            }
        }
    }

    fun refresh() {
        micPermissionState.value = readMicPermission()
        _uiState.update { current ->
            current.copy(
                audioEngineMonitoring = neonAudioEngine.isMonitoring(),
                soundscapeLooping = soundscapeEngine.isLooping()
            )
        }
    }

    private fun readMicPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun tickerFlow(intervalMillis: Long) = flow {
        while (true) {
            emit(Unit)
            delay(intervalMillis)
        }
    }
}

data class DevDiagnosticsUiState(
    val premiumEnabled: Boolean = false,
    val adsActive: Boolean = false,
    val micPermissionGranted: Boolean = false,
    val audioEngineMonitoring: Boolean = false,
    val soundscapeLooping: Boolean = false,
    val billingStatusLabel: String = "Stub"
)
