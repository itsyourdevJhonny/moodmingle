package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.TimingType
import com.emc.moodmingle.ui.theme.MentionTextColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingTimePickerDialog(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = settings.timing.scheduledAt?.hour ?: 12,
        initialMinute = settings.timing.scheduledAt?.minute ?: 0
    )

    onSettingsEdited(
        settings.copy(
            timing = settings.timing.copy(
                type = TimingType.SCHEDULE,
                scheduledAt = settings.timing.scheduledAt?.withHour(timePickerState.hour)
                    ?.withMinute(timePickerState.minute)
            )
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(horizontalAlignment = Alignment.End) {
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

            DoneButton(onDismiss)
        }
    }
}

@Composable
private fun DoneButton(onDismiss: () -> Unit) {
    TextButton(
        onClick = { onDismiss() },
        colors = ButtonDefaults.textButtonColors(
            containerColor = MentionTextColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Text(text = " Done")
    }
}