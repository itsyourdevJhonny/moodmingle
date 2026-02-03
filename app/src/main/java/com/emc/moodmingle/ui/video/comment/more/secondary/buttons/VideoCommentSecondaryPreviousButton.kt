package com.emc.moodmingle.ui.video.comment.more.secondary.buttons

import androidx.compose.runtime.Composable

@Composable
fun VideoCommentSecondaryPreviousButton(selectedTriggerPage: Int, onClick: () -> Unit) {
    if (selectedTriggerPage == 2) {
        VideoCommentSecondaryButton(label = "Previous", onClick)
    }
}