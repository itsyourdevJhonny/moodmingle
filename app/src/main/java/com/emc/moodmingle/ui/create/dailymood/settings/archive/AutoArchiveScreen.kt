package com.emc.moodmingle.ui.create.dailymood.settings.archive

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun AutoArchiveScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = PrimaryDark)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TitleAndDescription()
                ToggleButton(settings, onEdit)
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    AutoArchiveExplanation(settings.autoArchive)
}

@Composable
private fun RowScope.TitleAndDescription() {
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = "Automatically archive expired moods",
            color = Color.White,
            style = Typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Expired moods will be saved privately instead of being deleted.",
            color = GrayTextColor,
            style = Typography.bodySmall
        )
    }
}

@Composable
private fun ToggleButton(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    Switch(
        checked = settings.autoArchive,
        onCheckedChange = { onEdit(settings.copy(autoArchive = it)) },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = MentionTextColor,
            checkedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun AutoArchiveExplanation(enabled: Boolean) {
    val text = if (enabled) {
        "When your mood expires, it will be moved to your private archive and only visible to you."
    } else {
        "When your mood expires, it will be permanently removed."
    }

    Text(
        text = text,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}

