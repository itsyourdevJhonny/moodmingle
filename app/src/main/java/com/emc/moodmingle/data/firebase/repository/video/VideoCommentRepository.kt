package com.emc.moodmingle.data.firebase.repository.video

import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.orEmpty

@Singleton
class VideoCommentRepository @Inject constructor(firestore: FirebaseFirestore) {
    private val collection = firestore.collection("video_comments")

    suspend fun createComment(comment: VideoComment) {
        val docRef = collection.document()
        collection.document(docRef.id).set(comment.copy(id = docRef.id)).await()
    }

    fun getCommentsByVideoUrl(videoUrl: String): Flow<List<VideoComment>> = callbackFlow {
        val listener = collection
            .whereEqualTo("videoUrl", videoUrl)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.toObjects(VideoComment::class.java).orEmpty()
                trySend(comments)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getCommentById(id: String): VideoComment? {
        val doc = collection.document(id).get().await()
        return doc.toObject(VideoComment::class.java)
    }

    fun getCommentCountByVideoUrl(videoUrl: String): Flow<Long> = callbackFlow {
        val listener = collection
            .whereEqualTo("videoUrl", videoUrl)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size()?.toLong() ?: 0L)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateComment(comment: VideoComment) {
        collection.document(comment.id).set(comment).await()
    }

    suspend fun deleteComment(comment: VideoComment) {
        collection.document(comment.id).delete().await()
    }

    fun getCommentByIdAsFlow(commentId: String): Flow<VideoComment?> = callbackFlow {
        val listener = collection.document(commentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(VideoComment::class.java))
            }

        awaitClose { listener.remove() }
    }
}
