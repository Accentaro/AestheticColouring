package com.trippify.app.core.state

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.trippify.app.ads.AdManager
import com.trippify.app.audio.AmbientAudioEngine
import com.trippify.app.audio.NeonAudioEngine
import com.trippify.app.audio.SoundscapeEngine
import com.trippify.app.billing.PremiumManager
import com.trippify.app.data.SettingsStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class AppState(
    val navController: NavHostController,
    val settings: AppSettingsState,
    val ambientAudioEngine: AmbientAudioEngine,
    val soundscapeEngine: SoundscapeEngine,
    val neonAudioEngine: NeonAudioEngine,
    val adManager: AdManager,
    val premiumManager: PremiumManager,
    val settingsStore: SettingsStore
)

@Composable
fun rememberAppState(
    context: Context = LocalContext.current,
    navController: NavHostController = rememberNavController(),
    coroutineScope: androidx.compose.runtime.CoroutineScope = rememberCoroutineScope(),
    settingsStore: SettingsStore? = null,
    settingsState: AppSettingsState? = null,
    ambientAudioEngine: AmbientAudioEngine? = null,
    soundscapeEngine: SoundscapeEngine? = null,
    neonAudioEngine: NeonAudioEngine? = null,
    adManager: AdManager? = null,
    premiumManager: PremiumManager? = null
): AppState = remember {
    val application = context.applicationContext as Application
    val entryPoint = EntryPointAccessors.fromApplication(application, ApplicationDependencies::class.java)
    val resolvedSettingsStore = settingsStore ?: entryPoint.settingsStore()
    val resolvedSettingsState = settingsState ?: AppSettingsState(coroutineScope, resolvedSettingsStore)
    AppState(
        navController = navController,
        settings = resolvedSettingsState,
        ambientAudioEngine = ambientAudioEngine ?: entryPoint.ambientAudioEngine(),
        soundscapeEngine = soundscapeEngine ?: entryPoint.soundscapeEngine(),
        neonAudioEngine = neonAudioEngine ?: entryPoint.neonAudioEngine(),
        adManager = adManager ?: entryPoint.adManager(),
        premiumManager = premiumManager ?: entryPoint.premiumManager(),
        settingsStore = resolvedSettingsStore
    )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ApplicationDependencies {
    fun settingsStore(): SettingsStore
    fun ambientAudioEngine(): AmbientAudioEngine
    fun soundscapeEngine(): SoundscapeEngine
    fun neonAudioEngine(): NeonAudioEngine
    fun adManager(): AdManager
    fun premiumManager(): PremiumManager
}
