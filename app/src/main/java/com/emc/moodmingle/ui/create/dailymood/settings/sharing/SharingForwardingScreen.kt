package com.emc.moodmingle.ui.create.dailymood.settings.sharing

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.SharingSettings
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.MentionBackground
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun SharingForwardingScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    val sharingSettings = settings.sharingSettings

    Text(
        text = "Control whether others can share or forward your mood.",
        color = Color.White,
        style = Typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(24.dp))

    PermissionSectionCard {
        PermissionToggleRow(
            title = "Allow External Sharing",
            description = "Others can share your mood outside this app.",
            checked = sharingSettings.allowExternalSharing,
            onCheckedChange = { value ->
                onEdit(settings.copy(sharingSettings = SharingSettings(allowExternalSharing = value)))
            }
        )

        HorizontalDivider(thickness = 0.5.dp)

        PermissionToggleRow(
            title = "Allow Forwarding",
            description = "Others can forward your mood inside the app.",
            checked = sharingSettings.allowForwarding,
            onCheckedChange = { value ->
                onEdit(settings.copy(sharingSettings = SharingSettings(allowForwarding = value)))
            }
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    SharingExplanation(sharingSettings)
}

@Composable
private fun PermissionSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = PrimaryDark)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun PermissionToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, style = Typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, color = GrayTextColor, style = Typography.bodySmall)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MentionTextColor,
                checkedTrackColor = MentionBackground
            )
        )
    }
}

@Composable
private fun SharingExplanation(settings: SharingSettings) {
    val message = when {
        settings.allowExternalSharing && settings.allowForwarding -> "Your mood can be shared anywhere."
        !settings.allowExternalSharing && settings.allowForwarding -> "Your mood can only be forwarded inside the app."
        settings.allowExternalSharing && !settings.allowForwarding -> "Your mood can be shared externally but not forwarded inside the app."
        else -> "Your mood cannot be shared or forwarded."
    }

    Text(
        text = message,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}

