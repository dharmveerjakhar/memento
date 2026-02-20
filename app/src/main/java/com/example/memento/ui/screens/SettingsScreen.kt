package com.example.memento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.memento.data.CalendarTheme
import com.example.memento.data.UserPreferences
import com.example.memento.domain.LifeCalendarCalculator
import com.example.memento.wallpaper.WallpaperTarget
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Settings screen for adjusting life calendar preferences.
 *
 * Allows users to:
 * - Change their birth date
 * - Adjust life expectancy
 * - Select wallpaper target (home, lock, or both)
 * - Choose theme (dark or light)
 *
 * @param preferences Current user preferences
 * @param onBack Callback to navigate back
 * @param onBirthDateChange Callback when birth date is changed
 * @param onLifeExpectancyChange Callback when life expectancy is changed
 * @param onWallpaperTargetChange Callback when wallpaper target is changed
 * @param onThemeChange Callback when theme is changed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onBack: () -> Unit,
    onBirthDateChange: (LocalDate) -> Unit,
    onLifeExpectancyChange: (Int) -> Unit,
    onWallpaperTargetChange: (WallpaperTarget) -> Unit,
    onThemeChange: (CalendarTheme) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var lifeExpectancy by remember(preferences.lifeExpectancy) {
        mutableFloatStateOf(preferences.lifeExpectancy.toFloat())
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM d, yyyy") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp) // Added top padding for status bar
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Birth Date Section
            SettingsSection(title = "Birth Date") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = preferences.birthDate?.format(dateFormatter)
                                ?: "Not set",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Change date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Life Expectancy Section
            SettingsSection(title = "Life Expectancy: ${lifeExpectancy.toInt()} years") {
                Slider(
                    value = lifeExpectancy,
                    onValueChange = { lifeExpectancy = it },
                    onValueChangeFinished = {
                        onLifeExpectancyChange(lifeExpectancy.toInt())
                    },
                    valueRange = LifeCalendarCalculator.MIN_LIFE_EXPECTANCY.toFloat()..
                            LifeCalendarCalculator.MAX_LIFE_EXPECTANCY.toFloat(),
                    steps = (LifeCalendarCalculator.MAX_LIFE_EXPECTANCY -
                            LifeCalendarCalculator.MIN_LIFE_EXPECTANCY - 1),
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Theme Section
            SettingsSection(title = "Theme") {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column {
                        CalendarTheme.entries.forEach { theme ->
                            RadioOption(
                                text = when (theme) {
                                    CalendarTheme.DARK -> "Dark (White on Black)"
                                    CalendarTheme.LIGHT -> "Light (Black on White)"
                                },
                                selected = preferences.theme == theme,
                                onClick = { onThemeChange(theme) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Date Picker Dialog - Custom Dotted Style
    if (showDatePicker) {
        com.example.memento.ui.components.DottedDatePickerDialog(
            initialDate = preferences.birthDate,
            onDateSelected = { newDate ->
                onBirthDateChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

/**
 * Section header for settings groups.
 *
 * @param title Section title
 * @param content Content of the section
 */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

/**
 * Radio button option row.
 *
 * @param text Option label
 * @param selected Whether this option is currently selected
 * @param onClick Callback when option is clicked
 */
@Composable
private fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
