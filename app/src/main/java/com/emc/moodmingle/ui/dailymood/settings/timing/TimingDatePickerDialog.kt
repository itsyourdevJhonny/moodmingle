package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.MentionTextColor
import java.time.LocalDate
import java.time.Year

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingDatePickerDialog(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(yearRange = IntRange(Year.now().value, 2100))
    var selectedDate: LocalDate?

    datePickerState.selectedDateMillis?.let { dateMillis ->
        selectedDate = LocalDate.ofEpochDay(dateMillis / (1000 * 60 * 60 * 24))
        onSettingsEdited(settings.copy(timing = settings.timing.copy(scheduledAt = selectedDate?.atStartOfDay())))
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
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
}