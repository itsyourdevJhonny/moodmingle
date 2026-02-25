package com.emc.moodmingle.ui.video.comment.icons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.domain.remote.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.text.NumberFormatter

@Composable
fun VideoCommentDislikeIcon(comment: VideoComment, currentUserId: String, isSelected: Boolean) {
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()
    val dislikerIds = comment.dislikerIds
    val isCurrentUserDisliked = dislikerIds.contains(currentUserId)

    AnimatedVisibility(
        visible = !isSelected,
        enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.dislike),
                contentDescription = "Dislike",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        videoCommentViewModel.updateComment(
                            comment = comment.copy(dislikerIds = if (isCurrentUserDisliked) dislikerIds - currentUserId else dislikerIds + currentUserId)
                        )
                    },
                tint = if (isCurrentUserDisliked) Color.Blue.copy(alpha = 0.9f) else Color.White
            )

            if (dislikerIds.isNotEmpty()) {
                val formattedSize = NumberFormatter.formatValue(dislikerIds.size.toLong(), true)

                Text(
                    text = " $formattedSize",
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )
            }
        }
    }
}