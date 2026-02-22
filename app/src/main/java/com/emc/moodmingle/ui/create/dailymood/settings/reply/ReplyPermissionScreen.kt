package com.emc.moodmingle.ui.create.dailymood.settings.reply

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.ReplyPermissionType
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun ReplyPermissionScreen(settings: DailyMoodSettings, onEdit: (DailyMoodSettings) -> Unit) {
    Text(
        text = "Choose who can reply to your daily moods.",
        color = Color.White,
        style = Typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(24.dp))

    ReplyPermissionType.entries.forEach { permission ->
        ReplyPermissionItem(
            permission = permission,
            selected = settings.replyPermissionType == permission,
            onClick = { onEdit(settings.copy(replyPermissionType = permission)) }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    Spacer(modifier = Modifier.height(24.dp))
    ReplyPermissionDescription(settings.replyPermissionType)
}

@Composable
private fun ReplyPermissionItem(
    permission: ReplyPermissionType,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val background = if (selected) SecondaryDark else PrimaryDark
    val borderColor = if (selected) Color.White else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = background),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PermissionTitleAndSubtitle(permission)
            SelectedIcon(selected)
        }
    }
}

@Composable
private fun SelectedIcon(selected: Boolean) {
    AnimatedVisibility(visible = selected) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun RowScope.PermissionTitleAndSubtitle(permission: ReplyPermissionType) {
    Column(modifier = Modifier.weight(1f)) {
        Text(text = permission.title(), color = Color.White, style = Typography.bodyLarge)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = permission.subtitle(), color = GrayTextColor, style = Typography.bodySmall)
    }
}

@Composable
private fun ReplyPermissionDescription(permission: ReplyPermissionType) {
    val description = when (permission) {
        ReplyPermissionType.EVERYONE -> "Anyone who can see your mood can reply."
        ReplyPermissionType.FOLLOWERS_ONLY -> "Only people who follow you can reply."
        ReplyPermissionType.SUPPORTERS_ONLY -> "Only your supporters can reply."
        ReplyPermissionType.NO_ONE -> "Replies are disabled for this mood."
    }

    Text(
        text = description,
        color = GrayTextColor,
        style = Typography.bodySmall,
        modifier = Modifier.animateContentSize()
    )
}

private fun ReplyPermissionType.title(): String {
    return when (this) {
        ReplyPermissionType.EVERYONE -> "Everyone"
        ReplyPermissionType.FOLLOWERS_ONLY -> "Followers Only"
        ReplyPermissionType.SUPPORTERS_ONLY -> "Supporters Only"
        ReplyPermissionType.NO_ONE -> "No One"
    }
}

private fun ReplyPermissionType.subtitle(): String {
    return when (this) {
        ReplyPermissionType.EVERYONE -> "Public replies allowed"
        ReplyPermissionType.FOLLOWERS_ONLY -> "Limit replies to followers"
        ReplyPermissionType.SUPPORTERS_ONLY -> "Limit replies to supporters"
        ReplyPermissionType.NO_ONE -> "Disable replies completely"
    }
}
