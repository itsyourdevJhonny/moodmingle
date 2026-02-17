package com.emc.moodmingle.ui.dailymood.settings.viewlist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun ViewListVisibilityScreen(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScaffoldHeader(
                title = "Viewer List Visibility"
            ) { onDismiss() }
        }
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
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Control whether you can see who viewed your daily mood.",
            color = Color.White,
            style = Typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        SwitchButton(
            label = if (settings.viewListEnabled)
                "Viewer list is visible"
            else
                "Viewer list is hidden",
            isChecked = settings.viewListEnabled,
            padding = 0.dp,
            onCheckedChange = {
                onSettingsEdited(
                    settings.copy(
                        viewListEnabled = it
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (settings.viewListEnabled) {
                "You can see the list of people who viewed your mood."
            } else {
                "You won’t be able to see who viewed your mood. This does not notify others."
            },
            style = Typography.bodySmall.copy(color = GrayTextColor),
            modifier = Modifier.animateContentSize()
        )
    }
}
