package com.emc.moodmingle.domain.remote.repository.saved

import com.emc.moodmingle.domain.remote.model.saved.CollectionEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CollectionRepositoryFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("collection")

    suspend fun insert(collectionEntity: CollectionEntityFirebase) {
        val docRef = collection.document()
        collection.document(docRef.id).set(collectionEntity.copy(id = docRef.id)).await()
    }

    suspend fun getCollectionByUserId(userId: String): List<CollectionEntityFirebase> {
        val snapshot = collection.whereEqualTo("userUid", userId).get().await()
        return snapshot.toObjects(CollectionEntityFirebase::class.java)
    }

    suspend fun getCollectionBySaveIdAndUserUid(saveId: String, userUid: String): CollectionEntityFirebase? {
        val snapshot = collection
            .whereEqualTo("saveId", saveId)
            .whereEqualTo("userUid", userUid)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(CollectionEntityFirebase::class.java)
    }

    suspend fun update(collectionEntity: CollectionEntityFirebase) {
        collection.document(collectionEntity.id).set(collectionEntity).await()
    }

    suspend fun delete(collectionEntity: CollectionEntityFirebase) {
        collection.document(collectionEntity.id).delete().await()
    }
}