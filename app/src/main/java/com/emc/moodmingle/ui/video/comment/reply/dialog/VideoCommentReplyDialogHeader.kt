package com.emc.moodmingle.ui.video.comment.reply.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.domain.remote.model.video.VideoCommentReply
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.gradientCircleBorder
import com.emc.moodmingle.utils.text.NumberFormatter
import com.emc.moodmingle.viewmodel.remote.FirebaseUserViewModel

@Composable
fun VideoCommentReplyDialogHeader(
    commentReplies: List<VideoCommentReply>,
    commenter: UserEntityFirebase?,
    comment: VideoComment,
    onDismiss: () -> Unit
) {
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onDismiss() },
                tint = Color.White
            )

            AsyncImage(
                model = commenter?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .gradientCircleBorder(),
                contentScale = ContentScale.Crop
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (currentUser?.uid == commenter?.uid) "Your" else commenter?.username} ",
                    style = Typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(text = "comment...", style = Typography.bodyLarge.copy(color = GrayTextColor))
            }
        }

        val formattedTotalRepliesSize =
            NumberFormatter.formatValue(commentReplies.size.toLong(), true)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = formattedTotalRepliesSize,
                style = Typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )

            Text(
                text = " Total ${if (comment.replies.size > 1) "Replies" else "Reply"}",
                style = Typography.bodyLarge.copy(color = GrayTextColor)
            )
        }

        DrawNoPaddingLine(thickness = 0.5.dp)
    }
}