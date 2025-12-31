package com.example.lifecalendar.ui.navigation

/**
 * Navigation destinations for the Life Calendar app.
 *
 * Each destination is represented by its route string, used by NavController
 * to navigate between screens.
 */
sealed class Screen(val route: String) {
    /**
     * Onboarding screen for new users to enter their birth date.
     */
    data object Onboarding : Screen("onboarding")

    /**
     * Main screen showing the calendar preview and actions.
     */
    data object Home : Screen("home")

    /**
     * Settings screen for adjusting preferences.
     */
    data object Settings : Screen("settings")
}
