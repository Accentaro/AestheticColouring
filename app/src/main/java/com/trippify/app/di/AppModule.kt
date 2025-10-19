package com.trippify.app.di

import android.content.Context
import com.trippify.app.ads.AdManager
import com.trippify.app.audio.AmbientAudioEngine
import com.trippify.app.audio.NeonAudioEngine
import com.trippify.app.audio.SoundscapeEngine
import com.trippify.app.billing.PremiumManager
import com.trippify.app.data.SettingsStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSettingsStore(@ApplicationContext context: Context): SettingsStore = SettingsStore(context)

    @Provides
    @Singleton
    fun provideAmbientAudioEngine(): AmbientAudioEngine = AmbientAudioEngine()

    @Provides
    @Singleton
    fun provideSoundscapeEngine(@ApplicationContext context: Context): SoundscapeEngine = SoundscapeEngine(context)

    @Provides
    @Singleton
    fun provideNeonAudioEngine(): NeonAudioEngine = NeonAudioEngine()

    @Provides
    @Singleton
    fun provideAdManager(): AdManager = AdManager()

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob()) + Dispatchers.Main.immediate

    @Provides
    @Singleton
    fun providePremiumManager(
        settingsStore: SettingsStore,
        scope: CoroutineScope
    ): PremiumManager = PremiumManager(settingsStore, scope)
}
