package com.example.lifecalendar

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifecalendar.data.PreferencesRepository
import com.example.lifecalendar.ui.MainViewModel
import com.example.lifecalendar.ui.MainViewModelFactory
import com.example.lifecalendar.ui.navigation.Screen
import com.example.lifecalendar.ui.screens.HomeScreen
import com.example.lifecalendar.ui.screens.OnboardingScreen
import com.example.lifecalendar.ui.screens.SettingsScreen
import com.example.lifecalendar.ui.theme.LifeCalendarTheme
import com.example.lifecalendar.wallpaper.WallpaperUpdater
import com.example.lifecalendar.worker.WallpaperUpdateWorker

/**
 * Main entry point for the Life Calendar app.
 *
 * Sets up:
 * - Edge-to-edge display
 * - Jetpack Compose theming
 * - Navigation between screens
 * - ViewModel with dependencies
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesRepository = PreferencesRepository(applicationContext)
        val wallpaperUpdater = WallpaperUpdater(applicationContext)

        // Get screen dimensions
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val screenWidth = metrics.widthPixels
        val screenHeight = metrics.heightPixels

        setContent {
            LifeCalendarTheme {
                LifeCalendarApp(
                    preferencesRepository = preferencesRepository,
                    wallpaperUpdater = wallpaperUpdater,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight
                )
            }
        }
    }
}

/**
 * Main composable for the Life Calendar app.
 *
 * Handles navigation and screen routing based on user setup status.
 *
 * @param preferencesRepository Repository for user preferences
 * @param wallpaperUpdater Service for setting wallpaper
 * @param screenWidth Device screen width for preview generation
 * @param screenHeight Device screen height for preview generation
 */
@Composable
fun LifeCalendarApp(
    preferencesRepository: PreferencesRepository,
    wallpaperUpdater: WallpaperUpdater,
    screenWidth: Int,
    screenHeight: Int
) {
    val navController = rememberNavController()

    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            preferencesRepository = preferencesRepository,
            wallpaperUpdater = wallpaperUpdater,
            scheduleWorker = {
                WallpaperUpdateWorker.scheduleWeeklyUpdate(
                    navController.context.applicationContext
                )
            }
        )
    )

    // Set screen dimensions for bitmap generation
    LaunchedEffect(Unit) {
        viewModel.setScreenDimensions(screenWidth, screenHeight)
    }

    val preferences by viewModel.preferences.collectAsState()
    val metrics by viewModel.metrics.collectAsState()

    // Determine start destination based on setup status
    val startDestination = if (preferences?.isSetupComplete == true) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    // Wait for preferences to load before showing UI
    if (preferences != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = { birthDate, lifeExpectancy ->
                        viewModel.completeOnboarding(birthDate, lifeExpectancy)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    metrics = metrics,
                    previewBitmap = viewModel.previewBitmap,
                    isLoading = viewModel.isLoading,
                    wallpaperSet = viewModel.wallpaperSet,
                    onSetWallpaper = { viewModel.setWallpaper() },
                    onRefresh = { viewModel.refresh() },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Settings.route) {
                preferences?.let { prefs ->
                    SettingsScreen(
                        preferences = prefs,
                        onBack = { navController.popBackStack() },
                        onBirthDateChange = { viewModel.updateBirthDate(it) },
                        onLifeExpectancyChange = { viewModel.updateLifeExpectancy(it) },
                        onWallpaperTargetChange = { viewModel.updateWallpaperTarget(it) },
                        onThemeChange = { viewModel.updateTheme(it) }
                    )
                }
            }
        }
    }
}
