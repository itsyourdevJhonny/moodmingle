package com.emc.moodmingle.data.firebase.repository.video

import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
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
class VideoCommentReplyRepository @Inject constructor(firestore: FirebaseFirestore) {
    val collection = firestore.collection("video_comment_replies")

    suspend fun createReply(reply: VideoCommentReply) {
        val docRef = collection.document()
        collection.document(docRef.id).set(reply.copy(id = docRef.id)).await()
    }

    fun getRepliesByCommentId(videoCommentId: String): Flow<List<VideoCommentReply>> = callbackFlow {
        val listener = collection
            .whereEqualTo("videoCommentId", videoCommentId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.toObjects(VideoCommentReply::class.java).orEmpty()
                trySend(comments)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getReplyById(id: String): VideoCommentReply? {
        val doc = collection.document(id).get().await()
        return doc.toObject(VideoCommentReply::class.java)
    }

    fun getReplyCountByCommentId(videoCommentId: String): Flow<Long> = callbackFlow {
        val listener = collection
            .whereEqualTo("videoCommentId", videoCommentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size()?.toLong() ?: 0L)
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateReply(reply: VideoCommentReply) {
        collection.document(reply.id).set(reply).await()
    }

    suspend fun deleteReply(reply: VideoCommentReply) {
        collection.document(reply.id).delete().await()
    }
}
