package com.emc.moodmingle.ui.chat.utils

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.emc.moodmingle.utils.modifier.drawGradient

@Composable
fun ReplyIcon(
    chatMessage: ChatMessage,
    post: PostEntityFirebase?,
    isPostReplyEnabled: Boolean,
    isTextReplyEnabled: Boolean,
    onPostReplyEnabled: (Boolean) -> Unit,
    onTextReplyEnabled: (Boolean) -> Unit,
    onPostMessageReplied: (Boolean, PostEntityFirebase) -> Unit,
    onTextMessageReplied: (Boolean, ChatMessage) -> Unit,
    @DrawableRes iconRes: Int,
) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = "Reply",
        modifier = Modifier
            .size(20.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawGradient()
            .clickable {
                when (chatMessage.type) {
                    "TEXT", "TEXT_REPLIED", "POST_REPLIED", "EDITED" -> {
                        if (isPostReplyEnabled) onPostReplyEnabled(false)
                        onTextMessageReplied(true, chatMessage)
                    }

                    "POST" -> {
                        post?.let {
                            if (isTextReplyEnabled) onTextReplyEnabled(false)
                            onPostMessageReplied(true, it)
                        }
                    }
                }
            }
    )
}