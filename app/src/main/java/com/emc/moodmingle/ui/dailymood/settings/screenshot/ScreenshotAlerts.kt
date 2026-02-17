package com.emc.moodmingle.ui.dailymood.settings.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun ScreenshotAlerts(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "View List Visibility") { onDismiss() } }
    ) { paddingValues ->
        Content(paddingValues, settings, onSettingsEdited)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        SwitchButton(
            label = if (settings.viewListEnabled) "Enabled" else "Disabled",
            isChecked = settings.viewListEnabled,
            padding = 16.dp,
            onCheckedChange = { onSettingsEdited(settings.copy(viewListEnabled = it)) }
        )
    }
}