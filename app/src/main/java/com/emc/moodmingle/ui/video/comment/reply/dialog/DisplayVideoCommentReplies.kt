package com.emc.moodmingle.ui.video.comment.reply.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentReplyViewModel
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayVideoCommentReplies(
    comment: VideoComment,
    userViewModel: FirebaseUserViewModel,
    onDismiss: () -> Unit
) {
    val videoCommentReplyViewModel = hiltViewModel<VideoCommentReplyViewModel>()

    val commenterResult by remember(comment.commenterId) {
        userViewModel.getUserByUid(comment.commenterId)
    }.collectAsState(initial = null)

    val commenter = commenterResult?.getOrNull()

    val commentReplies by remember(comment.id) {
        videoCommentReplyViewModel.getRepliesByCommentId(comment.id)
    }.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { VideoCommentReplyDialogHeader(commentReplies, commenter, comment, onDismiss) },
        bottomBar = { VideoCommentReplyDialogFooter(comment) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            VideoCommentReplyDialogContent(commentReplies, comment, userViewModel)
        }
    }
}