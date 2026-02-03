package com.emc.moodmingle.ui.video.comment.media.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.ui.theme.PrimaryDark

@Composable
fun VideoCommentMediaDialog(
    commenterUsername: String,
    mediaUrls: List<String>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark)
        ) {
            VideoCommentMediaDialogHeader(commenterUsername, onDismiss)
            VideoCommentMediaDialogContent(mediaUrls)
        }
    }
}