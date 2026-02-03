package com.emc.moodmingle.ui.remix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.utils.components.SwitchButton

@Composable
fun BoxScope.RemixUseCommentSwitchButton(
    comment: VideoComment?,
    useCommentMessage: Boolean,
    onUseCommentMessage: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .align(Alignment.TopCenter)
    ) {
        SwitchButton(
            label = "Use comment as description",
            isChecked = useCommentMessage,
            onCheckedChange = onUseCommentMessage
        )
    }

    onDescriptionChange(if (useCommentMessage) comment?.comment ?: "" else "")
}