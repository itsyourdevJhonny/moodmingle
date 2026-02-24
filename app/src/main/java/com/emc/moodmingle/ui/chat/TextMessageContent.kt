package com.emc.moodmingle.ui.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emc.moodmingle.domain.remote.model.chat.ChatMessage

@Composable
fun TextMessageContent(chatMessage: ChatMessage?) {
    Text(text = chatMessage?.message ?: "", color = Color.White, modifier = Modifier.padding(8.dp))
}