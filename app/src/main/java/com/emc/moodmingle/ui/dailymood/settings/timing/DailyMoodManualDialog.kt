package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.runtime.Composable
import com.emc.moodmingle.ui.create.post.settings.schedule.ScheduleDialog
import java.time.LocalDate

@Composable
fun DailyMoodManualDialog(onManualCreated: (LocalDate?) -> Unit, onDismiss: () -> Unit) {
    ScheduleDialog(onScheduleCreated = { onManualCreated(it.expirationDate) }, onDismiss)
}