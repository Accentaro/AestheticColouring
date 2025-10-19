package com.trippify.app.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier, adManager: AdManager) {
    val context = LocalContext.current
    val adViewHolder = remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(adManager) {
        adManager.initialize(context)
        onDispose {
            adManager.destroyBanner(adViewHolder.value)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            adManager.createBanner(ctx).also { created ->
                adViewHolder.value = created
            }
        }
    )
}
