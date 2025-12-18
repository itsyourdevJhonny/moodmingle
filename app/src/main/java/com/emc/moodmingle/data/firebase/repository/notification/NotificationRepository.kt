package com.emc.moodmingle.data.firebase.repository.notification

import com.emc.moodmingle.data.firebase.model.notification.NotificationEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.forEach

class NotificationRepository @Inject constructor(
    val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("notifications")

    suspend fun insert(notificationEntity: NotificationEntity) {
        val docRef = collection.document()
        collection.document(docRef.id).set(notificationEntity.copy(id = docRef.id)).await()
    }

    fun getNotificationsByUserId(userId: String): Flow<List<NotificationEntity?>> = callbackFlow {
        val listener = FirebaseFirestore.getInstance()
            .collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val notifications =
                    snapshot?.toObjects(NotificationEntity::class.java) ?: emptyList()
                trySend(notifications)
            }

        awaitClose {
            listener.remove()
        }
    }

    fun getUnreadNotificationsByUserId(userId: String): Flow<List<NotificationEntity?>> =
        callbackFlow {
            val listener = FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("read", false)
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val notifications =
                        snapshot?.toObjects(NotificationEntity::class.java) ?: emptyList()
                    trySend(notifications)
                }

            awaitClose {
                listener.remove()
            }
        }

    suspend fun getNotificationPostId(postId: String): NotificationEntity? {
        if (postId.isBlank()) return null

        return try {
            val snapshot = collection
                .whereEqualTo("postId", postId)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(NotificationEntity::class.java)
        } catch (_: Exception) {
            null
        }
    }

    fun markNotificationsAsRead(unreadNotifications: List<NotificationEntity?>) {
        if (unreadNotifications.isEmpty()) return

        val batch = firestore.batch()

        unreadNotifications.forEach { notification ->
            notification?.let {
                val docRef = collection.document(it.id)
                batch.update(docRef, "read", false)
            }
        }

        batch.commit().addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    suspend fun update(notificationEntity: NotificationEntity) {
        collection.document(notificationEntity.id).set(notificationEntity).await()
    }

    suspend fun delete(notificationEntity: NotificationEntity) {
        collection.document(notificationEntity.id).delete().await()
    }
}