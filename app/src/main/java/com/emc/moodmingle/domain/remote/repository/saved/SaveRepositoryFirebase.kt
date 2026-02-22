package com.emc.moodmingle.domain.remote.repository.saved

import com.emc.moodmingle.domain.remote.model.saved.SaveEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SaveRepositoryFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("save")

    suspend fun insert(saveEntity: SaveEntityFirebase) {
        val docRef = collection.document()
        collection.document(docRef.id).set(saveEntity.copy(id = docRef.id)).await()
    }

    suspend fun getSavedByUserUid(userUid: String): List<SaveEntityFirebase> {
        val snapshot = collection.whereEqualTo("userUid", userUid).get().await()
        return snapshot.toObjects(SaveEntityFirebase::class.java)
    }

    suspend fun getSavedByPostIdAndUserUid(postId: String, userUid: String): SaveEntityFirebase? {
        val snapshot = collection
            .whereEqualTo("postId", postId)
            .whereEqualTo("userUid", userUid)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(SaveEntityFirebase::class.java)
    }

    suspend fun update(saveEntity: SaveEntityFirebase) {
        collection.document(saveEntity.id).set(saveEntity).await()
    }

    suspend fun delete(saveEntity: SaveEntityFirebase) {
        collection.document(saveEntity.id).delete().await()
    }
}