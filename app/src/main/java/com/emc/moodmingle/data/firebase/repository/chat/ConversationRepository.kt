package com.emc.moodmingle.data.firebase.repository.chat

import com.emc.moodmingle.data.firebase.model.chat.Conversation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("conversation")

    suspend fun insert(conversation: Conversation) {
        val docRef = collection.document()
        collection.document(docRef.id).set(conversation.copy(id = docRef.id)).await()
    }

    fun getConversationFlow(user1: String, user2: String): Flow<Conversation?> = callbackFlow {
        val pairIds = generatePairIds(user1, user2)

        val listener = FirebaseFirestore.getInstance()
            .collection("conversation")
            .whereIn("pairId", pairIds)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val conversation = snapshot?.documents?.firstOrNull()?.toObject(Conversation::class.java)
                trySend(conversation)
            }

        awaitClose {
            listener.remove()
        }
    }

    fun getConversationsByUser(userId: String): Flow<List<Conversation>> = callbackFlow {
        val possiblePairs = listOf(
            "$userId ",
            " $userId"
        )

        // Use OR-like behavior by combining two queries
        val query1 = collection
            .orderBy("timeAgo", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("pairId", possiblePairs[0])
            .whereLessThan("pairId", possiblePairs[0] + "\uf8ff")  // prefix search

        val query2 = collection
            .orderBy("timeAgo", Query.Direction.DESCENDING)
            .whereGreaterThanOrEqualTo("pairId", possiblePairs[1])
            .whereLessThan("pairId", possiblePairs[1] + "\uf8ff")

        val listener1 = query1.addSnapshotListener { _, _ -> }
        val listener2 = query2.addSnapshotListener { _, _ -> }

        val combinedListener =
            collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val conversations = snapshot?.documents
                    ?.mapNotNull { doc -> doc.toObject(Conversation::class.java)?.copy(id = doc.id) }
                    ?.filter { it.pairId.split(" ").contains(userId) } // final filter
                    ?.sortedByDescending { it.createdTime }
                    ?: emptyList()

                trySend(conversations)
            }

        awaitClose {
            listener1.remove()
            listener2.remove()
            combinedListener.remove()
        }
    }

    fun getConversationByPairUser(user1: String, user2: String, callback: (Conversation?) -> Unit) {
        val pairIds = generatePairIds(user1, user2)

        collection
            .whereIn("pairId", pairIds)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    val conversation = doc.toObject(Conversation::class.java)?.copy(id = doc.id)
                    callback(conversation)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun checkConversationExists(user1: String, user2: String, callback: (Conversation?) -> Unit) {
        val pairIds = generatePairIds(user1, user2)

        collection
            .whereIn("pairId", pairIds)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    val conversation = doc.toObject(Conversation::class.java)?.copy(id = doc.id)
                    callback(conversation)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    suspend fun update(conversation: Conversation) {
        collection.document(conversation.id).set(conversation).await()
    }

    suspend fun delete(conversation: Conversation) {
        collection.document(conversation.id).delete().await()
    }
}

fun generatePairIds(id1: String, id2: String): List<String> {
    return listOf(
        "$id1 $id2",
        "$id2 $id1"
    )
}