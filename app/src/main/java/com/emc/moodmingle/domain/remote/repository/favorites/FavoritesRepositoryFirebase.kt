package com.emc.moodmingle.domain.remote.repository.favorites

import com.emc.moodmingle.domain.remote.model.favorites.FavoritesEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritesRepositoryFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("favorites")

    suspend fun insert(favoritesEntity: FavoritesEntityFirebase) {
        val docRef = collection.document()
        collection.document(docRef.id).set(favoritesEntity.copy(id = docRef.id)).await()
    }

    suspend fun getFavoritesByUserUid(userUid: String): List<FavoritesEntityFirebase> {
        val snapshot = collection.whereEqualTo("userUid", userUid).get().await()
        return snapshot.toObjects(FavoritesEntityFirebase::class.java)
    }

    suspend fun getFavoritesByPostIdAndUserUid(postId: String, userUid: String): FavoritesEntityFirebase? {
        val snapshot = collection
            .whereEqualTo("postId", postId)
            .whereEqualTo("userUid", userUid)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(FavoritesEntityFirebase::class.java)
    }

    suspend fun update(favoritesEntity: FavoritesEntityFirebase) {
        collection.document(favoritesEntity.id).set(favoritesEntity).await()
    }

    suspend fun delete(favoritesEntity: FavoritesEntityFirebase) {
        collection.document(favoritesEntity.id).delete().await()
    }
}