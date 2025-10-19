package com.trippify.app.core.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.trippify.app.core.config.AppConfiguration
import com.trippify.app.data.SettingsSnapshot
import com.trippify.app.data.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Stateful holder that bridges persisted [SettingsStore] data with Compose UI state.
 * It exposes snapshot-backed mutable states for quick reads while persisting any
 * mutations back to the underlying DataStore.
 */
class AppSettingsState(
    private val scope: CoroutineScope,
    private val settingsStore: SettingsStore
) {
    var hapticsEnabled by mutableStateOf(AppConfiguration.hapticsEnabledByDefault)
        private set
    var audioEnabled by mutableStateOf(AppConfiguration.audioEnabledByDefault)
        private set
    var particleTrailsEnabled by mutableStateOf(false)
        private set
    var audioReactiveEnabled by mutableStateOf(true)
        private set
    var multiColorRipplesEnabled by mutableStateOf(true)
        private set
    var selectedSceneId by mutableStateOf(AppConfiguration.defaultSceneId)
        private set
    var lastSoundscapeId by mutableStateOf(SettingsStore.DEFAULT_SOUNDSCAPE)
        private set
    var isPremium by mutableStateOf(false)
        private set
    var adsEnabled by mutableStateOf(true)
        private set

    init {
        scope.launch {
            settingsStore.settings.collectLatest { snapshot ->
                applySnapshot(snapshot)
            }
        }
    }

    private fun applySnapshot(snapshot: SettingsSnapshot) {
        hapticsEnabled = snapshot.hapticsEnabled
        audioEnabled = snapshot.audioEnabled
        particleTrailsEnabled = snapshot.particleTrailsEnabled
        audioReactiveEnabled = snapshot.audioReactiveEnabled
        multiColorRipplesEnabled = snapshot.multiColorRipples
        selectedSceneId = snapshot.selectedSceneId
        lastSoundscapeId = snapshot.lastSoundscapeId
        isPremium = snapshot.premiumUnlocked
        adsEnabled = snapshot.adsEnabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        if (hapticsEnabled == enabled) return
        hapticsEnabled = enabled
        scope.launch { settingsStore.setHapticsEnabled(enabled) }
    }

    fun setAudioEnabled(enabled: Boolean) {
        if (audioEnabled == enabled) return
        audioEnabled = enabled
        scope.launch { settingsStore.setAudioEnabled(enabled) }
    }

    fun setParticleTrailsEnabled(enabled: Boolean) {
        if (particleTrailsEnabled == enabled) return
        particleTrailsEnabled = enabled
        scope.launch { settingsStore.setParticleTrails(enabled) }
    }

    fun setAudioReactiveEnabled(enabled: Boolean) {
        if (audioReactiveEnabled == enabled) return
        audioReactiveEnabled = enabled
        scope.launch { settingsStore.setAudioReactive(enabled) }
    }

    fun setMultiColorRipplesEnabled(enabled: Boolean) {
        if (multiColorRipplesEnabled == enabled) return
        multiColorRipplesEnabled = enabled
        scope.launch { settingsStore.setMultiColor(enabled) }
    }

    fun setSelectedScene(sceneId: String) {
        if (selectedSceneId == sceneId) return
        selectedSceneId = sceneId
        scope.launch { settingsStore.setSelectedScene(sceneId) }
    }

    fun setLastSoundscape(soundscapeId: String) {
        if (lastSoundscapeId == soundscapeId) return
        lastSoundscapeId = soundscapeId
        scope.launch { settingsStore.setLastSoundscape(soundscapeId) }
    }

    fun setPremiumUnlocked(unlocked: Boolean) {
        if (isPremium == unlocked) return
        isPremium = unlocked
        scope.launch {
            settingsStore.setPremiumUnlocked(unlocked)
            if (unlocked) {
                settingsStore.setAdsEnabled(false)
            }
        }
    }

    fun setAdsEnabled(enabled: Boolean) {
        if (adsEnabled == enabled) return
        adsEnabled = enabled
        scope.launch { settingsStore.setAdsEnabled(enabled) }
    }
}
