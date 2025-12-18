package com.emc.moodmingle.ui.chat.input

import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel

suspend fun sendMessage(
    message: String,
    senderId: String,
    receiverId: String,
    conversation: Conversation?,
    conversationViewModel: ConversationViewModel,
    type: String = "TEXT",
    postId: String = "",
    replyMessage: String = ""
) {
    conversation?.let {
        val chatMessage = ChatMessage(
            senderId = senderId,
            receiverId = receiverId,
            message = message,
            conversationId = it.id,
            type = type,
            postId = postId,
            replyMessage = replyMessage
        )

        conversationViewModel.updateConversation(
            conversation = it.copy(
                lastMessage = message,
                lastMessageTime = System.currentTimeMillis(),
                messages = conversation.messages + chatMessage
            )
        )
    }
}