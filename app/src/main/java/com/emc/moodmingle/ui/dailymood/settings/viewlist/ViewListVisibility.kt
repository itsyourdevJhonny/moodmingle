package com.emc.moodmingle.ui.dailymood.settings.viewlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun ViewListVisibility(
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

        HorizontalDivider(thickness = 0.5.dp)

        Text(
            text = if (settings.viewListEnabled) "Views will be displayed in the list." else "Views will not be displayed in the list. You can still change this later.",
            style = Typography.bodySmall.copy(color = GrayTextColor),
            modifier = Modifier
                .padding(top = 12.dp, start = 16.dp)
                .animateContentSize()
        )
    }
}