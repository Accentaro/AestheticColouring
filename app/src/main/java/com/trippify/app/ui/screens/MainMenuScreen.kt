package com.trippify.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.trippify.app.ads.AdManager
import com.trippify.app.ads.BannerAd
import com.trippify.app.ui.components.GlowingButton
import com.trippify.app.ui.components.NeonLogo
import com.trippify.app.ui.components.NeonScreenContainer
import com.trippify.app.R

@Composable
fun MainMenuScreen(
    onNavigateToPlayground: () -> Unit,
    onNavigateToColoring: () -> Unit,
    onNavigateToLoops: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDevTools: () -> Unit,
    hapticsEnabled: Boolean,
    adManager: AdManager,
    showAds: Boolean
) {
    NeonScreenContainer {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            val tapCount = remember { mutableIntStateOf(0) }
            val lastTapTimestamp = remember { mutableStateOf(0L) }

            LaunchedEffect(tapCount.intValue) {
                if (tapCount.intValue >= SECRET_TAPS_REQUIRED) {
                    tapCount.intValue = 0
                    onNavigateToDevTools()
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                NeonLogo(
                    onSecretTap = {
                        val now = System.currentTimeMillis()
                        if (now - lastTapTimestamp.value > SECRET_TAP_RESET_MS) {
                            tapCount.intValue = 0
                        }
                        lastTapTimestamp.value = now
                        tapCount.intValue += 1
                    }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GlowingButton(label = stringResource(R.string.menu_playground), hapticsEnabled = hapticsEnabled, onClick = onNavigateToPlayground)
                    GlowingButton(label = stringResource(R.string.menu_coloring), hapticsEnabled = hapticsEnabled, onClick = onNavigateToColoring)
                    GlowingButton(label = stringResource(R.string.menu_loops), hapticsEnabled = hapticsEnabled, onClick = onNavigateToLoops)
                    GlowingButton(label = stringResource(R.string.menu_settings), hapticsEnabled = hapticsEnabled, onClick = onNavigateToSettings)
                }
                if (showAds) {
                    BannerAd(
                        modifier = Modifier.padding(top = 24.dp),
                        adManager = adManager
                    )
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

private const val SECRET_TAPS_REQUIRED = 5
private const val SECRET_TAP_RESET_MS = 2500L
