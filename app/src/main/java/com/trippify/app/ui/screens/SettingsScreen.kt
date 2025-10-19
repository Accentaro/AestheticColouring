package com.trippify.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.testTag
import com.trippify.app.BuildConfig
import com.trippify.app.ads.AdManager
import com.trippify.app.ads.BannerAd
import com.trippify.app.scenes.SceneRegistry
import com.trippify.app.ui.components.GlowingButton
import com.trippify.app.ui.components.NeonScreenContainer
import com.trippify.app.ui.components.NeonToggle
import com.trippify.app.ui.components.SceneSelector
import com.trippify.app.R

@Composable
fun SettingsScreen(
    hapticsEnabled: Boolean,
    audioEnabled: Boolean,
    particleTrailsEnabled: Boolean,
    audioReactiveEnabled: Boolean,
    multiColorEnabled: Boolean,
    selectedSceneId: String,
    isPremium: Boolean,
    onHapticsToggle: (Boolean) -> Unit,
    onAudioToggle: (Boolean) -> Unit,
    onParticleTrailsToggle: (Boolean) -> Unit,
    onAudioReactiveToggle: (Boolean) -> Unit,
    onMultiColorToggle: (Boolean) -> Unit,
    onSceneSelected: (String) -> Unit,
    onRequestPremium: () -> Unit,
    onSimulatePurchase: () -> Unit,
    onSimulateRevoke: () -> Unit,
    adManager: AdManager,
    showAds: Boolean,
    onBackupRequested: () -> Unit,
    onRestoreRequested: () -> Unit,
    onOpenDevTools: () -> Unit,
    transferStatusMessage: String?
) {
    NeonScreenContainer {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.settings_title),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(24.dp))
                NeonToggle(
                    modifier = Modifier.testTag("settings_toggle_haptics"),
                    label = stringResource(R.string.settings_toggle_haptics),
                    checked = hapticsEnabled,
                    description = stringResource(R.string.settings_toggle_haptics_desc),
                    onCheckedChange = onHapticsToggle
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeonToggle(
                    modifier = Modifier.testTag("settings_toggle_audio"),
                    label = stringResource(R.string.settings_toggle_audio),
                    checked = audioEnabled,
                    description = stringResource(R.string.settings_toggle_audio_desc),
                    onCheckedChange = onAudioToggle
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeonToggle(
                    modifier = Modifier.testTag("settings_toggle_trails"),
                    label = stringResource(R.string.settings_toggle_trails),
                    checked = particleTrailsEnabled,
                    description = stringResource(R.string.settings_toggle_trails_desc),
                    onCheckedChange = onParticleTrailsToggle
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeonToggle(
                    modifier = Modifier.testTag("settings_toggle_audio_reactive"),
                    label = stringResource(R.string.settings_toggle_audio_reactive),
                    checked = audioReactiveEnabled,
                    description = stringResource(R.string.settings_toggle_audio_reactive_desc),
                    onCheckedChange = onAudioReactiveToggle
                )
                Spacer(modifier = Modifier.height(12.dp))
                NeonToggle(
                    modifier = Modifier.testTag("settings_toggle_multicolor"),
                    label = stringResource(R.string.settings_toggle_multicolor),
                    checked = multiColorEnabled,
                    description = if (isPremium) {
                        stringResource(R.string.settings_toggle_multicolor_desc_premium)
                    } else {
                        stringResource(R.string.settings_toggle_multicolor_desc_locked)
                    },
                    enabled = isPremium,
                    locked = !isPremium,
                    lockLabel = stringResource(R.string.settings_toggle_multicolor_locked),
                    onCheckedChange = onMultiColorToggle,
                    onLockedClick = onRequestPremium
                )
                Spacer(modifier = Modifier.height(24.dp))
                SceneSelector(
                    scenes = SceneRegistry.allScenes(),
                    selectedSceneId = selectedSceneId,
                    onSceneSelected = { scene -> onSceneSelected(scene.id) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.settings_backup_title),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.settings_backup_description),
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlowingButton(
                        label = stringResource(R.string.settings_backup),
                        modifier = Modifier.weight(1f),
                        onClick = onBackupRequested
                    )
                    GlowingButton(
                        label = stringResource(R.string.settings_restore),
                        modifier = Modifier.weight(1f),
                        onClick = onRestoreRequested
                    )
                }
                transferStatusMessage?.let { status ->
                    Text(
                        text = status,
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isPremium) stringResource(R.string.settings_premium_unlocked) else stringResource(R.string.settings_premium_locked),
                    color = if (isPremium) Color(0xFF39FF14) else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                if (!isPremium) {
                    GlowingButton(
                        label = stringResource(R.string.settings_unlock_premium),
                        onClick = onRequestPremium
                    )
                } else {
                    Text(
                        text = stringResource(R.string.settings_premium_thanks),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                GlowingButton(
                    label = stringResource(R.string.settings_developer_tools),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenDevTools
                )
                if (BuildConfig.DEBUG) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NeonToggle(
                            label = stringResource(R.string.settings_debug_simulate),
                            checked = isPremium,
                            description = stringResource(R.string.settings_debug_simulate_desc),
                            onCheckedChange = { enabled ->
                                if (enabled) onSimulatePurchase() else onSimulateRevoke()
                            }
                        )
                    }
                }
                if (showAds) {
                    Spacer(modifier = Modifier.height(32.dp))
                    BannerAd(adManager = adManager)
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.settings_ads_disabled),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
