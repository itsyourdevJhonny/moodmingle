package com.emc.moodmingle.ui.dailymood.settings.notify

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.data.firebase.model.post.dailymood.settings.NotifyType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.components.ScaffoldHeader

@Composable
fun NotifyPeopleScreen(
    settings: DailyMoodSettings,
    onSettingsEdited: (DailyMoodSettings) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = { ScaffoldHeader(title = "Notify People") { onDismiss() } }
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
            text = "Choose who should receive a notification when you post a new daily mood.",
            color = Color.White,
            style = Typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        NotifyOption(
            icon = R.drawable.none_image,
            title = "No notifications",
            description = "No one will be notified when you post a mood.",
            selected = settings.notifyType == NotifyType.NONE
        ) { onSettingsEdited(settings.copy(notifyType = NotifyType.NONE)) }

        NotifyOption(
            icon = R.drawable.followers,
            title = "Followers",
            description = "All your followers will receive a notification.",
            selected = settings.notifyType == NotifyType.FOLLOWERS
        ) { onSettingsEdited(settings.copy(notifyType = NotifyType.FOLLOWERS)) }

        NotifyOption(
            icon = R.drawable.supporter,
            title = "Supporters only",
            description = "Only people marked as supporters will be notified.",
            selected = settings.notifyType == NotifyType.SUPPORTERS
        ) { onSettingsEdited(settings.copy(notifyType = NotifyType.SUPPORTERS)) }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (settings.notifyType) {
                NotifyType.NONE -> "Notifications are disabled for your daily moods."
                NotifyType.FOLLOWERS -> "All followers will be notified when you post a mood."
                NotifyType.SUPPORTERS -> "Only your supporters will receive notifications."
            },
            color = GrayTextColor,
            style = Typography.bodySmall,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
private fun NotifyOption(
    icon: Int,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Text(text = title, color = Color.White)
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = description, color = GrayTextColor, style = Typography.bodySmall)
    }
}
