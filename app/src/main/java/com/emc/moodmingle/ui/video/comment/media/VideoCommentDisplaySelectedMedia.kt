package com.emc.moodmingle.ui.video.comment.media

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable

@Composable
fun VideoCommentDisplaySelectedMedia(
    isSelected: Boolean,
    mediaUris: List<Uri>,
    onSelectedUris: (List<Uri>) -> Unit,
    onSelected: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = isSelected,
        enter = slideInHorizontally(
            initialOffsetX = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    ) {
        VideoCommentSelectedMediaPreview(mediaUris = mediaUris, onSelectedUris = onSelectedUris)
    }

    if (isSelected && mediaUris.isEmpty()) {
        onSelected(false)
    }
}