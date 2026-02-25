package com.emc.moodmingle.ui.video.comment.icons

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.domain.remote.model.video.VideoCommentReply
import com.emc.moodmingle.domain.remote.viewmodel.video.VideoCommentReplyViewModel
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.Typography
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.text.NumberFormatter

@Composable
fun VideoCommentSendOrReplyIcon(
    currentUserId: String,
    comment: VideoComment,
    replyText: String,
    isSelected: Boolean,
    focusRequester: FocusRequester,
    onReplyText: (String) -> Unit,
    onReplyEnabled: (Boolean) -> Unit,
    onSelectedComment: (VideoComment?) -> Unit,
    onCommentTextChange: (String) -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val videoCommentReplyViewModel = hiltViewModel<VideoCommentReplyViewModel>()

    val replyCount by remember(comment.id) {
        videoCommentReplyViewModel.getReplyCountByCommentId(comment.id)
    }.collectAsState(initial = 0)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(if (isSelected) R.drawable.send else R.drawable.reply_right),
            contentDescription = "Reply",
            modifier = Modifier
                .size(20.dp)
                .drawGradient()
                .clickable {
                    if (isSelected) {
                        if (replyText.isBlank()) return@clickable

                        videoCommentReplyViewModel.createReply(
                            reply = VideoCommentReply(
                                videoCommentId = comment.id,
                                replierId = currentUserId,
                                reply = replyText
                            )
                        )

                        keyboardController?.hide()

                        onReplyEnabled(false)
                        onReplyText("")
                        onCommentTextChange("")
                        onSelectedComment(null)

                        Toast.makeText(context, "Reply posted", Toast.LENGTH_SHORT).show()
                    } else {
                        focusRequester.requestFocus()
                        keyboardController?.show()

                        onReplyEnabled(true)
                        onSelectedComment(comment)
                    }
                }
        )

        if (!isSelected && replyCount >= 1) {
            Text(
                text = " ${NumberFormatter.formatValue(replyCount + 2900000, true)}",
                style = Typography.bodyMedium.copy(color = GrayTextColor)
            )
        }
    }
}