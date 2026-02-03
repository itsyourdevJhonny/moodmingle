package com.emc.moodmingle.ui.video.comment.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.Typography

@Composable
fun ColumnScope.VideoCommentReplying(replyEnabled: Boolean, editEnabled: Boolean) {
    AnimatedVisibility(
        visible = replyEnabled && !editEnabled,
        enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300))
    ) {
        Text(
            text = "Replying...",
            style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
        )
    }
}