package com.emc.moodmingle.domain.remote.repository.remix

import com.emc.moodmingle.domain.remote.model.post.remix.RemixEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemixRepository @Inject constructor(
    firestore: FirebaseFirestore
) {

    private val remixCollection = firestore.collection("remixes")

    suspend fun insert(remixEntity: RemixEntity) {
        val docRef = remixCollection.document()
        remixCollection.document(docRef.id).set(remixEntity.copy(id = docRef.id)).await()
    }

    fun getAllRemixes(): Flow<List<RemixEntity>> = callbackFlow {
        val listener = remixCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val remixes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(RemixEntity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(remixes)
            }
        awaitClose { listener.remove() }
    }

    fun getRemixById(id: String): Flow<RemixEntity?> = callbackFlow {
        if (id.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = remixCollection
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val remix = snapshot?.toObject(RemixEntity::class.java)?.copy(id = snapshot.id)
                trySend(remix)
            }

        awaitClose { listener.remove() }
    }

    fun getRemixedByUserId(userId: String): Flow<List<RemixEntity>> = callbackFlow {
        val listener = remixCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val remixes = snapshot?.toObjects(RemixEntity::class.java) ?: emptyList()
                trySend(remixes)
            }
        awaitClose { listener.remove() }
    }

    fun getRemixCountByUserId(userId: String): Flow<Long> = callbackFlow {
        val listener = remixCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size()?.toLong() ?: 0L)
            }
        awaitClose { listener.remove() }
    }

    suspend fun update(remixEntity: RemixEntity) {
        if (remixEntity.id.isEmpty()) return
        remixCollection
            .document(remixEntity.id)
            .set(remixEntity)
            .await()
    }

    suspend fun delete(remixEntity: RemixEntity) {
        if (remixEntity.id.isEmpty()) return
        remixCollection
            .document(remixEntity.id)
            .delete()
            .await()
    }
}