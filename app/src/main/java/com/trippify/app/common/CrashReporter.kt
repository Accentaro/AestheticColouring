package com.trippify.app.common

import android.util.Log
import com.trippify.app.BuildConfig

/**
 * Lightweight logging shim so we can wire Firebase Crashlytics or another crash reporter later on.
 * All production-facing error handling should flow through this helper to keep the app resilient
 * without shipping a real SDK in the scaffold.
 */
object CrashReporter {
    private const val TAG = "TrippifyCrashReporter"

    fun log(throwable: Throwable, message: String? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message ?: throwable.message ?: "Unknown error", throwable)
        } else {
            Log.w(TAG, message ?: throwable.message ?: "Unknown error")
        }
    }

    fun logMessage(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        } else {
            Log.i(TAG, message)
        }
    }
}
