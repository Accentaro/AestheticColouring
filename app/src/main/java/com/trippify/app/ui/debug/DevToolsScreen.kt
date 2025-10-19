package com.trippify.app.ui.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.trippify.app.ui.components.GlowingButton
import com.trippify.app.ui.components.NeonScreenContainer
import com.trippify.app.ui.components.NeonToggle
import com.trippify.app.R

@Composable
fun DevToolsScreen(
    premiumOverride: PremiumOverride,
    onOverrideChanged: (PremiumOverride) -> Unit,
    onSimulatePurchase: () -> Unit,
    onSimulateRevoke: () -> Unit,
    isFakeAudioEnabled: Boolean,
    onFakeAudioEnabled: (Boolean) -> Unit,
    onFakeAudioLevelChange: (Float, Float, Float) -> Unit,
    isPerformanceOverlayEnabled: Boolean,
    onPerformanceOverlayToggle: (Boolean) -> Unit,
    onResetDefaults: () -> Unit,
    diagnosticsState: DevDiagnosticsUiState,
    onRefreshDiagnostics: () -> Unit
) {
    NeonScreenContainer {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.devtools_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = stringResource(R.string.devtools_description),
                    color = Color.White.copy(alpha = 0.7f)
                )

                DiagnosticsStatusTable(
                    state = diagnosticsState,
                    onRefresh = onRefreshDiagnostics
                )

                Text(
                    text = stringResource(R.string.devtools_section_premium),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                NeonToggle(
                    label = stringResource(R.string.devtools_follow_purchases),
                    checked = premiumOverride == PremiumOverride.Realtime,
                    description = stringResource(R.string.devtools_follow_purchases_desc),
                    onCheckedChange = {
                        if (it) onOverrideChanged(PremiumOverride.Realtime)
                    }
                )
                NeonToggle(
                    label = stringResource(R.string.devtools_force_premium),
                    checked = premiumOverride == PremiumOverride.ForcedOn,
                    description = stringResource(R.string.devtools_force_premium_desc),
                    onCheckedChange = {
                        if (it) onOverrideChanged(PremiumOverride.ForcedOn) else onOverrideChanged(PremiumOverride.Realtime)
                    }
                )
                NeonToggle(
                    label = stringResource(R.string.devtools_force_locked),
                    checked = premiumOverride == PremiumOverride.ForcedOff,
                    description = stringResource(R.string.devtools_force_locked_desc),
                    onCheckedChange = {
                        if (it) onOverrideChanged(PremiumOverride.ForcedOff) else onOverrideChanged(PremiumOverride.Realtime)
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlowingButton(
                        label = stringResource(R.string.devtools_simulate_purchase),
                        modifier = Modifier.weight(1f),
                        onClick = onSimulatePurchase
                    )
                    GlowingButton(
                        label = stringResource(R.string.devtools_simulate_revoke),
                        modifier = Modifier.weight(1f),
                        onClick = onSimulateRevoke
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.devtools_section_audio),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                var lowLevel by remember { mutableStateOf(0.4f) }
                var midLevel by remember { mutableStateOf(0.6f) }
                var highLevel by remember { mutableStateOf(0.3f) }

                val onLevelsChanged: () -> Unit = {
                    if (isFakeAudioEnabled) {
                        onFakeAudioLevelChange(lowLevel, midLevel, highLevel)
                    }
                }

                NeonToggle(
                    label = stringResource(R.string.devtools_fake_levels),
                    checked = isFakeAudioEnabled,
                    description = stringResource(R.string.devtools_fake_levels_desc),
                    onCheckedChange = {
                        onFakeAudioEnabled(it)
                        if (it) {
                            onFakeAudioLevelChange(lowLevel, midLevel, highLevel)
                        }
                    }
                )

                LevelSlider(
                    label = stringResource(R.string.devtools_energy_label, "Low", (lowLevel * 100).toInt()),
                    value = lowLevel,
                    onValueChange = {
                        lowLevel = it
                        onLevelsChanged()
                    }
                )
                LevelSlider(
                    label = stringResource(R.string.devtools_energy_label, "Mid", (midLevel * 100).toInt()),
                    value = midLevel,
                    onValueChange = {
                        midLevel = it
                        onLevelsChanged()
                    }
                )
                LevelSlider(
                    label = stringResource(R.string.devtools_energy_label, "High", (highLevel * 100).toInt()),
                    value = highLevel,
                    onValueChange = {
                        highLevel = it
                        onLevelsChanged()
                    }
                )

                NeonToggle(
                    label = stringResource(R.string.devtools_performance_overlay),
                    checked = isPerformanceOverlayEnabled,
                    description = stringResource(R.string.devtools_performance_overlay_desc),
                    onCheckedChange = onPerformanceOverlayToggle
                )

                TextButton(onClick = { onOverrideChanged(PremiumOverride.Realtime) }) {
                    Text(stringResource(R.string.devtools_reset_overrides), color = Color.White.copy(alpha = 0.8f))
                }
                GlowingButton(
                    label = stringResource(R.string.devtools_reset_defaults),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onResetDefaults
                )
            }
        }
    }
}

@Composable
private fun DiagnosticsStatusTable(
    state: DevDiagnosticsUiState,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.devtools_section_diagnostics),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            TextButton(onClick = onRefresh) {
                Text(stringResource(R.string.devtools_refresh), color = Color.White.copy(alpha = 0.8f))
            }
        }

        StatusRow(
            label = stringResource(R.string.devtools_diagnostic_premium),
            value = state.premiumEnabled.toStatusEmoji()
        )
        StatusRow(
            label = stringResource(R.string.devtools_diagnostic_ads),
            value = state.adsActive.toStatusEmoji()
        )
        StatusRow(
            label = stringResource(R.string.devtools_diagnostic_mic),
            value = if (state.micPermissionGranted) stringResource(R.string.devtools_status_granted) else stringResource(R.string.devtools_status_denied)
        )
        StatusRow(
            label = stringResource(R.string.devtools_diagnostic_audio_monitoring),
            value = state.audioEngineMonitoring.toStatusEmoji()
        )
        StatusRow(
            label = stringResource(R.string.devtools_diagnostic_soundscape),
            value = state.soundscapeLooping.toStatusEmoji()
        )
        StatusRow(
            label = stringResource(R.string.devtools_diagnostic_billing),
            value = state.billingStatusLabel
        )
    }
}

@Composable
private fun StatusRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f))
        Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

private fun Boolean.toStatusEmoji(): String = if (this) "✅" else "❌"

@Composable
private fun LevelSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f)
        )
        androidx.compose.material3.Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}

enum class PremiumOverride {
    Realtime,
    ForcedOn,
    ForcedOff
}
