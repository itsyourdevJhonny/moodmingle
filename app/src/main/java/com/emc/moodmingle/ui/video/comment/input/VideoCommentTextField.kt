package com.emc.moodmingle.ui.video.comment.input

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.ui.theme.GrayTextColor
import com.emc.moodmingle.ui.theme.SecondaryDark

@Composable
fun VideoCommentTextField(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    replyEnabled: Boolean,
    editEnabled: Boolean,
    onReplyText: (String) -> Unit,
    focusRequester: FocusRequester,
    onFocusedChange: (Boolean) -> Unit
) {
    TextField(
        value = commentText,
        onValueChange = {
            onCommentTextChange(it)
            if (replyEnabled) onReplyText(it)
        },
        shape = RoundedCornerShape(8.dp),
        placeholder = {
            Text(
                text = "Enter a ${
                    when {
                        replyEnabled -> "reply"
                        editEnabled -> "new comment"
                        else -> "comment"
                    }
                }...",
                color = GrayTextColor
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = SecondaryDark,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = SecondaryDark,
            focusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White
        ),
        modifier = Modifier
            .width(280.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusedChange(it.isFocused) }
    )
}