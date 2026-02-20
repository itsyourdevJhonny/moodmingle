package com.emc.moodmingle.domain.remote.repository.chat

import com.emc.moodmingle.domain.remote.model.chat.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ChatRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val messageCollection = firestore.collection("messages")

    fun sendMessage(message: ChatMessage, onResult: (Boolean) -> Unit) {
        messageCollection
            .add(message)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getMessages(senderId: String, receiverId: String, onMessages: (List<ChatMessage>) -> Unit) {
        messageCollection
            .whereIn("senderId", listOf(senderId, receiverId))
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val filtered = snapshot.toObjects(ChatMessage::class.java)
                        .filter {
                            (it.senderId == senderId && it.receiverId == receiverId) ||
                                    (it.senderId == receiverId && it.receiverId == senderId)
                        }
                        .sortedBy { it.timestamp }
                    onMessages(filtered)
                }
            }
    }

    fun getMessagesByConversation(conversationId: Int, onMessages: (List<ChatMessage>) -> Unit) {
        messageCollection
            .whereEqualTo("conversationId", conversationId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val sorted =
                        snapshot.toObjects(ChatMessage::class.java).sortedBy { it.timestamp }
                    onMessages(sorted)
                }
            }
    }
}
