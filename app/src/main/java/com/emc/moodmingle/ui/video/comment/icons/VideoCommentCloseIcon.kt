package com.emc.moodmingle.ui.video.comment.icons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.video.VideoComment

@Composable
fun VideoCommentCloseIcon(
    isSelected: Boolean,
    onReplyEnabled: (Boolean) -> Unit,
    onSelectedComment: (VideoComment?) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AnimatedVisibility(
        visible = isSelected,
        enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300))
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    onSelectedComment(null)
                    onReplyEnabled(false)
                    keyboardController?.hide()
                },
            tint = Color.Red
        )
    }
}