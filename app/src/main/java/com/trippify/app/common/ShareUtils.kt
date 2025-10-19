package com.trippify.app.common

import android.content.Context
import android.content.Intent

/**
 * Convenience helpers for social sharing flows. The implementation intentionally keeps things
 * lightweight so designers can wire in actual screenshot capture later without blocking release.
 */
object ShareUtils {
    fun shareScreenshotPlaceholder(context: Context, screenName: String) {
        val shareText = "Trippify placeholder share from $screenName. TODO: capture ComposeView image via captureToImage()."
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        runCatching {
            val title = context.getString(com.trippify.app.R.string.share_chooser_title)
            context.startActivity(Intent.createChooser(intent, title))
        }.onFailure { CrashReporter.log(it, "Share intent failed") }
    }
}
