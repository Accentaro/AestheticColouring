package com.trippify.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import com.trippify.app.core.immersive.ImmersiveModeController
import com.trippify.app.core.navigation.TrippifyApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var immersiveModeController: ImmersiveModeController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        immersiveModeController = ImmersiveModeController(window)
        immersiveModeController.enterImmersiveMode()
        setContent {
            TrippifyApp()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            immersiveModeController.enterImmersiveMode()
        }
    }
}
