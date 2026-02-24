package com.emc.moodmingle.ui.video.comment.more.secondary.trigger

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.video.comment.more.secondary.buttons.VideoCommentSecondaryCancelButton
import com.emc.moodmingle.ui.video.comment.more.secondary.buttons.VideoCommentSecondaryNextButton
import com.emc.moodmingle.ui.video.comment.more.secondary.buttons.VideoCommentSecondaryPreviousButton
import com.emc.moodmingle.utils.components.LoadingDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TriggerPageDialog(
    selectedTriggerPage: Int,
    comment: VideoComment,
    currentUserId: String,
    onSelectedTriggerPage: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTriggerOption by remember { mutableStateOf("") }
    var showLoadingDialog by remember { mutableStateOf(false) }

    if (selectedTriggerPage != 0) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryDark)
            ) {
                if (selectedTriggerPage != 3) {
                    TriggerPageHeader(onDismiss = { onSelectedTriggerPage(0) })
                }

                when (selectedTriggerPage) {
                    1 -> TriggerFirstPage(comment)
                    2 -> TriggerSecondPage(
                        comment,
                        onSelectedTriggerOption = { selectedTriggerOption = it }
                    )

                    3 -> TriggerThirdPage(
                        selectedTriggerOption,
                        onSelectedTriggerOption = { selectedTriggerOption = it },
                        onDismiss = { onSelectedTriggerPage(0) }
                    )
                }

                if (selectedTriggerPage != 3) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        VideoCommentSecondaryCancelButton(selectedTriggerPage) {
                            onSelectedTriggerPage(selectedTriggerPage - 1)
                        }
                        VideoCommentSecondaryPreviousButton(selectedTriggerPage) {
                            onSelectedTriggerPage(selectedTriggerPage - 1)
                        }
                        VideoCommentSecondaryNextButton(
                            selectedTriggerPage,
                            selectedTriggerOption,
                            currentUserId,
                            comment,
                            onShowLoadingDialog = { showLoadingDialog = it },
                            onClick = { onSelectedTriggerPage(selectedTriggerPage + 1) }
                        )
                    }
                }
            }
        }
    }

    if (showLoadingDialog) {
        LoadingDialog("Reporting") {
            scope.launch {
                delay(2000)
                showLoadingDialog = false
                onSelectedTriggerPage(selectedTriggerPage + 1)
                Toast.makeText(context, "Trigger reported successfully.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}