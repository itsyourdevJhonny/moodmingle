package com.emc.moodmingle.ui.create.post.settings.schedule

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.settings.PostSchedule
import com.emc.moodmingle.ui.create.post.CreatePostDialogHeader
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.Typography
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.util.Calendar
import java.util.Locale

@Composable
fun ScheduleDialog(onScheduleCreated: (PostSchedule) -> Unit, onDismiss: () -> Unit) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { CreatePostDialogHeader(label = "Schedule Post Expiration", onBack = onDismiss) }
    ) { paddingValues ->
        ScheduleDialogContent(paddingValues, onScheduleCreated, onDismiss)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDialogContent(
    paddingValues: PaddingValues,
    onScheduleCreated: (PostSchedule) -> Unit,
    onDismiss: () -> Unit
) {
    var currentSection by remember { mutableIntStateOf(1) }

    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(Year.now().value, 2100)
    )
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute
    )

    var selectedDate: LocalDate? = null
    var selectedTime = 0L

    datePickerState.selectedDateMillis?.let { dateMillis ->
        selectedDate = LocalDate.ofEpochDay(dateMillis / (1000 * 60 * 60 * 24))

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        calendar.set(Calendar.MINUTE, timePickerState.minute)
        calendar.isLenient = false

        selectedTime = calendar.timeInMillis
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.animateContentSize()
            ) {
                if (currentSection >= 1 && currentSection < 3) {
                    IndicatorText(currentSection)
                    SelectedDateTimePreview(selectedDate, selectedTime, currentSection)
                    HorizontalDivider(thickness = 0.5.dp)
                }

                when (currentSection) {
                    1 -> ExpirationDateSection(datePickerState)
                    2 -> ExpirationTimeSection(timePickerState)
                    3 -> CreatedScheduleSection(
                        selectedDate,
                        selectedTime,
                        onScheduleCreated,
                        onDismiss
                    )
                }
            }
        }

        Row(
            horizontalArrangement = if (currentSection != 1) Arrangement.SpaceBetween else Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            if (currentSection < 3) {
                if (currentSection > 1) {
                    NextOrPreviousButton(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        label = "Previous",
                        onClick = { currentSection-- }
                    )
                }

                NextOrPreviousButton(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    label = "Next",
                    enabled = selectedDate != null,
                    onClick = { currentSection++ }
                )
            }
        }
    }
}

@Composable
private fun IndicatorText(currentSection: Int) {
    Text(
        text = "Set Expiration ${if (currentSection == 1) "Date" else "Time"}",
        style = Typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SelectedDateTimePreview(
    selectedDate: LocalDate?,
    selectedTime: Long,
    currentSection: Int = 3
) {
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    selectedDate?.let { date ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.animateContentSize()
        ) {
            Icon(
                painter = painterResource(R.drawable.time),
                contentDescription = "DateTime",
                tint = MentionTextColor,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "$date ${
                    if (currentSection == 2 || currentSection == 3) formatter.format(selectedTime)
                    else ""
                }",
                color = Color.White
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExpirationDateSection(datePickerState: DatePickerState) {
    DatePicker(
        title = null,
        showModeToggle = false,
        headline = null,
        state = datePickerState,
        colors = DatePickerDefaults.colors(
            headlineContentColor = Color.White,
            containerColor = Color.Transparent,
            weekdayContentColor = Color.White,
            yearContentColor = Color.Black,
            selectedDayContainerColor = Color.White,
            dayInSelectionRangeContainerColor = Color.White,
            dayInSelectionRangeContentColor = Color.White,
            todayContentColor = Color.White,
            todayDateBorderColor = Color.White,
            dayContentColor = Color.White,
            navigationContentColor = Color.White,
            currentYearContentColor = Color.White,
            selectedYearContentColor = Color.Black,
            selectedYearContainerColor = Color.White,
            dividerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(MentionTextColor, RoundedCornerShape(24.dp))
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExpirationTimeSection(timePickerState: TimePickerState) {
    TimePicker(
        state = timePickerState,
        colors = TimePickerDefaults.colors(
            clockDialColor = MentionTextColor,
            clockDialSelectedContentColor = Color.Black,
            clockDialUnselectedContentColor = Color.White,
            selectorColor = Color.White,
            periodSelectorBorderColor = MentionTextColor,
            periodSelectorSelectedContainerColor = MentionTextColor,
            periodSelectorUnselectedContainerColor = Color.Unspecified,
            periodSelectorSelectedContentColor = Color.White,
            periodSelectorUnselectedContentColor = Color.White,
            timeSelectorSelectedContainerColor = MentionTextColor,
            timeSelectorUnselectedContainerColor = Color.Unspecified,
            timeSelectorSelectedContentColor = Color.White,
            timeSelectorUnselectedContentColor = Color.White,
        )
    )
}

@Composable
private fun NextOrPreviousButton(
    imageVector: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MentionTextColor,
            contentColor = Color.White
        ),
        enabled = enabled
    ) {
        Icon(imageVector = imageVector, contentDescription = label)
        Text(text = " $label", fontWeight = FontWeight.Bold)
    }
}