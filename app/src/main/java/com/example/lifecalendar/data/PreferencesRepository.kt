package com.example.lifecalendar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.lifecalendar.domain.LifeCalendarCalculator
import com.example.lifecalendar.wallpaper.WallpaperTarget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Extension property to create a singleton DataStore instance.
 *
 * Uses Jetpack DataStore Preferences for type-safe key-value storage.
 * The DataStore is created lazily and cached for the application lifecycle.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "life_calendar_preferences"
)

/**
 * Repository for managing user preferences.
 *
 * This class provides a clean API for reading and writing user settings
 * using Jetpack DataStore. All operations are suspend functions for
 * proper coroutine integration.
 *
 * ## Stored Data
 * - Birth date (as epoch days for easy serialization)
 * - Life expectancy (years)
 * - Wallpaper target (home, lock, or both)
 * - Theme preference (dark/light)
 *
 * ## Usage Example
 * ```kotlin
 * val repository = PreferencesRepository(context)
 *
 * // Save preferences
 * repository.saveBirthDate(LocalDate.of(1990, 5, 15))
 * repository.saveLifeExpectancy(85)
 *
 * // Read preferences as Flow
 * repository.getUserPreferences().collect { prefs ->
 *     println("Birth date: ${prefs.birthDate}")
 * }
 * ```
 *
 * @param context Application context for accessing DataStore
 * @see UserPreferences for the complete preferences data class
 */
class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val BIRTH_DATE_EPOCH_DAYS = longPreferencesKey("birth_date_epoch_days")
        val LIFE_EXPECTANCY = intPreferencesKey("life_expectancy")
        val WALLPAPER_TARGET = stringPreferencesKey("wallpaper_target")
        val THEME = stringPreferencesKey("theme")
        val IS_SETUP_COMPLETE = stringPreferencesKey("is_setup_complete")
    }

    /**
     * Returns a Flow of user preferences that emits whenever data changes.
     *
     * The Flow is cold and will only query DataStore when collected.
     * Use this for observing preference changes in the UI.
     *
     * @return Flow emitting [UserPreferences] on each change
     */
    fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { preferences ->
            val epochDays = preferences[PreferencesKeys.BIRTH_DATE_EPOCH_DAYS]
            val birthDate = epochDays?.let { LocalDate.ofEpochDay(it) }

            UserPreferences(
                birthDate = birthDate,
                lifeExpectancy = preferences[PreferencesKeys.LIFE_EXPECTANCY]
                    ?: LifeCalendarCalculator.DEFAULT_LIFE_EXPECTANCY,
                wallpaperTarget = preferences[PreferencesKeys.WALLPAPER_TARGET]
                    ?.let { WallpaperTarget.valueOf(it) }
                    ?: WallpaperTarget.BOTH,
                theme = preferences[PreferencesKeys.THEME]
                    ?.let { CalendarTheme.valueOf(it) }
                    ?: CalendarTheme.DARK,
                isSetupComplete = preferences[PreferencesKeys.IS_SETUP_COMPLETE] == "true"
            )
        }
    }

    /**
     * Saves the user's birth date.
     *
     * The date is stored as epoch days (days since 1970-01-01) for
     * efficient serialization and deserialization.
     *
     * @param birthDate The user's date of birth
     */
    suspend fun saveBirthDate(birthDate: LocalDate) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIRTH_DATE_EPOCH_DAYS] = birthDate.toEpochDay()
        }
    }

    /**
     * Saves the user's expected life span.
     *
     * @param years Life expectancy in years (typically 60-100)
     */
    suspend fun saveLifeExpectancy(years: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LIFE_EXPECTANCY] = years
        }
    }

    /**
     * Saves the wallpaper target preference.
     *
     * @param target Where to set the wallpaper (home, lock, or both)
     */
    suspend fun saveWallpaperTarget(target: WallpaperTarget) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WALLPAPER_TARGET] = target.name
        }
    }

    /**
     * Saves the theme preference.
     *
     * @param theme The calendar theme (dark or light)
     */
    suspend fun saveTheme(theme: CalendarTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    /**
     * Marks the initial setup as complete.
     *
     * Called after the user completes onboarding. Used to determine
     * whether to show the onboarding flow or the main app.
     */
    suspend fun setSetupComplete() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_SETUP_COMPLETE] = "true"
        }
    }

    /**
     * Saves all user preferences at once.
     *
     * Useful for saving multiple values in a single transaction,
     * which is more efficient than multiple individual saves.
     *
     * @param birthDate The user's date of birth
     * @param lifeExpectancy Expected life span in years
     * @param wallpaperTarget Where to set the wallpaper
     * @param theme The calendar theme
     */
    suspend fun saveAllPreferences(
        birthDate: LocalDate,
        lifeExpectancy: Int,
        wallpaperTarget: WallpaperTarget,
        theme: CalendarTheme
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIRTH_DATE_EPOCH_DAYS] = birthDate.toEpochDay()
            preferences[PreferencesKeys.LIFE_EXPECTANCY] = lifeExpectancy
            preferences[PreferencesKeys.WALLPAPER_TARGET] = wallpaperTarget.name
            preferences[PreferencesKeys.THEME] = theme.name
            preferences[PreferencesKeys.IS_SETUP_COMPLETE] = "true"
        }
    }
}

/**
 * User preferences for the life calendar.
 *
 * Immutable data class containing all user-configurable settings.
 *
 * @property birthDate The user's date of birth, null if not yet set
 * @property lifeExpectancy Expected life span in years
 * @property wallpaperTarget Where to apply the wallpaper
 * @property theme Visual theme for the calendar
 * @property isSetupComplete Whether the user has completed initial setup
 */
data class UserPreferences(
    val birthDate: LocalDate?,
    val lifeExpectancy: Int,
    val wallpaperTarget: WallpaperTarget,
    val theme: CalendarTheme,
    val isSetupComplete: Boolean
)

/**
 * Visual theme options for the calendar.
 *
 * @property DARK White dots on black background (default)
 * @property LIGHT Black dots on white background
 */
enum class CalendarTheme {
    DARK,
    LIGHT
}
