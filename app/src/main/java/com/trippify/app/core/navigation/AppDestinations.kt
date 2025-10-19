package com.trippify.app.core.navigation

sealed class AppDestination(val route: String) {
    object MainMenu : AppDestination("mainMenu")
    object VisualPlayground : AppDestination("visualPlayground")
    object ColoringZone : AppDestination("coloringZone")
    object CalmingLoop : AppDestination("calmingLoop")
    object Settings : AppDestination("settings")
    object DevTools : AppDestination("devTools")
}

val primaryDestinations = listOf(
    AppDestination.MainMenu,
    AppDestination.VisualPlayground,
    AppDestination.ColoringZone,
    AppDestination.CalmingLoop,
    AppDestination.Settings
)
