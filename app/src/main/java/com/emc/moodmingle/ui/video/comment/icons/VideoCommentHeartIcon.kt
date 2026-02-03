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
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.text.NumberFormatter

@Composable
fun VideoCommentHeartIcon(comment: VideoComment, currentUserId: String, isSelected: Boolean) {
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()
    val isCurrentUserReacted = comment.reactorIds.contains(currentUserId)

    AnimatedVisibility(
        visible = !isSelected,
        enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.love),
                contentDescription = "Love",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        videoCommentViewModel.updateComment(
                            comment = comment.copy(reactorIds = if (isCurrentUserReacted) comment.reactorIds - currentUserId else comment.reactorIds + currentUserId)
                        )
                    },
                tint = if (isCurrentUserReacted) Color.Red else Color.White
            )

            if (comment.reactorIds.isNotEmpty()) {
                val formattedReactionsSize =
                    NumberFormatter.formatValue(comment.reactorIds.size.toLong() + 1000000, true)

                Text(
                    text = " $formattedReactionsSize",
                    style = Typography.bodyMedium.copy(color = GrayTextColor)
                )
            }
        }
    }
}