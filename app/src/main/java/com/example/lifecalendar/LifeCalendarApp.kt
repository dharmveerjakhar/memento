package com.example.lifecalendar

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

/**
 * Application class for Life Calendar.
 *
 * Initializes app-wide dependencies and configures WorkManager for background updates.
 * This class is specified in AndroidManifest.xml as the application entry point.
 *
 * @see WorkManager for background wallpaper update scheduling
 */
class LifeCalendarApp : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        // WorkManager is initialized automatically via Configuration.Provider
    }

    /**
     * Provides custom WorkManager configuration.
     *
     * Uses the default configuration with minimum logging in release builds.
     * This approach is recommended over manual initialization for better lifecycle handling.
     *
     * @return WorkManager configuration with default settings
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
