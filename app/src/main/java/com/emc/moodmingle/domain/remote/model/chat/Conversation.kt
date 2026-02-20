package com.emc.moodmingle.domain.remote.model.chat

data class Conversation(
    val id: String = "",
    val creatorId: String = "",
    val pairId: String = "",
    val avatarUrl: String = "",
    val name: String = "",
    val createdTime: Long = System.currentTimeMillis(),
    val lastMessageTime: Long = 0L,
    val lastMessageRead: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val lastMessage: String = "",
    val lastMessageUserId: String = ""
)