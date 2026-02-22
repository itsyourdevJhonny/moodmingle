package com.emc.moodmingle.ui.create.dailymood.settings.viewlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun ViewListVisibilityScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    Text(
        text = "Control whether you can see who viewed your daily mood.",
        color = Color.White,
        style = Typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(24.dp))

    SwitchButton(
        label = if (settings.viewListEnabled) "Viewer list is visible" else "Viewer list is hidden",
        isChecked = settings.viewListEnabled,
        padding = 0.dp,
        onCheckedChange = { onEdit(settings.copy(viewListEnabled = it)) }
    )

    Spacer(modifier = Modifier.height(16.dp))

    HorizontalDivider(thickness = 0.5.dp)

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = if (settings.viewListEnabled) "You can see the list of people who viewed your mood." else "You won’t be able to see who viewed your mood. This does not notify others.",
        style = Typography.bodySmall.copy(color = GrayTextColor),
        modifier = Modifier.animateContentSize()
    )
}