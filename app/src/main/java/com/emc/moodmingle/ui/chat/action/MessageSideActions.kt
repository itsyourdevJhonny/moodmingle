package com.emc.moodmingle.ui.chat.action

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.ui.chat.utils.CopyIcon
import com.emc.moodmingle.ui.chat.utils.ReplyIcon

@Composable
fun MessageSideActions(
    chatMessage: ChatMessage,
    post: PostEntityFirebase?,
    isPostReplyEnabled: Boolean,
    isTextReplyEnabled: Boolean,
    onPostReplyEnabled: (Boolean) -> Unit,
    onTextReplyEnabled: (Boolean) -> Unit,
    onPostMessageReplied: (Boolean, PostEntityFirebase) -> Unit,
    onTextMessageReplied: (Boolean, ChatMessage) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    xOffset: Int
) {
    Column(
        modifier = modifier.absoluteOffset(x = xOffset.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CopyIcon(chatMessage)
        ReplyIcon(
            chatMessage,
            post,
            isPostReplyEnabled,
            isTextReplyEnabled,
            onPostReplyEnabled,
            onTextReplyEnabled,
            onPostMessageReplied,
            onTextMessageReplied,
            iconRes
        )
    }
}