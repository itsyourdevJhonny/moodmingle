package com.emc.moodmingle.ui.video.comment.more.primary

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.ui.theme.PurplePrimary
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.ui.theme.TertiaryDark
import com.emc.moodmingle.utils.chat.ChatUtils
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import com.emc.moodmingle.utils.modifier.roundedGradientBorder
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import com.emc.moodmingle.viewmodel.firebase.PostViewModelFirebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun PrimaryActions(
    currentUserId: String,
    comment: VideoComment,
    commenter: UserEntityFirebase?,
    onDismiss: () -> Unit,
    onEditEnabled: (Boolean) -> Unit,
    onReplyEnabled: (Boolean) -> Unit,
    onSelectedComment: (VideoComment?) -> Unit,
    onChatClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()
    val postViewModel = hiltViewModel<PostViewModelFirebase>()

    var deleted by remember { mutableStateOf(false) }

    val primaryActions = listOf(
        Triple(R.drawable.edit, "Edit") {
            keyboardController?.show()
            onSelectedComment(comment)
            onEditEnabled(true)
            onDismiss()
        },
        Triple(R.drawable.reply_right, "Reply") {
            keyboardController?.show()
            onSelectedComment(comment)
            onReplyEnabled(true)
            onDismiss()
        },
        Triple(R.drawable.chat, "Talk") {
            checkConversationAndSendMessage(
                context,
                scope,
                conversationViewModel,
                postViewModel,
                currentUserId,
                comment,
                commenter,
                onChatClick,
                onDismiss
            )
        },
        Triple(R.drawable.delete, "Delete") { deleted = true },
        Triple(R.drawable.report, "Report") {
            onDismiss()
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        primaryActions.forEach { (iconRes, text, onClick) ->
            val isFromCurrentUser = currentUserId == comment.commenterId

            if (!isFromCurrentUser && (text == "Edit" || text == "Delete")) return@forEach
            if (isFromCurrentUser && text == "Edit" && comment.comment.isBlank()) return@forEach
            if (isFromCurrentUser && text == "Talk") return@forEach

            PrimaryActionIcon(iconRes, text, onClick)
        }
    }

    if (deleted) {
        ConfirmDeleteDialog(
            comment,
            videoCommentViewModel,
            context,
            onCancel = { deleted = false },
            onDismiss = {
                deleted = false
                onDismiss()
            }
        )
    }
}

private fun checkConversationAndSendMessage(
    context: Context,
    scope: CoroutineScope,
    conversationViewModel: ConversationViewModel,
    postViewModel: PostViewModelFirebase,
    currentUserId: String,
    comment: VideoComment,
    commenter: UserEntityFirebase?,
    onChatClick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val commenterId = comment.commenterId

    conversationViewModel.checkConversationExists(
        user1 = currentUserId,
        user2 = commenterId
    ) { conversation ->
        scope.launch {
            val post = postViewModel.getPostByVideoUrl(comment.videoUrl).first()

            if (conversation != null && post != null) {
                val isPostFromCommenter = post.userId == commenterId

                ChatUtils.sendMessage(
                    message = "Hey, can we talk about your comment from ${if (isPostFromCommenter) "your" else post.username} comment.",
                    senderId = currentUserId,
                    receiverId = commenterId,
                    conversation = conversation,
                    conversationViewModel = conversationViewModel,
                    type = "COMMENT",
                    entityId = comment.id
                )

                Toast.makeText(context, "Talk with ${commenter?.username}", Toast.LENGTH_SHORT)
                    .show()
            } else {
                conversationViewModel.createConversation(
                    Conversation(creatorId = currentUserId, pairId = "$currentUserId $commenterId")
                )
            }
        }
    }

    onChatClick(currentUserId, comment.commenterId)
    onDismiss()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmDeleteDialog(
    comment: VideoComment,
    videoCommentViewModel: VideoCommentViewModel,
    context: Context,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    videoCommentViewModel.deleteComment(comment)
                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                content = { Text(text = "Delete") }
            )
        },
        dismissButton = {
            TextButton(
                onClick = { onCancel() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TertiaryDark,
                    contentColor = Color.White
                ),
                content = { Text(text = "Cancel") }
            )
        },
        title = {
            Text(
                text = "Delete comment?",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(text = "Are you sure you want to delete this comment?") },
        containerColor = SecondaryDark,
        shape = RectangleShape,
        modifier = Modifier.roundedGradientBorder(0.dp)
    )
}

@Composable
private fun PrimaryActionIcon(iconRes: Int, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(SecondaryDark, CircleShape)
            .grayCircleBorder()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = "Action Icon",
            modifier = Modifier.size(24.dp),
            tint = when (text) {
                "Edit" -> Color.Green
                "Reply" -> Color.Blue
                "Delete" -> Color.Red
                "Report" -> Color.Yellow
                "Talk" -> PurplePrimary
                else -> Color.White
            }
        )
    }
}