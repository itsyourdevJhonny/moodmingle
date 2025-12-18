package com.emc.moodmingle.viewmodel.firebase.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.saved.CollectionEntityFirebase
import com.emc.moodmingle.data.firebase.repository.saved.CollectionRepositoryFirebase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@HiltViewModel
class CollectionViewModelFirebase @Inject constructor(
    private val repository: CollectionRepositoryFirebase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val collection = firestore.collection("collection")

    fun getSavedByPostAndUser(postId: String, userUid: String, callback: (CollectionEntityFirebase?) -> Unit) {
        viewModelScope.launch {
            val collection = repository.getCollectionBySaveIdAndUserUid(postId, userUid)
            callback(collection)
        }
    }

    fun getCollectionByUser(userUid: String): Flow<List<CollectionEntityFirebase>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userUid", userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val collections = snapshot?.documents?.mapNotNull {
                    it.toObject(CollectionEntityFirebase::class.java)?.copy(id = it.id)
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
    suspend fun getCollectionByNameAndUser(name: String, userUid: String): CollectionEntityFirebase? =
        suspendCancellableCoroutine { cont ->
            collection
                .whereEqualTo("name", name)
                .whereEqualTo("userUid", userUid)
                .limit(1) // only fetch the first matching document
                .get()
                .addOnSuccessListener { snapshot ->
                    val collection = snapshot.documents.firstOrNull()?.toObject(CollectionEntityFirebase::class.java)
                        ?.copy(id = snapshot.documents.firstOrNull()?.id ?: "")
                    cont.resume(collection) { cause, _, _ -> }
                }
                .addOnFailureListener {
                    cont.resume(null) { cause, _, _ -> }
                }
        }

    suspend fun getCollectionByUserAndSaved(userUid: String, saveId: String): CollectionEntityFirebase? =
        suspendCancellableCoroutine { cont ->
            collection
                .whereEqualTo("userUid", userUid)
                .whereArrayContains("saveIds", saveId)
                .limit(1) // only fetch the first matching document
                .get()
                .addOnSuccessListener { snapshot ->
                    val collection = snapshot.documents.firstOrNull()?.toObject(CollectionEntityFirebase::class.java)
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
            collection.saveIds.contains(saveId)
        }
    }


    fun insert(collectionEntityFirebase: CollectionEntityFirebase) {
        viewModelScope.launch {
            repository.insert(collectionEntityFirebase)
        }
    }

    fun update(collection: CollectionEntityFirebase) {
        viewModelScope.launch {
            repository.update(collection)
        }
    }

    fun delete(collectionEntityFirebase: CollectionEntityFirebase) {
        viewModelScope.launch {
            repository.delete(collectionEntityFirebase)
        }
    }
}