package com.emc.moodmingle.data.firebase.repository.favorites

import com.emc.moodmingle.data.firebase.model.favorites.FavoritesCollectionEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoritesCollectionRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("favorites_collection")

    suspend fun insert(collectionEntity: FavoritesCollectionEntity) {
        val docRef = collection.document()
        collection.document(docRef.id).set(collectionEntity.copy(id = docRef.id)).await()
    }

    suspend fun getCollectionByUserUid(userUid: String): List<FavoritesCollectionEntity> {
        val snapshot = collection.whereEqualTo("userUid", userUid).get().await()
        return snapshot.toObjects(FavoritesCollectionEntity::class.java)
    }

    suspend fun getCollectionByFavoriteIdAndUserUid(favoriteId: String, userUid: String): FavoritesCollectionEntity? {
        val snapshot = collection
            .whereEqualTo("saveId", favoriteId)
            .whereEqualTo("userUid", userUid)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.toObject(FavoritesCollectionEntity::class.java)
    }

    suspend fun update(collectionEntity: FavoritesCollectionEntity) {
        collection.document(collectionEntity.id).set(collectionEntity).await()
    }

    suspend fun delete(collectionEntity: FavoritesCollectionEntity) {
        collection.document(collectionEntity.id).delete().await()
    }
}