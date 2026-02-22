package com.emc.moodmingle.domain.remote.repository.post

import com.emc.moodmingle.domain.remote.model.post.normal.HideEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HideRepositoryFirebase @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("hide")

    suspend fun insert(hideEntity: HideEntityFirebase) {
        val docRef = collection.document()
        collection.document(docRef.id).set(hideEntity.copy(id = docRef.id)).await()
    }

    suspend fun getHiddenByUserUid(userUid: String): List<HideEntityFirebase> {
        val snapshot = collection.whereEqualTo("userUid", userUid).get().await()
        return snapshot.toObjects(HideEntityFirebase::class.java)
    }

    suspend fun getHiddenByPostIdAndUserUid(postId: String, userUid: String): HideEntityFirebase? {
        val snapshot = collection
            .whereEqualTo("postId", postId)
            .whereEqualTo("userUid", userUid)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(HideEntityFirebase::class.java)
    }

    suspend fun update(hideEntity: HideEntityFirebase) {
        collection.document(hideEntity.id).set(hideEntity).await()
    }

    suspend fun delete(hideEntity: HideEntityFirebase) {
        collection.document(hideEntity.id).delete().await()
    }
}
