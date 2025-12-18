package com.emc.moodmingle.data.firebase.repository

import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepositoryFirebase @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val postCollection = firestore.collection("posts")

    /** listen to realtime updates for all posts */
    fun getAllPosts(): Flow<List<PostEntityFirebase>> = callbackFlow {
        val listener = postCollection
            .orderBy("timeAgo", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PostEntityFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    /** listen to posts by user */
    /*fun getPostsByUserId(userId: String): Flow<List<PostEntityFirebase>> = callbackFlow {
        val listener = postCollection
            .whereEqualTo("userId", userId)
            .orderBy("timeAgo", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PostEntityFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }*/

    suspend fun getPostsByUserId(userId: String): List<PostEntityFirebase> {
        return try {
            val snapshot = postCollection
                .whereEqualTo("userId", userId)
                .orderBy("timeAgo", Query.Direction.DESCENDING)
                .get()
                .await() // this fetches the data once

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(PostEntityFirebase::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList() // or handle error as needed
        }
    }

    /** create or update post */
    suspend fun insertPost(post: PostEntityFirebase) {
        val postId = post.id.ifEmpty { postCollection.document().id }
        postCollection.document(postId).set(post.copy(id = postId)).await()
    }

    /** get single post by id */
    /*suspend fun getPostById(id: String): PostEntityFirebase? {
        val snapshot = postCollection.document(id).get().await()
        return snapshot.toObject(PostEntityFirebase::class.java)?.copy(id = snapshot.id)
    }*/

    suspend fun getPostByIdOnce(id: String): PostEntityFirebase? {
        if (id.isBlank()) return null

        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("posts")
                .document(id)
                .get()
                .await()

            snapshot.toObject(PostEntityFirebase::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }

    fun getPostById(id: String): Flow<PostEntityFirebase?> = callbackFlow {
        if (id.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = FirebaseFirestore.getInstance()
            .collection("posts")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val post = snapshot?.toObject(PostEntityFirebase::class.java)?.copy(id = snapshot.id)
                trySend(post)
            }

        awaitClose { listener.remove() }
    }

    fun getFilteredPostsByMood(mood: String): Flow<List<PostEntityFirebase>> = callbackFlow {
        if (mood.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("mood", mood)
            .orderBy("timeAgo", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PostEntityFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }


    /** update post fields */
    suspend fun updatePost(post: PostEntityFirebase) {
        postCollection.document(post.id).set(post).await()
    }

    /** delete post */
    suspend fun deletePost(post: PostEntityFirebase) {
        postCollection.document(post.id).delete().await()
    }
}
