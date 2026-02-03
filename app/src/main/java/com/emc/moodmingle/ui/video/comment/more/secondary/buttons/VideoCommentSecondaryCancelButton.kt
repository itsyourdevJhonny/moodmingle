package com.emc.moodmingle.ui.video.comment.more.secondary.buttons

import androidx.compose.runtime.Composable

@Composable
fun VideoCommentSecondaryCancelButton(selectedTriggerPage: Int, onClick: () -> Unit) {
    if (selectedTriggerPage == 1) {
        VideoCommentSecondaryButton(label = "Cancel", onClick)
    }
}