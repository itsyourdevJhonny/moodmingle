package com.emc.moodmingle.ui.video.comment.icons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.comment.more.secondary.support.VideoCommentGroupedSupportContent
import com.emc.moodmingle.ui.video.comment.more.secondary.trigger.VideoCommentGroupedTriggerContent
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.text.NumberFormatter

@Composable
fun VideoCommentTriggerAndSupportIcon(comment: VideoComment) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.background(SecondaryDark, RoundedCornerShape(8.dp))) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .clickable { showDialog = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(comment.triggers.size, R.drawable.triggering)
            Icon(comment.supports.size, R.drawable.support)
        }
    }

    if (showDialog) {
        TriggerAndSupportDialog(comment, onDismiss = { showDialog = false })
    }
}

@Composable
private fun TriggerAndSupportDialog(comment: VideoComment, onDismiss: () -> Unit) {
    var selectedTab by remember { mutableStateOf("trigger_tab") }

    val triggers = comment.triggers.groupingBy { it.description }.eachCount().toList()
    val supports = comment.supports

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
        ) {
            Header(onDismiss, selectedTab)

            Tabs(comment, selectedTab, onSelectedTab = { selectedTab = it })

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(if (selectedTab == "trigger_tab") triggers else supports) { content ->
                    if (selectedTab == "trigger_tab") {
                        VideoCommentGroupedTriggerContent(content)
                    } else {
                        VideoCommentGroupedSupportContent(content)
                    }
                }
            }
        }
    }
}

@Composable
private fun Tabs(comment: VideoComment, selectedTab: String, onSelectedTab: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("trigger_tab", "support_tab").forEach {
            val tabText = if (it == "trigger_tab") "Triggers" else "Supports"
            val isSelected = selectedTab == it

            Column(
                modifier = Modifier
                    .width(170.dp)
                    .clickable { onSelectedTab(it) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tabText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )

                    Text(
                        text = NumberFormatter.formatValue(
                            (if (it == "trigger_tab") comment.triggers.size else comment.supports.size).toLong(),
                            true
                        ),
                        style = Typography.bodyLarge.copy(color = GrayTextColor)
                    )
                }

                if (isSelected) DrawNoPaddingLine()
            }
        }
    }
}

@Composable
private fun Header(onDismiss: () -> Unit, selectedTab: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        BackIcon(onClick = onDismiss)
        Text(
            text = if (selectedTab == "trigger_tab") "Triggers" else "Supports",
            style = Typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun Icon(size: Int, @DrawableRes iconRes: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Icon",
            tint = Color.White,
            modifier = Modifier.size(if (iconRes == R.drawable.triggering) 16.dp else 14.dp)
        )

        Text(
            text = NumberFormatter.formatValue(size.toLong(), true),
            style = Typography.bodyMedium.copy(color = GrayTextColor)
        )
    }
}