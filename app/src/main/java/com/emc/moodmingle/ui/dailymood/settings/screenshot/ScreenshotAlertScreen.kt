package com.emc.moodmingle.ui.dailymood.settings.screenshot

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun ScreenshotAlertScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    Text(
        text = "Get notified when someone captures your daily mood.",
        color = Color.White,
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "This may not work on all devices or third-party screen recording apps.",
        color = GrayTextColor,
        style = MaterialTheme.typography.bodySmall
    )

    Spacer(modifier = Modifier.height(24.dp))

    SwitchButton(
        label = if (settings.screenshotAlertEnabled) "Screenshot alerts are ON" else "Screenshot alerts are OFF",
        isChecked = settings.screenshotAlertEnabled,
        padding = 0.dp,
        onCheckedChange = { onEdit(settings.copy(screenshotAlertEnabled = it)) }
    )
}