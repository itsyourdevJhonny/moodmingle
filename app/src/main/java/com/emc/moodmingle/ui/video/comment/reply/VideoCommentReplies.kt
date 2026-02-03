package com.emc.moodmingle.ui.video.comment.reply

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentReplyViewModel
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.theme.BrushPrimaryGradient
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun ColumnScope.VideoCommentReplies(comment: VideoComment, onShowReplies: () -> Unit) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val videoCommentReplyViewModel = hiltViewModel<VideoCommentReplyViewModel>()

    val replies by remember(comment.id) {
        videoCommentReplyViewModel.getRepliesByCommentId(comment.id)
    }.collectAsState(initial = emptyList())

    if (replies.isNotEmpty()) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.End),
            visible = replies.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)) + expandHorizontally(
                animationSpec = tween(
                    300
                )
            ),
            exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                animationSpec = tween(
                    300
                )
            )
        ) {
            val replySize = replies.size

            val lastReplierResult by remember {
                userViewModel.getUserByUid(replies[0].replierId)
            }.collectAsState(initial = null)

            val lastReplier = lastReplierResult?.getOrNull()
            val lastReply = replies[0]

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.reply_right),
                        contentDescription = "Reply",
                        modifier = Modifier
                            .size(16.dp)
                            .drawGradient()
                    )

                    val formattedTotalRepliesSize =
                        NumberFormatter.formatValue(replySize.toLong(), true)

                    Text(
                        text = " $formattedTotalRepliesSize ${if (replies.size > 1) "Replies" else "Reply"} ",
                        style = Typography.bodyMedium.copy(
                            color = GrayTextColor,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    HorizontalDivider(thickness = 0.5.dp)
                }

                LastReply(replies, lastReplier, lastReply, onShowReplies)
            }
        }
    }
}

@Composable
private fun ColumnScope.LastReply(
    replies: List<VideoCommentReply>,
    lastReplier: UserEntityFirebase?,
    lastReply: VideoCommentReply,
    onShowReplies: () -> Unit
) {
    Row(
        modifier = Modifier
            .align(Alignment.End)
            .padding(top = 8.dp)
            .clickable { onShowReplies() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = lastReplier?.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .gradientCircleBorder(),
            contentScale = ContentScale.Crop
        )

        Column {
            Text(
                text = formatTimeAgo(lastReply.timestamp),
                style = Typography.labelSmall.copy(color = GrayTextColor)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@",
                    style = Typography.bodyLarge.copy(
                        brush = BrushPrimaryGradient,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = lastReplier?.username ?: "",
                    style = Typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 70.dp)
                )

                Text(
                    text = " ${lastReply.reply} ",
                    style = Typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp).animateContentSize()
                )

                if (replies.size > 1) {
                    val formattedRepliesSize =
                        NumberFormatter.formatValue(replies.size.toLong() - 1, true)

                    Text(
                        text = "+$formattedRepliesSize more...",
                        style = Typography.bodyMedium.copy(color = GrayTextColor)
                    )
                }
            }
        }
    }
}