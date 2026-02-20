package com.example.memento.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Custom dotted-style date picker dialog matching the Memento app aesthetic.
 *
 * Features:
 * - Black background with white dot-matrix style text
 * - Clear left/right arrows for month navigation
 * - Clickable year for year selection
 * - Calendar grid with day selection (fixed 6 weeks for consistent layout)
 * - Confirm/Cancel buttons
 *
 * @param initialDate The initially selected date
 * @param onDateSelected Callback when a date is confirmed
 * @param onDismiss Callback when the dialog is dismissed
 */
@Composable
fun DottedDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var displayedMonth by remember {
        mutableStateOf(YearMonth.from(initialDate ?: LocalDate.now()))
    }
    var selectedDate by remember {
        mutableStateOf(initialDate ?: LocalDate.now())
    }
    var showYearPicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false  // Allow custom width
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)  // 95% of screen width
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                DottedText(
                    text = "SELECT BIRTH DATE",
                    fontSize = 14,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selected date display
                DottedText(
                    text = selectedDate.format(dateFormatter).uppercase(),
                    fontSize = 26,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (showYearPicker) {
                    // Year picker view
                    YearPicker(
                        currentYear = displayedMonth.year,
                        onYearSelected = { year ->
                            displayedMonth = displayedMonth.withYear(year)
                            showYearPicker = false
                        },
                        onDismiss = { showYearPicker = false }
                    )
                } else {
                    // Month navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            displayedMonth = displayedMonth.minusMonths(1)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous month",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showYearPicker = true }
                        ) {
                            DottedText(
                                text = displayedMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                                fontSize = 18,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DottedText(
                                text = displayedMonth.year.toString(),
                                fontSize = 18,
                                color = Color(0xFF64B5F6) // Light blue to indicate clickable
                            )
                        }

                        IconButton(onClick = {
                            displayedMonth = displayedMonth.plusMonths(1)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next month",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weekday headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                DottedText(
                                    text = day,
                                    fontSize = 16,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar grid - Always 6 weeks for consistent layout
                    CalendarGrid(
                        yearMonth = displayedMonth,
                        selectedDate = selectedDate,
                        onDateClick = { date -> selectedDate = date }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        DottedText(text = "CANCEL", fontSize = 16, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(onClick = { onDateSelected(selectedDate) }) {
                        DottedText(text = "CONFIRM", fontSize = 16, color = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * Year picker with scrollable list of years.
 */
@Composable
private fun YearPicker(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val years = (1900..LocalDate.now().year).toList().reversed()
    val listState = rememberLazyListState()
    
    // Scroll to current year on first composition
    LaunchedEffect(Unit) {
        val index = years.indexOf(currentYear)
        if (index >= 0) {
            listState.scrollToItem(index)
        }
    }

    Column(
        modifier = Modifier.height(350.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DottedText(
            text = "SELECT YEAR",
            fontSize = 16,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(years) { year ->
                val isSelected = year == currentYear
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onYearSelected(year) }
                        .background(
                            if (isSelected) Color.White else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DottedText(
                        text = year.toString(),
                        fontSize = 22,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }
    }
}

/**
 * Calendar grid displaying days of the month.
 * Always shows 6 weeks for consistent layout.
 */
@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    // Monday = 1, Sunday = 7
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value

    Column {
        // Always show 6 weeks for consistent layout
        repeat(6) { weekIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayOfWeek ->
                    val cellIndex = weekIndex * 7 + dayOfWeek
                    val dayOffset = cellIndex - (firstDayOfWeek - 1)
                    val isValidDay = dayOffset in 0 until daysInMonth

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isValidDay) {
                            val currentDay = dayOffset + 1
                            val date = yearMonth.atDay(currentDay)
                            val isSelected = date == selectedDate

                            Box(
                                modifier = Modifier
                                    .size(48.dp)  // Larger circles
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable { onDateClick(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                DottedText(
                                    text = currentDay.toString(),
                                    fontSize = 20,  // Larger font
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Text styled with monospace font and letter spacing for dot-matrix aesthetic.
 */
@Composable
private fun DottedText(
    text: String,
    fontSize: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        color = color,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        letterSpacing = 1.sp,
        modifier = modifier
    )
}
