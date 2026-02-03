package com.emc.moodmingle.data.firebase.repository.post.normal

import com.emc.moodmingle.data.firebase.model.post.normal.NormalPostEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NormalPostRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("normal_posts")

    /** listen to realtime updates for all posts */
    fun getAllPosts(): Flow<List<NormalPostEntity>> = callbackFlow {
        val listener = collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NormalPostEntity::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    /** create or update post */
    suspend fun insertPost(post: NormalPostEntity) {
        val postId = post.id.ifEmpty { collection.document().id }
        collection.document(postId).set(post.copy(id = postId)).await()
    }

    fun getPostById(id: String): Flow<NormalPostEntity?> = callbackFlow {
        if (id.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = collection
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val post = snapshot?.toObject(NormalPostEntity::class.java)?.copy(id = snapshot.id)
                trySend(post)
            }

        awaitClose { listener.remove() }
    }

    fun getPostByVideoUrl(videoUrl: String): Flow<NormalPostEntity?> = callbackFlow {
        val listener = collection
            .whereArrayContains("urls", videoUrl)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.firstOrNull()?.toObject(NormalPostEntity::class.java))
            }
        awaitClose { listener.remove() }
    }

    fun getPostsByUserId(userId: String): Flow<List<NormalPostEntity>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NormalPostEntity::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updatePost(post: NormalPostEntity) {
        collection.document(post.id).set(post).await()
    }

    suspend fun deletePost(post: NormalPostEntity) {
        collection.document(post.id).delete().await()
    }
}
