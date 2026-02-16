package com.emc.moodmingle.ui.dailymood.settings.timing

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.utils.components.DateTimePicker
import java.time.LocalDate

@Composable
fun DailyMoodManualDialog(onManualCreated: (LocalDate?, Long?) -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        DateTimePicker(
            title = "Manual Timing",
            doneLabel = "Save",
            onCreated = { onManualCreated(it.date, it.time) },
            onDismiss = onDismiss
        )
    }
}