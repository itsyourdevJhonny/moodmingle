package com.emc.moodmingle.data.firebase.repository.post

import com.emc.moodmingle.data.firebase.model.post.CommentEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all Firestore operations related to comments.
 */
@Singleton
class CommentRepositoryFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val commentsCollection = firestore.collection("comments")

    /** create a new comment document */
    suspend fun createComment(comment: CommentEntityFirebase) {
        commentsCollection.add(comment).await()
    }

    /** get all comments for a specific post as a realtime flow */
    fun getCommentsByPostId(postId: String): Flow<List<CommentEntityFirebase>> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("postId", postId)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.toObjects(CommentEntityFirebase::class.java).orEmpty()
                trySend(comments)
            }

        awaitClose { listener.remove() }
    }

    /** get a single comment by document id */
    suspend fun getCommentById(id: String): CommentEntityFirebase? {
        val doc = commentsCollection.document(id).get().await()
        return doc.toObject(CommentEntityFirebase::class.java)
    }

    /** count the number of comments for a post */
    fun getCommentCountByPostId(postId: String): Flow<Long> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("postId", postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size()?.toLong() ?: 0L)
            }

        awaitClose { listener.remove() }
    }

    /** update an existing comment */
    suspend fun updateComment(comment: CommentEntityFirebase) {
        commentsCollection.document(comment.id).set(comment).await()
    }

    /** delete a comment */
    suspend fun deleteComment(comment: CommentEntityFirebase) {
        commentsCollection.document(comment.id).delete().await()
    }
}
