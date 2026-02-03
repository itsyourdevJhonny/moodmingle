package com.emc.moodmingle.data.firebase.model.chat

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val conversationId: String = "",
    val entity: String = "",
    val type: String = "TEXT",
    val replyMessage: String = ""
)