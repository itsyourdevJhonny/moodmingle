package com.emc.moodmingle.data.firebase.repository.post

import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.ShareEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository handling CRUD operations for shares in Firebase Firestore.
 * preserves original method naming from the Room DAO
 */
class ShareRepositoryFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val sharesCollection = firestore.collection("shares")

    /**
     * insert or update a share
     */
    suspend fun insert(shareEntity: ShareEntityFirebase) {
        val docRef = sharesCollection
            .document(shareEntity.id.ifEmpty { firestore.collection("tmp").document().id })
        docRef.set(shareEntity).await()
    }

    fun getAllShares(): Flow<List<ShareEntityFirebase>> = callbackFlow {
        val listener = sharesCollection
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ShareEntityFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    /**
     * get shares by userUid as flow (realtime)
     */
    fun getSharedByUserUid(userUid: String): Flow<List<ShareEntityFirebase>> = callbackFlow {
        val listener = sharesCollection
            .whereEqualTo("userUid", userUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val shares = snapshot?.toObjects(ShareEntityFirebase::class.java) ?: emptyList()
                trySend(shares)
            }
        awaitClose { listener.remove() }
    }

    /**
     * get share by postId as flow (realtime)
     */
    fun getSharedByPostId(postId: String): Flow<ShareEntityFirebase?> = callbackFlow {
        val listener = sharesCollection
            .whereEqualTo("postId", postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(
                    snapshot?.documents?.firstOrNull()?.toObject(ShareEntityFirebase::class.java)
                )
            }
        awaitClose { listener.remove() }
    }

    suspend fun getSharedByPostIdOnce(postId: String): ShareEntityFirebase? {
        return try {
            val snapshot = sharesCollection
                .whereEqualTo("postId", postId)
                .get()
                .await()

            snapshot.documents
                .firstOrNull()
                ?.toObject(ShareEntityFirebase::class.java)

        } catch (e: Exception) {
            null
        }
    }


    /**
     * get share by postId and userUid as flow (realtime)
     */
    fun getSharedByPostIdAndUserUid(postId: String, userUid: String): Flow<ShareEntityFirebase?> =
        callbackFlow {
            val listener = sharesCollection
                .whereEqualTo("postId", postId)
                .whereEqualTo("userUid", userUid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(
                        snapshot?.documents?.firstOrNull()
                            ?.toObject(ShareEntityFirebase::class.java)
                    )
                }
            awaitClose { listener.remove() }
        }

    /**
     * get real-time share count by postId
     */
    fun getShareCountByPostId(postId: String): Flow<Long> = callbackFlow {
        val listener = sharesCollection
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

    /**
     * update a share
     */
    suspend fun update(shareEntity: ShareEntityFirebase) {
        if (shareEntity.id.isEmpty()) return
        sharesCollection
            .document(shareEntity.id)
            .set(shareEntity)
            .await()
    }

    /**
     * delete a share
     */
    suspend fun delete(shareEntity: ShareEntityFirebase) {
        if (shareEntity.id.isEmpty()) return
        sharesCollection
            .document(shareEntity.id)
            .delete()
            .await()
    }
}