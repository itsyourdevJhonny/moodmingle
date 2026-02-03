package com.emc.moodmingle.ui.video.comment.more.secondary.support

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.video.Support
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentViewModel
import com.emc.moodmingle.utils.components.LoadingDialog
import com.emc.moodmingle.utils.components.BackIcon
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.utils.modifier.grayCircleBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SupportPageHeader(
    currentUserId: String,
    selectedType: String,
    message: String,
    comment: VideoComment,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val videoCommentViewModel = hiltViewModel<VideoCommentViewModel>()
    var isLoading by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackIcon(onClick = onDismiss)

        SendButton(
            message,
            selectedType,
            onClick = {
                val newSupport = Support(
                    supporterId = currentUserId,
                    message = message,
                    supportType = selectedType
                )
                videoCommentViewModel.updateComment(comment = comment.copy(supports = comment.supports + newSupport))
                isLoading = true
            }
        )
    }

    if (isLoading) {
        LoadingDialog(text = "Sending Support...") {
            scope.launch {
                delay(2000)
                isLoading = false
                Toast.makeText(context, "Support sent successfully", Toast.LENGTH_SHORT).show()
                onDismiss()
            }
        }
    }
}

@Composable
private fun SendButton(message: String, selectedType: String, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = message.isNotEmpty() && selectedType.isNotEmpty(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .grayCircleBorder()
                .clickable { onClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.continue_right),
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .drawGradient()
                )

                Text(text = " Send", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}