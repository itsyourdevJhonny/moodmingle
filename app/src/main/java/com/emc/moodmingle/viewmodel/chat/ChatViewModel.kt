package com.emc.moodmingle.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.emc.moodmingle.data.firebase.model.chat.ChatMessage
import com.emc.moodmingle.data.firebase.repository.chat.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val collection = firestore.collection("messages")

    var messages by mutableStateOf<List<ChatMessage>>(emptyList())
        private set

    fun loadMessages(senderId: String, receiverId: String) {
        chatRepository.getMessages(senderId, receiverId) {
            messages = it
        }
    }

    fun loadMessagesByConversationId(conversationId: String): Flow<List<ChatMessage>> =
        callbackFlow {
            val listener = collection
                .whereEqualTo("conversationId", conversationId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val messages = snapshot?.toObjects(ChatMessage::class.java)
                        ?.sortedByDescending { it.timestamp } ?: emptyList()
                    trySend(messages)
                }
            awaitClose { listener.remove() }
        }

    fun sendMessage(senderId: String, receiverId: String, message: String, conversationId: String) {
        val msg = ChatMessage(senderId, receiverId, message, conversationId = conversationId)
        chatRepository.sendMessage(msg) {}
    }
}
