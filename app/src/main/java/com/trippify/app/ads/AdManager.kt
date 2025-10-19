package com.trippify.app.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.trippify.app.core.config.AppConfiguration
import com.trippify.app.common.CrashReporter
import com.trippify.app.R

class AdManager {
    private var initialized = false

    fun initialize(context: Context) {
        if (!initialized) {
            runCatching {
                MobileAds.initialize(context) {
                    CrashReporter.logMessage("AdMob initialized for Trippify scaffold")
                }
                initialized = true
            }.onFailure { error ->
                CrashReporter.log(error, "AdMob init failed - ads disabled")
                initialized = false
            }
        }
    }

    fun createBanner(context: Context): AdView {
        return runCatching {
            AdView(context).apply {
                contentDescription = context.getString(R.string.content_desc_banner_ad)
                adUnitId = AppConfiguration.bannerAdUnitId
                setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                loadAd(AdRequest.Builder().build())
            }
        }.getOrElse { error ->
            CrashReporter.log(error, "Banner creation failed")
            AdView(context).apply {
                contentDescription = context.getString(R.string.content_desc_banner_ad)
            }
        }
    }

    fun destroyBanner(adView: AdView?) {
        runCatching { adView?.destroy() }
    }
}
