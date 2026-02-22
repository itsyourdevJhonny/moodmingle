package com.emc.moodmingle.viewmodel.remote.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.remote.model.favorites.FavoritesCollectionEntity
import com.emc.moodmingle.domain.remote.repository.favorites.FavoritesCollectionRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.collections.forEach

@HiltViewModel
class FavoritesCollectionViewModel @Inject constructor(
    private val repository: FavoritesCollectionRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val collection = firestore.collection("favorites_collection")

    fun getSavedByPostAndUser(favoriteId: String, userUid: String, callback: (FavoritesCollectionEntity?) -> Unit) {
        viewModelScope.launch {
            val collection = repository.getCollectionByFavoriteIdAndUserUid(favoriteId, userUid)
            callback(collection)
        }
    }

    fun getCollectionByUser(userUid: String): Flow<List<FavoritesCollectionEntity>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userUid", userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val collections = snapshot?.documents?.mapNotNull {
                    it.toObject(FavoritesCollectionEntity::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(collections)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Suspends and returns a single collection for a user matching the given name directly from Firestore.
     *
     * @param name the collection name to search for
     * @param userUid the user id owning the collection
     * @return the first matching CollectionEntityFirebase, or null if none found
     */
    suspend fun getCollectionByNameAndUser(name: String, userUid: String): FavoritesCollectionEntity? =
        suspendCancellableCoroutine { cont ->
            collection
                .whereEqualTo("name", name)
                .whereEqualTo("userUid", userUid)
                .limit(1) // only fetch the first matching document
                .get()
                .addOnSuccessListener { snapshot ->
                    val collection = snapshot.documents.firstOrNull()?.toObject(FavoritesCollectionEntity::class.java)
                        ?.copy(id = snapshot.documents.firstOrNull()?.id ?: "")
                    cont.resume(collection) { cause, _, _ -> }
                }
                .addOnFailureListener {
                    cont.resume(null) { cause, _, _ -> }
                }
        }

    suspend fun getCollectionByUserAndFavorite(userUid: String, favoriteId: String): FavoritesCollectionEntity? =
        suspendCancellableCoroutine { cont ->
            collection
                .whereEqualTo("userUid", userUid)
                .whereArrayContains("favoritesIds", favoriteId)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    val collection = snapshot.documents.firstOrNull()?.toObject(FavoritesCollectionEntity::class.java)
                        ?.copy(id = snapshot.documents.firstOrNull()?.id ?: "")
                    cont.resume(collection) { cause, _, _ -> }
                }
                .addOnFailureListener {
                    cont.resume(null) { cause, _, _ -> }
                }
        }


    /**
     * Suspends and checks if a specific save item exists in any collection of a user.
     *
     * @param saveId the id of the save item to check
     * @param userUid the id of the user owning the collections
     * @return true if the save item exists in any collection, false otherwise
     */
    suspend fun isSaveInCollection(saveId: String, userUid: String): Boolean {
        val collections = getCollectionByUser(userUid).first() // get the latest snapshot once
        return collections.any { collection ->
            collection.favoritesIds.contains(saveId)
        }
    }

    fun deleteAll(collections: List<FavoritesCollectionEntity>) {
        viewModelScope.launch {
            if (collections.isEmpty()) return@launch

            val batch = firestore.batch()
            val collectionRef = firestore.collection("favorites_collection")

            collections.forEach { collection ->
                val docRef = collectionRef.document(collection.id)
                batch.delete(docRef)
            }

            batch.commit()
        }
    }

    fun insert(collection: FavoritesCollectionEntity) {
        viewModelScope.launch {
            repository.insert(collection)
        }
    }

    fun update(collection: FavoritesCollectionEntity) {
        viewModelScope.launch {
            repository.update(collection)
        }
    }

    fun delete(collection: FavoritesCollectionEntity) {
        viewModelScope.launch {
            repository.delete(collection)
        }
    }
}