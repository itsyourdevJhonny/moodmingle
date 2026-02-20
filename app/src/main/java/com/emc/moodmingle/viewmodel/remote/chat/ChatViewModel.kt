package com.emc.moodmingle.viewmodel.remote.chat

import androidx.lifecycle.ViewModel
import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.emc.moodmingle.domain.remote.repository.chat.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {
    fun sendMessage(senderId: String, receiverId: String, message: String, conversationId: String) {
        val msg = ChatMessage(senderId, receiverId, message, conversationId = conversationId)
        chatRepository.sendMessage(msg) {}
    }
}
