package com.trippify.app.core.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.trippify.app.core.config.AppConfiguration
import com.trippify.app.core.state.rememberAppState
import com.trippify.app.core.theme.TrippifyTheme
import com.trippify.app.feature.settings.SettingsExporter
import com.trippify.app.feature.settings.SettingsImporter
import com.trippify.app.ui.debug.DevDiagnosticsViewModel
import com.trippify.app.ui.debug.DevToolsScreen
import com.trippify.app.ui.debug.PremiumOverride
import com.trippify.app.ui.debug.PerformanceOverlay
import com.trippify.app.ui.screens.CalmingLoopScreen
import com.trippify.app.ui.screens.ColoringZoneScreen
import com.trippify.app.ui.screens.MainMenuScreen
import com.trippify.app.ui.screens.SettingsScreen
import com.trippify.app.ui.screens.VisualPlaygroundScreen
import com.trippify.app.R
import com.trippify.app.BuildConfig
import com.trippify.app.common.CrashReporter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TrippifyApp() {
    TrippifyTheme {
        val appState = rememberAppState()
        val context = LocalContext.current
        val activity = remember(context) { context.findActivity() }
        val coroutineScope = rememberCoroutineScope()
        val navController = appState.navController
        val settings = appState.settings
        val premiumStatus by appState.premiumManager.isPremium.collectAsState(initial = settings.isPremium)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showAds = settings.adsEnabled && !premiumStatus
        var transferStatusMessage by remember { mutableStateOf<String?>(null) }
        var premiumOverrideState by remember { mutableStateOf(PremiumOverride.Realtime) }
        var fakeAudioEnabled by remember { mutableStateOf(appState.neonAudioEngine.isDebugOverrideActive()) }
        var performanceOverlayEnabled by remember { mutableStateOf(false) }

        val exporter = remember { SettingsExporter(context, appState.settingsStore) }
        val importer = remember { SettingsImporter(context, appState.settingsStore) }

        LaunchedEffect(transferStatusMessage) {
            if (transferStatusMessage != null) {
                delay(3200)
                transferStatusMessage = null
            }
        }

        val exportLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    val result = exporter.exportTo(uri)
                    transferStatusMessage = result.fold(
                        onSuccess = { context.getString(R.string.settings_transfer_success) },
                        onFailure = {
                            CrashReporter.log(it, "Settings export failed")
                            context.getString(
                                R.string.settings_transfer_export_failed,
                                it.message ?: context.getString(R.string.settings_transfer_failed_default)
                            )
                        }
                    )
                }
            }
        }
        val importLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    val result = importer.importFrom(uri)
                    transferStatusMessage = result.fold(
                        onSuccess = { context.getString(R.string.settings_transfer_restore_success) },
                        onFailure = {
                            CrashReporter.log(it, "Settings import failed")
                            context.getString(
                                R.string.settings_transfer_import_failed,
                                it.message ?: context.getString(R.string.settings_transfer_failed_default)
                            )
                        }
                    )
                }
            }
        }

        LaunchedEffect(currentRoute, settings.audioEnabled) {
            val immersiveDestinations = setOf(
                AppDestination.VisualPlayground.route,
                AppDestination.CalmingLoop.route
            )
            if (settings.audioEnabled && currentRoute in immersiveDestinations) {
                appState.ambientAudioEngine.start(context)
            } else {
                appState.ambientAudioEngine.stop()
            }
        }

        LaunchedEffect(Unit) {
            appState.adManager.initialize(context)
        }

        LaunchedEffect(premiumStatus, appState.premiumManager.isOverrideActive()) {
            if (!appState.premiumManager.isOverrideActive()) {
                settings.setPremiumUnlocked(premiumStatus)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                appState.ambientAudioEngine.stop()
            }
        }

        Surface(color = Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AppConfiguration.neonTheme.background,
                                AppConfiguration.neonTheme.background.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                NavHost(
                    navController = navController,
                    startDestination = AppDestination.MainMenu.route
                ) {
                    composable(AppDestination.MainMenu.route) {
                        MainMenuScreen(
                            onNavigateToPlayground = {
                                navController.navigate(AppDestination.VisualPlayground.route)
                            },
                            onNavigateToColoring = {
                                navController.navigate(AppDestination.ColoringZone.route)
                            },
                            onNavigateToLoops = {
                                navController.navigate(AppDestination.CalmingLoop.route)
                            },
                            onNavigateToSettings = {
                                navController.navigate(AppDestination.Settings.route)
                            },
                            onNavigateToDevTools = {
                                navController.navigate(AppDestination.DevTools.route)
                            },
                            hapticsEnabled = settings.hapticsEnabled,
                            adManager = appState.adManager,
                            showAds = showAds
                        )
                    }
                    composable(AppDestination.VisualPlayground.route) {
                        VisualPlaygroundScreen(
                            hapticsEnabled = settings.hapticsEnabled,
                            particleTrailsEnabled = settings.particleTrailsEnabled,
                            audioReactiveEnabled = settings.audioReactiveEnabled,
                            multiColorRipplesEnabled = settings.multiColorRipplesEnabled,
                            selectedSceneId = settings.selectedSceneId,
                            neonAudioEngine = appState.neonAudioEngine
                        )
                    }
                    composable(AppDestination.ColoringZone.route) {
                        ColoringZoneScreen()
                    }
                    composable(AppDestination.CalmingLoop.route) {
                        CalmingLoopScreen(
                            soundscapeEngine = appState.soundscapeEngine,
                            selectedSoundscapeId = settings.lastSoundscapeId,
                            onSoundscapeSelected = { settings.setLastSoundscape(it) },
                            isPremium = premiumStatus,
                            onRequestPremium = { appState.premiumManager.launchPremiumFlow(activity = null) }
                        )
                    }
                    composable(AppDestination.Settings.route) {
                        SettingsScreen(
                            hapticsEnabled = settings.hapticsEnabled,
                            audioEnabled = settings.audioEnabled,
                            particleTrailsEnabled = settings.particleTrailsEnabled,
                            audioReactiveEnabled = settings.audioReactiveEnabled,
                            multiColorEnabled = settings.multiColorRipplesEnabled,
                            selectedSceneId = settings.selectedSceneId,
                            isPremium = premiumStatus,
                            onHapticsToggle = { settings.setHapticsEnabled(it) },
                            onAudioToggle = { settings.setAudioEnabled(it) },
                            onParticleTrailsToggle = { settings.setParticleTrailsEnabled(it) },
                            onAudioReactiveToggle = { settings.setAudioReactiveEnabled(it) },
                            onMultiColorToggle = { settings.setMultiColorRipplesEnabled(it) },
                            onSceneSelected = { settings.setSelectedScene(it) },
                            onRequestPremium = { appState.premiumManager.launchPremiumFlow(null) },
                            onSimulatePurchase = { appState.premiumManager.simulatePurchaseUnlock() },
                            onSimulateRevoke = { appState.premiumManager.simulateRevoke() },
                            adManager = appState.adManager,
                            showAds = showAds,
                            onBackupRequested = {
                                val filename = "Trippify-Settings-${System.currentTimeMillis()}.json"
                                exportLauncher.launch(filename)
                            },
                            onRestoreRequested = {
                                importLauncher.launch(arrayOf("application/json"))
                            },
                            onOpenDevTools = {
                                navController.navigate(AppDestination.DevTools.route)
                            },
                            transferStatusMessage = transferStatusMessage
                        )
                    }
                    composable(AppDestination.DevTools.route) {
                        val diagnosticsViewModel: DevDiagnosticsViewModel = hiltViewModel()
                        val diagnosticsState by diagnosticsViewModel.uiState.collectAsState()
                        LaunchedEffect(Unit) { diagnosticsViewModel.refresh() }
                        DevToolsScreen(
                            premiumOverride = premiumOverrideState,
                            onOverrideChanged = { override ->
                                premiumOverrideState = override
                                val value = when (override) {
                                    PremiumOverride.Realtime -> null
                                    PremiumOverride.ForcedOn -> true
                                    PremiumOverride.ForcedOff -> false
                                }
                                appState.premiumManager.forcePremium(value)
                            },
                            onSimulatePurchase = { appState.premiumManager.simulatePurchaseUnlock() },
                            onSimulateRevoke = { appState.premiumManager.simulateRevoke() },
                            isFakeAudioEnabled = fakeAudioEnabled,
                            onFakeAudioEnabled = { enabled ->
                                fakeAudioEnabled = enabled
                                appState.neonAudioEngine.enableDebugOverride(enabled)
                            },
                            onFakeAudioLevelChange = { low, mid, high ->
                                appState.neonAudioEngine.setDebugLevels(low, mid, high)
                            },
                            isPerformanceOverlayEnabled = performanceOverlayEnabled,
                            onPerformanceOverlayToggle = { enabled -> performanceOverlayEnabled = enabled },
                            onResetDefaults = {
                                coroutineScope.launch {
                                    appState.settingsStore.resetToDefaults()
                                    activity?.recreate()
                                }
                            },
                            diagnosticsState = diagnosticsState,
                            onRefreshDiagnostics = diagnosticsViewModel::refresh
                        )
                    }
                }
                if (BuildConfig.DEBUG && performanceOverlayEnabled) {
                    PerformanceOverlay(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext?.findActivity()
    else -> null
}
