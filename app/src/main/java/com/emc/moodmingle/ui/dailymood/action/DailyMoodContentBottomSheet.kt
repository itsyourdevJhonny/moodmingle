package com.emc.moodmingle.ui.dailymood.action

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.data.firebase.model.post.dailymood.media.DailyMoodMedia
import com.emc.moodmingle.data.firebase.model.post.dailymood.text.DailyMoodText
import com.emc.moodmingle.data.firebase.model.post.dailymood.gif.Gif
import com.emc.moodmingle.ui.theme.MentionTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMoodContentBottomSheet(
    selectedAction: String,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onActionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PrimaryDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White) }
    ) {
        listOf("Delete" to R.drawable.remove, "Edit" to R.drawable.edit).forEach { (label, icon) ->
            ActionItem(label, icon, selectedAction, mood, onEdited, onActionSelected, onDismiss)
        }
    }
}

@Composable
private fun ActionItem(
    label: String,
    icon: Int,
    selectedAction: String,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
    onActionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable {
                if (label == "Delete") {
                    performDeleteAction(selectedAction, mood, onEdited)
                    onDismiss()
                } else {
                    performEditAction(selectedAction, onActionSelected)
                }
            }
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (label == "Delete") Color.Red else MentionTextColor,
            modifier = Modifier.size(20.dp)
        )

        Text(text = label, color = Color.White)
    }
}

private fun performDeleteAction(
    selectedAction: String,
    mood: DailyMoodEntity,
    onEdited: (DailyMoodEntity) -> Unit,
) {
    when (selectedAction) {
        "text_sheet" -> onEdited(mood.copy(text = DailyMoodText()))
        "single_image_sheet", "single_video_sheet" -> onEdited(mood.copy(media = DailyMoodMedia()))
        "gif_sheet" -> onEdited(mood.copy(gif = Gif()))
    }
}

private fun performEditAction(selectedAction: String, onActionSelected: (String) -> Unit) {
    when (selectedAction) {
        "text_sheet" -> onActionSelected("text")
        "single_image_sheet" -> onActionSelected("edit_single_image")
        "single_video_sheet" -> onActionSelected("edit_single_video")
        "gif_sheet" -> onActionSelected("gif")
    }
}