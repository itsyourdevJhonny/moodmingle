package com.emc.moodmingle.ui.video.comment.more.secondary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.comment.more.secondary.support.SupportPageDialog
import com.emc.moodmingle.ui.video.comment.more.secondary.trigger.TriggerPageDialog
import com.emc.moodmingle.utils.modifier.drawGradient

private data class Action(
    val icon: Int,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
fun SecondaryActions(
    comment: VideoComment,
    currentUserId: String,
    onRemix: (String, String) -> Unit
) {
    var selectedTriggerPage by remember { mutableIntStateOf(0) }
    var showSupportDialog by remember { mutableStateOf(false) }

    val secondaryActions = listOf(
        Action(
            R.drawable.triggering,
            "Flag as Triggering",
            "Report comment as triggering or harmful"
        ) { selectedTriggerPage = 1 },
        Action(
            R.drawable.support,
            "Offer Support",
            "Send a supportive message to the comment"
        ) { showSupportDialog = true },
        Action(
            R.drawable.remix,
            "Remix Comment",
            "Create a new post inspired by this comment"
        ) { onRemix(comment.id, "COMMENT") }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        secondaryActions.forEach { action ->
            Box(
                modifier = Modifier
                    .background(SecondaryDark, RoundedCornerShape(8.dp))
                    .clickable { action.onClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionIcon(action)
                    ActionTitleAndDescription(action)
                }
            }
        }
    }

    TriggerPageDialog(
        selectedTriggerPage,
        comment,
        currentUserId,
        onSelectedTriggerPage = { selectedTriggerPage = it }
    )

    if (showSupportDialog) {
        SupportPageDialog(currentUserId, comment, onDismiss = { showSupportDialog = false })
    }
}

@Composable
private fun ActionTitleAndDescription(action: Action) {
    Column {
        Text(
            text = action.title,
            style = Typography.bodyMedium.copy(color = Color.White)
        )

        Text(
            text = action.description,
            style = Typography.bodySmall.copy(color = GrayTextColor)
        )
    }
}

@Composable
private fun ActionIcon(action: Action) {
    Icon(
        painter = painterResource(action.icon),
        contentDescription = action.title,
        modifier = Modifier
            .size(20.dp)
            .drawGradient()
    )
}