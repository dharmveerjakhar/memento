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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.memento.data.CalendarTheme
import com.example.memento.data.UserPreferences
import com.example.memento.domain.LifeCalendarCalculator
import com.example.memento.wallpaper.WallpaperTarget
import com.example.memento.ui.components.DotText
import com.example.memento.ui.components.DottedDatePickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        mutableStateOf(preferences.lifeExpectancy)
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d yyyy") }
    val onBg = MaterialTheme.colorScheme.onBackground
    val bg = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(top = 48.dp)
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
                DotText(text = "<", color = onBg, dotSize = 2.dp, spacing = 1.dp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            DotText(text = "SETTINGS", color = onBg, dotSize = 4.dp, spacing = 1.dp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            // Birth Date Section
            SettingsSection(title = "BIRTH DATE", onBg) {
                val dateText = preferences.birthDate?.format(dateFormatter)?.uppercase() ?: "NOT SET"
                Row(
                    modifier = Modifier.clickable { showDatePicker = true }.padding(vertical = 12.dp)
                ) {
                    DotText(text = "[ $dateText ]", color = Color(0xFF64B5F6), dotSize = 2.dp, spacing = 1.dp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Life Expectancy Section
            SettingsSection(title = "LIFE EXPECTANCY", onBg) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DotText(text = "YEARS", color = onBg, dotSize = 2.dp, spacing = 1.dp)
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(onClick = { 
                        if (lifeExpectancy > LifeCalendarCalculator.MIN_LIFE_EXPECTANCY) {
                            lifeExpectancy--
                            onLifeExpectancyChange(lifeExpectancy)
                        } 
                    }) {
                        DotText(text = "-", color = onBg, dotSize = 2.dp, spacing = 1.dp)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    DotText(text = lifeExpectancy.toString(), color = onBg, dotSize = 3.dp, spacing = 1.dp)
                    
                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(onClick = { 
                        if (lifeExpectancy < LifeCalendarCalculator.MAX_LIFE_EXPECTANCY) {
                            lifeExpectancy++
                            onLifeExpectancyChange(lifeExpectancy)
                        }
                    }) {
                        DotText(text = "+", color = onBg, dotSize = 2.dp, spacing = 1.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Theme Section
            SettingsSection(title = "THEME", onBg) {
                Column {
                    CalendarTheme.entries.forEach { theme ->
                        RadioOption(
                            text = when (theme) {
                                CalendarTheme.DARK -> "DARK"
                                CalendarTheme.LIGHT -> "LIGHT"
                            },
                            selected = preferences.theme == theme,
                            onClick = { onThemeChange(theme) },
                            color = onBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDatePicker) {
        DottedDatePickerDialog(
            initialDate = preferences.birthDate,
            onDateSelected = { newDate ->
                onBirthDateChange(newDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    onBg: Color,
    content: @Composable () -> Unit
) {
    Column {
        DotText(
            text = title,
            color = onBg,
            dotSize = 3.dp,
            spacing = 1.dp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}

@Composable
private fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconText = if (selected) "[X]" else "[ ]"
        DotText(
            text = iconText,
            color = color,
            dotSize = 2.dp,
            spacing = 1.dp
        )
        Spacer(modifier = Modifier.width(16.dp))
        DotText(
            text = text,
            color = color,
            dotSize = 2.dp,
            spacing = 1.dp
        )
    }
}
