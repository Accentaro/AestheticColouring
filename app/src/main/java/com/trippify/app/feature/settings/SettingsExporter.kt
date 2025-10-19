package com.trippify.app.feature.settings

import android.content.Context
import android.net.Uri
import com.trippify.app.data.SettingsSnapshot
import com.trippify.app.data.SettingsStore
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Handles serialising the current [SettingsSnapshot] to JSON so it can be backed up via the system
 * share sheet. Real builds may want to encrypt the payload or include additional metadata.
 */
class SettingsExporter(
    private val context: Context,
    private val settingsStore: SettingsStore
) {
    suspend fun exportTo(uri: Uri): Result<Unit> = runCatching {
        val snapshot = settingsStore.settings.first()
        val payload = buildPayload(snapshot)
        val json = payload.toString(INDENT_SPACES)
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(json.toByteArray(Charsets.UTF_8))
            stream.flush()
        } ?: error("Unable to open export destination")
    }

    private fun buildPayload(snapshot: SettingsSnapshot): JSONObject {
        val settingsJson = JSONObject().apply {
            put("hapticsEnabled", snapshot.hapticsEnabled)
            put("audioEnabled", snapshot.audioEnabled)
            put("particleTrailsEnabled", snapshot.particleTrailsEnabled)
            put("audioReactiveEnabled", snapshot.audioReactiveEnabled)
            put("multiColorRipples", snapshot.multiColorRipples)
            put("selectedSceneId", snapshot.selectedSceneId)
            put("lastSoundscapeId", snapshot.lastSoundscapeId)
            put("premiumUnlocked", snapshot.premiumUnlocked)
            put("adsEnabled", snapshot.adsEnabled)
        }
        return JSONObject().apply {
            put("version", CURRENT_VERSION)
            put("generatedAt", DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
            put("settings", settingsJson)
        }
    }

    companion object {
        private const val CURRENT_VERSION = 1
        private const val INDENT_SPACES = 2
    }
}
