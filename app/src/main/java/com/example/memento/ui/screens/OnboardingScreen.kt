package com.example.memento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.memento.domain.LifeCalendarCalculator
import com.example.memento.ui.components.DotText
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Onboarding screen for new users.
 *
 * Allows users to:
 * - Select their birth date via a date picker
 * - Adjust their life expectancy with a slider
 * - Proceed to generate their first life calendar
 *
 * @param onComplete Callback invoked when user completes setup with birth date and life expectancy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: (birthDate: LocalDate, lifeExpectancy: Int) -> Unit
) {
    // Default to 25 years ago for the initial date
    val defaultDate = LocalDate.now().minusYears(25)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = defaultDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
    )

    var lifeExpectancy by remember {
        mutableFloatStateOf(LifeCalendarCalculator.DEFAULT_LIFE_EXPECTANCY.toFloat())
    }

    // Derive the selected date from picker state
    val selectedDate by remember {
        derivedStateOf {
            datePickerState.selectedDateMillis?.let { millis ->
                Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            DotText(
                text = "MEMENTO",
                color = MaterialTheme.colorScheme.onBackground,
                dotSize = 5.dp,
                spacing = 1.5.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Visualize your life as a grid of weeks",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        // Date Picker
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "When were you born?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        // Life Expectancy Slider
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Life Expectancy: ${lifeExpectancy.toInt()} years",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = lifeExpectancy,
                onValueChange = { lifeExpectancy = it },
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

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Button
            Button(
                onClick = {
                    selectedDate?.let { date ->
                        onComplete(date, lifeExpectancy.toInt())
                    }
                },
                enabled = selectedDate != null && selectedDate!! <= LocalDate.now(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Generate My Memento",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
