package com.emc.moodmingle.ui.dailymood.settings.screenprotection

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
fun ScreenProtectionScreen(
    settings: DailyMoodSettings,
    onEdit: (DailyMoodSettings) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Screen Recording Protection") { onBack() } }
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

        ScreenProtectionExplanation(settings.screenProtectionEnabled)
    }
}

@Composable
private fun RowScope.TitleAndDescription() {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = "Block screen recording & screenshots",
            color = Color.White,
            style = Typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Prevents others from capturing your mood using screen recording or screenshots.",
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
        checked = settings.screenProtectionEnabled,
        onCheckedChange = { onSettingsEdited(DailyMoodSettings(screenProtectionEnabled = it)) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MentionTextColor,
            checkedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun ScreenProtectionExplanation(enabled: Boolean) {
    val text = if (enabled) {
        "When enabled, screenshots and screen recordings will be blocked while viewing your mood."
    } else {
        "Others may capture your mood using screenshots or screen recording."
    }

    Text(
        text = text,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}
