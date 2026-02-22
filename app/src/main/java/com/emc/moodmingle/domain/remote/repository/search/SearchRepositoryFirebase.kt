package com.emc.moodmingle.domain.remote.repository.search

import com.emc.moodmingle.domain.remote.model.search.SearchEntityFirebase
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchRepositoryFirebase @Inject constructor(
    firestore: FirebaseFirestore
) {

    private val usersRef = firestore.collection("users")
    private val searchesRef = firestore.collection("searches")

    suspend fun insert(searchEntity: SearchEntityFirebase) {
        val docRef = searchesRef.document()
        searchesRef.document(docRef.id).set(searchEntity.copy(id = docRef.id)).await()
    }

    suspend fun getSearchBySearcherIdAndUserId(
        searcherId: String,
        userUid: String
    ): SearchEntityFirebase? {
        val snapshot = searchesRef
            .whereEqualTo("searcherId", searcherId)
            .whereEqualTo("userUid", userUid)
            .limit(1)
            .get()
            .await()

        return snapshot.documents
            .firstOrNull()
            ?.toObject(SearchEntityFirebase::class.java)
    }

    fun getSearchesBySearcherId(searcherId: String): Flow<List<SearchEntityFirebase>> =
        callbackFlow {
            val listener = searchesRef
                .whereEqualTo("searcherId", searcherId)
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val results =
                        snapshot?.documents?.mapNotNull { it.toObject(SearchEntityFirebase::class.java) }
                            ?: emptyList()
                    trySend(results)
                }
            awaitClose { listener.remove() }
        }

    fun searchByUsername(usernameQuery: String): Flow<List<SearchEntityFirebase>> = callbackFlow {
        if (usernameQuery.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val queryText = usernameQuery.lowercase()

        usersRef
            .orderBy("username")
            .startAt(queryText)
            .endAt(queryText + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val results = snapshot.documents.mapNotNull { doc ->
                    val user = doc.toObject(UserEntityFirebase::class.java) as UserEntityFirebase
                    user.let {
                        SearchEntityFirebase(
                            userUid = it.uid,
                            username = it.username,
                            time = System.currentTimeMillis()
                        )
                    }
                }
                trySend(results)
            }
            .addOnFailureListener { e ->
                close(e)
            }
        awaitClose { }
    }

    suspend fun delete(searchEntity: SearchEntityFirebase) {
        searchesRef.document(searchEntity.id).delete().await()
    }
}