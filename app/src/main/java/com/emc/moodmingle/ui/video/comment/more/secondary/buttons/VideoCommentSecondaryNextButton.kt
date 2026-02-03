package com.emc.moodmingle.ui.video.comment.more.secondary.buttons

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.data.firebase.model.video.Trigger
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel

@Composable
fun VideoCommentSecondaryNextButton(
    selectedTriggerPage: Int,
    selectedTriggerOption: String,
    currentUserId: String,
    comment: VideoComment,
    onShowLoadingDialog: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()

    VideoCommentSecondaryButton(
        label = "Next",
        onClick = {
            if (selectedTriggerPage == 2) {
                if (selectedTriggerOption.isBlank()) {
                    Toast.makeText(context, "Please select an option", Toast.LENGTH_SHORT).show()
                    return@VideoCommentSecondaryButton
                } else {
                    val newTrigger = Trigger(triggererId = currentUserId, description = selectedTriggerOption)
                    videoCommentViewModel.updateComment(comment = comment.copy(triggers = comment.triggers + newTrigger))
                    onShowLoadingDialog(true)
                }
            } else {
                onClick()
            }
        }
    )
}