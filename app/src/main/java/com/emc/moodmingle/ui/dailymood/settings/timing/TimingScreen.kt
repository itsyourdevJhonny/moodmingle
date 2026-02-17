package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.SettingsTiming
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.TimingType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

@Composable
fun TimingScreen(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    val timing = settings.timing
    var selectedAction by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Timing") { onDismiss() } }
    ) { paddingValues ->
        Content(paddingValues, timing, settings, onSettingsEdited) { selectedAction = it }
    }

    when (selectedAction) {
        "date" -> TimingDatePickerDialog(settings, onSettingsEdited) { selectedAction = "" }
        "time" -> TimingTimePickerDialog(settings, onSettingsEdited) { selectedAction = "" }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    timing: SettingsTiming,
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onActionSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Control when your daily mood is published.",
            color = Color.White,
            style = Typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        TimingOption(
            title = "Post immediately",
            description = "Your mood will be published instantly.",
            selected = timing.type == TimingType.AUTO_POST_NOW
        ) {
            onSettingsEdited(settings.copy(timing = SettingsTiming(type = TimingType.AUTO_POST_NOW)))
        }

        TimingOption(
            title = "Schedule post",
            description = "Choose a specific date and time.",
            selected = timing.type == TimingType.SCHEDULE
        ) {
            onSettingsEdited(
                settings.copy(
                    timing = SettingsTiming(
                        type = TimingType.SCHEDULE,
                        scheduledAt = LocalDateTime.of(LocalDate.now(), LocalTime.now())
                    )
                )
            )
        }

        TimingOption(
            title = "Manual posting only",
            description = "Save moods as drafts and post manually.",
            selected = timing.type == TimingType.MANUAL_ONLY
        ) {
            onSettingsEdited(settings.copy(timing = SettingsTiming(type = TimingType.MANUAL_ONLY)))
        }

        // Show scheduler UI if selected
        if (timing.type == TimingType.SCHEDULE) {
            Spacer(modifier = Modifier.height(24.dp))
            ScheduleSection(timing, onActionSelected)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (timing.type) {
                TimingType.AUTO_POST_NOW -> "Your mood will post immediately after publishing."

                TimingType.SCHEDULE -> "Your mood will post on ${timing.scheduledAt?.toLocalDate()} at ${
                    formatTime(
                        timing.scheduledAt?.atZone(ZoneId.systemDefault())?.toInstant()
                            ?.toEpochMilli()
                    )
                }."

                TimingType.MANUAL_ONLY -> "Your mood will remain as a draft until you post it."
            },
            color = GrayTextColor,
            style = Typography.bodySmall,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
private fun TimingOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, color = Color.White, modifier = Modifier.weight(1f))

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = description, color = GrayTextColor, style = Typography.bodySmall)
    }
}

@Composable
private fun ScheduleSection(timing: SettingsTiming, onActionSelected: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionSelected("date") }
                .padding(vertical = 12.dp)
        ) {
            Text(text = "Date", color = Color.White, modifier = Modifier.weight(1f))
            Text(text = timing.scheduledAt?.toLocalDate().toString(), color = GrayTextColor)
        }

        HorizontalDivider(thickness = 0.5.dp)

        // Time selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onActionSelected("time") }
                .padding(vertical = 12.dp)
        ) {
            Text(text = "Time", color = Color.White, modifier = Modifier.weight(1f))

            Text(
                text = formatTime(
                    timing.scheduledAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                ),
                color = GrayTextColor
            )
        }
    }
}

private fun formatTime(millis: Long?): String {
    if (millis == null) return "Select time"

    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(millis)
}
