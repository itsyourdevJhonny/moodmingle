package com.emc.moodmingle.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.emc.moodmingle.data.firebase.repository.chat.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation = _conversation.asStateFlow()

    fun getConversation(user1: String, user2: String) {
        viewModelScope.launch {
            conversationRepository.getConversationFlow(user1, user2).collect { conv ->
                _conversation.value = conv
            }
        }
    }
    suspend fun createConversation(conversation: Conversation) =
        conversationRepository.insert(conversation)

    fun getConversationsByUser(userId: String) =
        conversationRepository.getConversationsByUser(userId)

    fun checkConversationExists(user1: String, user2: String, callback: (Conversation?) -> Unit) =
        conversationRepository.checkConversationExists(user1, user2, callback)

    fun getConversationByPairUser(user1: String, user2: String, callback: (Conversation?) -> Unit) =
        conversationRepository.getConversationByPairUser(user1, user2, callback)

    suspend fun updateConversation(conversation: Conversation, senderId: String = "", receiverId: String = "", message: String = "") =
        conversationRepository.update(conversation)

    suspend fun deleteConversation(conversation: Conversation) =
        conversationRepository.delete(conversation)
}