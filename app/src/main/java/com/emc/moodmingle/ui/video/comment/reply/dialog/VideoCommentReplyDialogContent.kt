package com.emc.moodmingle.ui.video.comment.reply.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentReplyViewModel
import com.emc.moodmingle.data.model.post.formatTimeAgo
import com.emc.moodmingle.ui.post.text.ExpandableAutoDetectClickableText
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel
import kotlinx.coroutines.launch

@Composable
fun VideoCommentReplyDialogContent(
    commentReplies: List<VideoCommentReply>,
    comment: VideoComment,
    userViewModel: FirebaseUserViewModel
) {
    val videoCommentReplyViewModel = hiltViewModel<VideoCommentReplyViewModel>()

    val currentUser by userViewModel.loggedUser
    val currentUserId = currentUser?.uid ?: ""

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = commentReplies, key = { it.id }) { reply ->
            val replierResult by remember(comment.commenterId) {
                userViewModel.getUserByUid(comment.commenterId)
            }.collectAsState(initial = null)

            val replier = replierResult?.getOrNull()

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VideoCommentReplyAvatar(replier)

                Column {
                    VideoCommentReplyTimeAndUsername(reply, replier)
                    VideoCommentReplyText(reply)
                    VideoCommentReplyReaction(
                        reply,
                        currentUserId,
                        videoCommentReplyViewModel
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.VideoCommentReplyReaction(
    reply: VideoCommentReply,
    currentUserId: String,
    videoCommentReplyViewModel: VideoCommentReplyViewModel,
) {
    val scope = rememberCoroutineScope()
    val isCurrentUserReacted = reply.reactorIds.contains(currentUserId)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .align(Alignment.End)
            .padding(end = 8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.love),
            contentDescription = "Love",
            modifier = Modifier
                .size(20.dp)
                .clickable {
                    scope.launch {
                        videoCommentReplyViewModel.updateReply(
                            reply.copy(
                                reactorIds = if (isCurrentUserReacted) reply.reactorIds - currentUserId else reply.reactorIds + currentUserId
                            )
                        )
                    }
                },
            tint = if (isCurrentUserReacted) Color.Red else Color.White
        )

        if (reply.reactorIds.isNotEmpty()) {
            val formattedReactionsSize =
                NumberFormatter.formatValue(reply.reactorIds.size.toLong(), true)

            Text(
                text = " $formattedReactionsSize",
                style = Typography.bodyMedium.copy(color = GrayTextColor)
            )
        }
    }
}

@Composable
private fun VideoCommentReplyAvatar(replier: UserEntityFirebase?) {
    AsyncImage(
        model = replier?.avatarUrl,
        contentDescription = "Avatar",
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .gradientCircleBorder(),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun VideoCommentReplyTimeAndUsername(
    reply: VideoCommentReply,
    replier: UserEntityFirebase?
) {
    Text(
        text = formatTimeAgo(reply.timestamp),
        style = Typography.bodySmall.copy(color = GrayTextColor)
    )

    Text(
        text = replier?.username ?: "",
        style = Typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Black)
    )
}

@Composable
private fun VideoCommentReplyText(reply: VideoCommentReply) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .background(SecondaryDark, RoundedCornerShape(8.dp))
            .border(width = 0.5.dp, color = TertiaryDark, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            ExpandableAutoDetectClickableText(
                fullText = reply.reply,
                style = Typography.bodyLarge.copy(color = Color.White),
                hasPadding = false,
                minLines = 5
            )
        }
    }
}