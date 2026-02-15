package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.runtime.Composable
import com.emc.moodmingle.utils.components.DateTimePicker
import java.time.LocalDate

@Composable
fun DailyMoodManualDialog(onManualCreated: (LocalDate?, Long?) -> Unit, onDismiss: () -> Unit) {

    DateTimePicker(
        title = "Manual Timing",
        doneLabel = "Save",
        onCreated = { onManualCreated(it.date, it.time) },
        onDismiss = onDismiss
    )
}