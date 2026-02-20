package com.example.memento

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
import com.example.memento.data.PreferencesRepository
import com.example.memento.ui.MainViewModel
import com.example.memento.ui.MainViewModelFactory
import com.example.memento.ui.navigation.Screen
import com.example.memento.ui.screens.HomeScreen
import com.example.memento.ui.screens.OnboardingScreen
import com.example.memento.ui.screens.SettingsScreen
import com.example.memento.ui.theme.MementoTheme
import com.example.memento.wallpaper.WallpaperUpdater
import com.example.memento.worker.WallpaperUpdateWorker

/**
 * Main entry point for the Memento app.
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
            MementoApp(
                preferencesRepository = preferencesRepository,
                wallpaperUpdater = wallpaperUpdater,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }
    }
}

/**
 * Main composable for the Memento app.
 *
 * Handles navigation and screen routing based on user setup status.
 *
 * @param preferencesRepository Repository for user preferences
 * @param wallpaperUpdater Service for setting wallpaper
 * @param screenWidth Device screen width for preview generation
 * @param screenHeight Device screen height for preview generation
 */
@Composable
fun MementoApp(
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
        val isDark = preferences?.theme != com.example.memento.data.CalendarTheme.LIGHT

        MementoTheme(darkTheme = isDark) {
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
                            onThemeChange = { viewModel.updateTheme(it) },
                            onDotStyleChange = { viewModel.updateDotStyle(it) }
                        )
                    }
                }
            }
        }
    }
}
