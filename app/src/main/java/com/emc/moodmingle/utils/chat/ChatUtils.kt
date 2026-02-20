package com.emc.moodmingle.utils.chat

import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.emc.moodmingle.domain.remote.model.chat.Conversation
import com.emc.moodmingle.viewmodel.remote.chat.ConversationViewModel

object ChatUtils {
    suspend fun sendMessage(
        message: String,
        senderId: String,
        receiverId: String,
        conversation: Conversation?,
        conversationViewModel: ConversationViewModel,
        type: String = "TEXT",
        entityId: String = "",
        replyMessage: String = ""
    ) {
        conversation?.let { conversation ->
            val chatMessage = ChatMessage(
                senderId = senderId,
                receiverId = receiverId,
                message = message,
                conversationId = conversation.id,
                type = type,
                entity = entityId,
                replyMessage = replyMessage
            )

            conversationViewModel.updateConversation(
                conversation = conversation.copy(
                    lastMessage = message,
                    lastMessageTime = System.currentTimeMillis(),
                    lastMessageUserId = senderId,
                    messages = conversation.messages + chatMessage
                )
            )
        }
    }
}