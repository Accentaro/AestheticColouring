package com.trippify.app.billing

import com.trippify.app.data.PremiumSettingsGateway
import com.trippify.app.data.SettingsSnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PremiumManagerTest {
    @Test
    fun `simulate purchase flips premium flag`() = runTest {
        val gateway = FakeGateway()
        val manager = PremiumManager(gateway, this)
        manager.simulatePurchaseUnlock()
        assertTrue(manager.isPremium.first())
        manager.simulateRevoke()
        assertFalse(manager.isPremium.first())
    }

    @Test
    fun `override forces premium without touching gateway`() = runTest {
        val gateway = FakeGateway()
        val manager = PremiumManager(gateway, this)
        gateway.state.value = gateway.state.value.copy(premiumUnlocked = false)
        manager.forcePremium(true)
        assertTrue(manager.isPremium.first())
        manager.forcePremium(false)
        assertFalse(manager.isPremium.first())
        manager.forcePremium(null)
        assertFalse(manager.isPremium.first())
    }

    private class FakeGateway : PremiumSettingsGateway {
        val state = MutableStateFlow(
            SettingsSnapshot(
                hapticsEnabled = true,
                audioEnabled = true,
                particleTrailsEnabled = false,
                audioReactiveEnabled = true,
                multiColorRipples = true,
                selectedSceneId = "neonRipple",
                lastSoundscapeId = "soundscape_neon_hum",
                premiumUnlocked = false,
                adsEnabled = true
            )
        )

        override val settings = state

        override suspend fun setPremiumUnlocked(unlocked: Boolean) {
            state.value = state.value.copy(premiumUnlocked = unlocked)
        }
    }
}
