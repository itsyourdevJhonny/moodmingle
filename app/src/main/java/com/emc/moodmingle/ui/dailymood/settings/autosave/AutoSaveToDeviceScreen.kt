package com.emc.moodmingle.ui.dailymood.settings.autosave

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun AutoSaveToDeviceScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Auto-Save to Device") { onBack() } }
    ) { paddingValues ->
        Content(paddingValues, settings, onEdit)
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

        Spacer(modifier = Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = PrimaryDark)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TitleAndDescription()
                    ToggleButton(settings, onSettingsEdited)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AutoSaveExplanation(settings.autoSaveToDevice)
    }
}

@Composable
private fun RowScope.TitleAndDescription() {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = "Automatically save posted moods",
            color = Color.White,
            style = Typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "A copy of your mood will be saved to your device after posting.",
            color = GrayTextColor,
            style = Typography.bodySmall
        )
    }
}

@Composable
private fun ToggleButton(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
) {
    Switch(
        checked = settings.autoSaveToDevice,
        onCheckedChange = { onSettingsEdited(settings.copy(autoSaveToDevice = it)) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MentionTextColor,
            checkedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun AutoSaveExplanation(enabled: Boolean) {

    val text = if (enabled) {
        "After you post a mood, it will automatically be saved to your device gallery."
    } else {
        "Your moods will only exist inside the app unless you manually save them."
    }

    Text(
        text = text,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}
