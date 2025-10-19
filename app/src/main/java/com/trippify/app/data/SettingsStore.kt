package com.trippify.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trippify.app.core.config.AppConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.trippifyDataStore by preferencesDataStore(name = "trippify_settings")

/**
 * Central persistence entry point for Trippify user preferences. The backing store uses
 * [androidx.datastore.preferences.core.DataStore] so values survive process death and future
 * migrations. All keys are surfaced through a structured [SettingsSnapshot].
 */
class SettingsStore(private val context: Context) : PremiumSettingsGateway {
    private val dataStore get() = context.trippifyDataStore

    override val settings: Flow<SettingsSnapshot> = dataStore.data.map { preferences ->
        SettingsSnapshot(
            hapticsEnabled = preferences[KEY_HAPTICS_ENABLED]
                ?: AppConfiguration.hapticsEnabledByDefault,
            audioEnabled = preferences[KEY_AUDIO_ENABLED]
                ?: AppConfiguration.audioEnabledByDefault,
            particleTrailsEnabled = preferences[KEY_PARTICLE_TRAILS] ?: false,
            audioReactiveEnabled = preferences[KEY_AUDIO_REACTIVE] ?: true,
            multiColorRipples = preferences[KEY_MULTI_COLOR] ?: true,
            selectedSceneId = preferences[KEY_SELECTED_SCENE]
                ?: AppConfiguration.defaultSceneId,
            lastSoundscapeId = preferences[KEY_LAST_SOUNDSCAPE] ?: DEFAULT_SOUNDSCAPE,
            premiumUnlocked = preferences[KEY_PREMIUM_UNLOCKED] ?: false,
            adsEnabled = preferences[KEY_ADS_ENABLED] ?: true
        )
    }

    suspend fun setHapticsEnabled(enabled: Boolean) = updatePreference(KEY_HAPTICS_ENABLED, enabled)
    suspend fun setAudioEnabled(enabled: Boolean) = updatePreference(KEY_AUDIO_ENABLED, enabled)
    suspend fun setParticleTrails(enabled: Boolean) = updatePreference(KEY_PARTICLE_TRAILS, enabled)
    suspend fun setAudioReactive(enabled: Boolean) = updatePreference(KEY_AUDIO_REACTIVE, enabled)
    suspend fun setMultiColor(enabled: Boolean) = updatePreference(KEY_MULTI_COLOR, enabled)
    suspend fun setSelectedScene(sceneId: String) = updatePreference(KEY_SELECTED_SCENE, sceneId)
    suspend fun setLastSoundscape(soundscapeId: String) = updatePreference(KEY_LAST_SOUNDSCAPE, soundscapeId)
    override suspend fun setPremiumUnlocked(unlocked: Boolean) = updatePreference(KEY_PREMIUM_UNLOCKED, unlocked)
    suspend fun setAdsEnabled(enabled: Boolean) = updatePreference(KEY_ADS_ENABLED, enabled)

    suspend fun getSnapshot(): SettingsSnapshot = settings.first()

    suspend fun applySnapshot(snapshot: SettingsSnapshot) {
        dataStore.edit { prefs ->
            prefs[KEY_HAPTICS_ENABLED] = snapshot.hapticsEnabled
            prefs[KEY_AUDIO_ENABLED] = snapshot.audioEnabled
            prefs[KEY_PARTICLE_TRAILS] = snapshot.particleTrailsEnabled
            prefs[KEY_AUDIO_REACTIVE] = snapshot.audioReactiveEnabled
            prefs[KEY_MULTI_COLOR] = snapshot.multiColorRipples
            prefs[KEY_SELECTED_SCENE] = snapshot.selectedSceneId
            prefs[KEY_LAST_SOUNDSCAPE] = snapshot.lastSoundscapeId
            prefs[KEY_PREMIUM_UNLOCKED] = snapshot.premiumUnlocked
            prefs[KEY_ADS_ENABLED] = snapshot.adsEnabled
        }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    private suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    /**
     * Utility helper to update boolean keys from UI callbacks without leaking coroutine scope
     * management to callers.
     */
    fun updateBooleanAsync(scope: CoroutineScope, key: Preferences.Key<Boolean>, value: Boolean) {
        scope.launch { dataStore.edit { prefs -> prefs[key] = value } }
    }

    companion object {
        const val DEFAULT_SOUNDSCAPE = "soundscape_neon_hum"

        private val KEY_HAPTICS_ENABLED = booleanPreferencesKey("pref_haptics_enabled")
        private val KEY_AUDIO_ENABLED = booleanPreferencesKey("pref_audio_enabled")
        private val KEY_PARTICLE_TRAILS = booleanPreferencesKey("pref_particle_trails")
        private val KEY_AUDIO_REACTIVE = booleanPreferencesKey("pref_audio_reactive")
        private val KEY_MULTI_COLOR = booleanPreferencesKey("pref_multicolor_ripples")
        private val KEY_PREMIUM_UNLOCKED = booleanPreferencesKey("pref_premium_unlocked")
        private val KEY_ADS_ENABLED = booleanPreferencesKey("pref_ads_enabled")

        private val KEY_SELECTED_SCENE = stringPreferencesKey("pref_selected_scene")
        private val KEY_LAST_SOUNDSCAPE = stringPreferencesKey("pref_last_soundscape")
    }
}

/**
 * Immutable snapshot of persisted settings. The UI layer binds directly to this via
 * [SettingsStore.settings].
 */
data class SettingsSnapshot(
    val hapticsEnabled: Boolean,
    val audioEnabled: Boolean,
    val particleTrailsEnabled: Boolean,
    val audioReactiveEnabled: Boolean,
    val multiColorRipples: Boolean,
    val selectedSceneId: String,
    val lastSoundscapeId: String,
    val premiumUnlocked: Boolean,
    val adsEnabled: Boolean
)

interface PremiumSettingsGateway {
    val settings: Flow<SettingsSnapshot>
    suspend fun setPremiumUnlocked(unlocked: Boolean)
}
