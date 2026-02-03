package com.emc.moodmingle.ui.video.comment.input

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentReplyViewModel
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.service.UploadVideoCommentService
import com.emc.moodmingle.utils.modifier.drawGradient
import kotlinx.coroutines.launch

@Composable
fun SendCommentOrReplyOrEdit(
    replyEnabled: Boolean,
    editEnabled: Boolean,
    replyText: String,
    videoCommentViewModel: VideoCommentViewModel,
    selectedComment: VideoComment?,
    currentUserId: String,
    onSelectedComment: (VideoComment?) -> Unit,
    onReplyEnabled: (Boolean) -> Unit,
    onEditEnabled: (Boolean) -> Unit,
    onReplyText: (String) -> Unit,
    commentText: String,
    videoUrl: String,
    mediaUris: List<Uri>,
    listState: LazyListState,
    emotion: String,
    isAnonymous: Boolean,
    onCommentTextChange: (String) -> Unit,
    onSelectedUris: (List<Uri>) -> Unit,
    onSelectedEmotion: (Pair<String, String>) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val videoCommentReplyViewModel = hiltViewModel<VideoCommentReplyViewModel>()

    Icon(
        painter = painterResource(R.drawable.send),
        contentDescription = "Send",
        modifier = Modifier
            .drawGradient()
            .clickable {
                when {
                    replyEnabled -> {
                        if (replyText.isBlank()) return@clickable

                        videoCommentReplyViewModel.createReply(
                            reply = VideoCommentReply(
                                videoCommentId = selectedComment?.id ?: "",
                                replierId = currentUserId,
                                reply = replyText
                            )
                        )

                        onSelectedComment(null)
                        onReplyEnabled(false)
                        onReplyText("")
                    }

                    editEnabled -> {
                        if (commentText.isBlank()) return@clickable

                        onSelectedComment(null)
                        onEditEnabled(false)
                        videoCommentViewModel.updateComment(comment = selectedComment!!.copy(comment = commentText))
                    }

                    else -> {
                        if (commentText.isBlank() && mediaUris.isEmpty()) return@clickable

                        val uploadIntent = Intent(context, UploadVideoCommentService::class.java).apply {
                            action = UploadVideoCommentService.ACTION_UPLOAD

                            putExtra(UploadVideoCommentService.EXTRA_VIDEO_URL, videoUrl)
                            putExtra(UploadVideoCommentService.EXTRA_COMMENT_TEXT, commentText)
                            putExtra(UploadVideoCommentService.EXTRA_CURRENT_USER_ID, currentUserId)
                            putStringArrayListExtra(UploadVideoCommentService.EXTRA_URIS, ArrayList(mediaUris.map { it.toString() }))
                            putExtra(UploadVideoCommentService.EXTRA_EMOTION, emotion)
                            putExtra(UploadVideoCommentService.EXTRA_ANONYMOUS, isAnonymous)
                        }

                        context.startForegroundService(uploadIntent)
                        scope.launch { listState.animateScrollToItem(0) }
                    }
                }

                onCommentTextChange("")
                onSelectedUris(emptyList())

                if (isAnonymous) onCheckedChange(false)
                if (emotion.isNotBlank()) onSelectedEmotion("" to "")

                toastMessage(context, replyEnabled, editEnabled)

                focusManager.clearFocus()
            }
    )
}

private fun toastMessage(
    context: Context,
    replyEnabled: Boolean,
    editEnabled: Boolean
) {
    Toast.makeText(
        context,
        when {
            replyEnabled -> "Replied to comment"
            editEnabled -> "Comment edited"
            else -> "Uploading comment..."
        },
        Toast.LENGTH_SHORT
    ).show()
}