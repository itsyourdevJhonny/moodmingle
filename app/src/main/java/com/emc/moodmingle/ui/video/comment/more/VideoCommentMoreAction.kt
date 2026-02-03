package com.emc.moodmingle.ui.video.comment.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.ui.video.comment.more.primary.PrimaryActions
import com.emc.moodmingle.ui.video.comment.more.secondary.SecondaryActions
import com.emc.moodmingle.utils.modifier.drawGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCommentMoreAction(
    currentUserId: String,
    commenter: UserEntityFirebase?,
    comment: VideoComment,
    onSelectedComment: (VideoComment?) -> Unit,
    onReplyEnabled: (Boolean) -> Unit,
    onEditEnabled: (Boolean) -> Unit,
    onChatClick: (String, String) -> Unit,
    onDismiss: () -> Unit,
    onRemix: (String, String) -> Unit
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        containerColor = PrimaryDark,
        dragHandle = { BottomSheetDefaults.DragHandle(modifier = Modifier.drawGradient()) },
    ) {
        Column {
            Label("Manage Comment")

            PrimaryActions(
                currentUserId,
                comment,
                commenter,
                onDismiss,
                onEditEnabled,
                onReplyEnabled,
                onSelectedComment,
                onChatClick
            )

            Label("Emotional Response")

            SecondaryActions(comment, currentUserId, onRemix)
        }
    }
}

@Composable
private fun Label(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = text, style = Typography.bodyMedium.copy(color = Color.White))
        HorizontalDivider()
    }
}