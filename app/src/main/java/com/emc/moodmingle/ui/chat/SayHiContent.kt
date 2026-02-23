package com.emc.moodmingle.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.utils.modifier.drawGradient
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel
import kotlinx.coroutines.launch

@Composable
fun SayHiContent(
    senderId: String,
    receiverId: String,
    conversation: Conversation,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = hiltViewModel<ConversationViewModel>()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Say Hi!",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(R.drawable.message_colored),
            contentDescription = "Message",
            modifier = Modifier.size(100.dp)
        )
        TextButton(
            onClick = {
                scope.launch {
                    val chatMessage = ChatMessage(
                        senderId = senderId,
                        receiverId = receiverId,
                        message = "Hi!",
                        conversationId = conversation.id
                    )

                    conversationViewModel.updateConversation(
                        conversation = conversation.copy(
                            lastMessage = "Hi!",
                            lastMessageTime = System.currentTimeMillis(),
                            messages = conversation.messages + chatMessage
                        )
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.send),
                contentDescription = "Send",
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawGradient()
            )
        }
    }
}