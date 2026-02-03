package com.emc.moodmingle.ui.video.comment.reply.dialog

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
import com.emc.moodmingle.data.firebase.viewmodel.video.VideoCommentReplyViewModel
import com.emc.moodmingle.ui.post.action.DrawNoPaddingLine
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.PrimaryDark
import com.emc.moodmingle.ui.theme.SecondaryDark
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.firebase.FirebaseUserViewModel

@Composable
fun VideoCommentReplyDialogFooter(comment: VideoComment) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val videoCommentReplyViewModel = hiltViewModel<VideoCommentReplyViewModel>()
    val userViewModel = hiltViewModel<FirebaseUserViewModel>()
    val currentUser by userViewModel.loggedUser

    var replyValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryDark)
    ) {
        DrawNoPaddingLine(thickness = 0.5.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(PrimaryDark),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TextField(
                value = replyValue,
                onValueChange = { replyValue = it },
                shape = RoundedCornerShape(8.dp),
                placeholder = { Text(text = "Enter a reply...", color = GrayTextColor) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = SecondaryDark,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = SecondaryDark,
                    focusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                modifier = Modifier.width(280.dp)
            )

            Icon(
                painter = painterResource(R.drawable.send),
                contentDescription = "Reply",
                modifier = Modifier
                    .drawGradient()
                    .clickable {
                        if (replyValue.isBlank()) return@clickable
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)

                        videoCommentReplyViewModel.createReply(
                            reply = VideoCommentReply(
                                videoCommentId = comment.id,
                                replierId = currentUser?.uid ?: "",
                                reply = replyValue
                            )
                        )

                        Toast.makeText(context, "Replied to comment", Toast.LENGTH_SHORT).show()

                        replyValue = ""
                    }
            )
        }
    }
}