package com.trippify.app.billing

import android.app.Activity
import com.trippify.app.common.CrashReporter
import com.trippify.app.data.PremiumSettingsGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Lightweight wrapper to manage premium entitlement state. Real billing integration should replace
 * the simulate* calls with BillingClient purchase flows and signature validation.
 */
class PremiumManager(
    private val settingsGateway: PremiumSettingsGateway,
    private val scope: CoroutineScope
) {
    private val overrideState = MutableStateFlow<Boolean?>(null)

    val isPremium: StateFlow<Boolean> = settingsGateway.settings
        .map { it.premiumUnlocked }
        .combine(overrideState) { stored, override -> override ?: stored }
        .stateIn(scope, SharingStarted.Eagerly, false)

    fun simulatePurchaseUnlock() {
        scope.launch {
            runCatching { settingsGateway.setPremiumUnlocked(true) }
                .onFailure { CrashReporter.log(it, "Simulated unlock failed") }
        }
    }

    fun simulateRevoke() {
        scope.launch {
            runCatching { settingsGateway.setPremiumUnlocked(false) }
                .onFailure { CrashReporter.log(it, "Simulated revoke failed") }
        }
    }

    fun launchPremiumFlow(activity: Activity?, sku: String = PREMIUM_UNLOCK_ONE_TIME) {
        runCatching {
            // TODO: Hook into BillingClient.launchBillingFlow here. For now we just unlock instantly.
            simulatePurchaseUnlock()
        }.onFailure { error ->
            CrashReporter.log(error, "Premium flow launch failed for $sku")
        }
    }

    fun forcePremium(value: Boolean?) {
        overrideState.value = value
    }

    fun isOverrideActive(): Boolean = overrideState.value != null

    companion object {
        const val PREMIUM_UNLOCK_ONE_TIME = "premium_unlock_one_time"
        const val PREMIUM_SUBSCRIPTION = "subscription_neon_plus"
    }
}
