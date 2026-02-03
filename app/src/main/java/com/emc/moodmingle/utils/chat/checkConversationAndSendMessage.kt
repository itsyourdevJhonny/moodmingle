package com.emc.moodmingle.utils.chat

import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.viewmodel.chat.ConversationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun checkConversationAndSendMessage(
    senderId: String,
    receiverId: String,
    entityId: String,
    type: String,
    scope: CoroutineScope,
    conversationViewModel: ConversationViewModel,
) {
    conversationViewModel.checkConversationExists(
        user1 = senderId,
        user2 = receiverId
    ) { conversation ->
        scope.launch {
            if (conversation != null) {
                ChatUtils.sendMessage(
                    message = when (type) {
                        "NORMAL_POST" -> "Hey, can we talk about how you’re feeling in your post? I just want to understand what's going on."
                        else -> ""
                    },
                    senderId,
                    receiverId,
                    conversation,
                    conversationViewModel,
                    type,
                    entityId
                )
            } else {
                conversationViewModel.createConversation(
                    Conversation(creatorId = senderId, pairId = "$senderId $receiverId")
                )
            }
        }
    }
}