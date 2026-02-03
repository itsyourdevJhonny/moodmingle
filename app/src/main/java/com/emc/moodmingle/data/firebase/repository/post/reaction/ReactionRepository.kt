package com.emc.moodmingle.data.firebase.repository.post.reaction

import com.emc.moodmingle.data.firebase.model.post.reaction.ReactionEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository handling CRUD operations for reactions in Firebase Firestore.
 * preserves original method naming and uses Flow for realtime updates.
 */
class ReactionRepository(private val firestore: FirebaseFirestore) {

    private val postsCollection = firestore.collection("posts")
    private val reactionsCollection = firestore.collection("reactions")

    private fun docId(postId: String, reactorId: String) = "${postId}_$reactorId"

    suspend fun getReactionByUserAndPost(
        reactorId: String,
        postId: String
    ): ReactionEntityFirebase? {
        val doc = firestore.collection("reactions")
            .document("$postId-$reactorId") // assuming documentId is postId-reactorId
            .get()
            .await()

        return if (doc.exists()) {
            doc.toObject(ReactionEntityFirebase::class.java)
        } else {
            null
        }
    }

    fun getReactionsByUser(userId: String): Flow<List<ReactionEntityFirebase>> = callbackFlow {
        // listen for real-time updates from Firestore
        val listener = reactionsCollection
            .whereEqualTo("reactorId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // map Firestore documents to your entity model
                val reactions =
                    snapshot?.toObjects(ReactionEntityFirebase::class.java) ?: emptyList()
                trySend(reactions)
            }

        // remove listener when the flow collection is cancelled
        awaitClose { listener.remove() }
    }

    /**
     * insert a reaction or update if id exists
     */
    /*suspend fun insertReaction(reactionEntity: ReactionEntityFirebase) {
        val docRef = postsCollection
            .document(reactionEntity.postId)
            .collection("reactions")
            .document(reactionEntity.id.ifEmpty { firestore.collection("tmp").document().id })

        docRef.set(reactionEntity).await() // save to firestore
    }*/
    suspend fun insertReaction(reaction: ReactionEntityFirebase) {
        val id = docId(reaction.postId, reaction.reactorId)
        postsCollection
            .document(reaction.postId)
            .collection("reactions")
            .document(id)
            .set(reaction.copy(id = id))
            .await()
    }

    /**
     * get all reactions for a post as Flow with realtime updates
     */
    fun getReactionsByPostId(postId: String): Flow<List<ReactionEntityFirebase>> = callbackFlow {
        val listener = postsCollection
            .document(postId)
            .collection("reactions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reactions =
                    snapshot?.toObjects(ReactionEntityFirebase::class.java) ?: emptyList()
                trySend(reactions)
            }
        awaitClose { listener.remove() }
    }

    fun hasUserReacted(postId: String, reactorId: String): Flow<Boolean> = callbackFlow {
        val docId = "${postId}_$reactorId"
        val listener = postsCollection
            .document(postId)
            .collection("reactions")
            .document(docId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.exists() ?: false)
            }
        awaitClose { listener.remove() }
    }

    /**
     * get reaction count for a post as Flow with realtime updates
     */
    fun getReactionsCountByPostId(postId: String): Flow<Long> = callbackFlow {
        val listener = postsCollection
            .document(postId)
            .collection("reactions")
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
     * get reaction by id as Flow
     */
    fun getReactionById(id: String, postId: String): Flow<ReactionEntityFirebase?> = callbackFlow {
        val listener = postsCollection
            .document(postId)
            .collection("reactions")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reaction = snapshot?.toObject(ReactionEntityFirebase::class.java)
                trySend(reaction)
            }
        awaitClose { listener.remove() }
    }

    /**
     * get reaction by reactorId and postId as Flow
     *
     */
    fun getReactionByReactorIdAndPostId(
        reactorId: String,
        postId: String
    ): Flow<ReactionEntityFirebase?> = callbackFlow {
        val listener = postsCollection
            .document(postId)
            .collection("reactions")
            .document(docId(postId, reactorId))
            .addSnapshotListener { snapshot, error ->
                println("SNAPSHOT SIZE = ${snapshot?.data?.size}")
                snapshot?.data?.forEach {
                    println("DOCUMENT: ${it.value}")
                    println("SNAPSHOT ID" + snapshot.id)
                    println("CURRENT DOC ID: ${docId(postId, reactorId)}")
                    println("RAW MAP = ${snapshot.data}")
                }

                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                trySend(snapshot?.toObject(ReactionEntityFirebase::class.java))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getReactionByReactorIdAndPostIdOnce(
        reactorId: String,
        postId: String
    ): ReactionEntityFirebase? {
        return try {
            val docRef = postsCollection
                .document(postId)
                .collection("reactions")
                .document(docId(postId, reactorId))

            val snapshot = docRef.get().await() // fetch once
            snapshot.toObject(ReactionEntityFirebase::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /*fun getReactionByReactorIdAndPostId(reactorId: String, postId: String): Flow<ReactionEntityFirebase?> = callbackFlow {
        val listener = postsCollection
            .document(postId.toString())
            .collection("reactions")
            .whereEqualTo("reactorId", reactorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val reaction = snapshot?.documents?.firstOrNull()?.toObject(ReactionEntityFirebase::class.java)
                trySend(reaction)
            }
        awaitClose { listener.remove() }
    }*/

    /**
     * update a reaction document
     */
    suspend fun updateReaction(reaction: ReactionEntityFirebase) {
        val id = docId(reaction.postId, reaction.reactorId)
        postsCollection
            .document(reaction.postId)
            .collection("reactions")
            .document(id)
            .set(reaction.copy(id = id))
            .await()
    }

    /**
     * delete a reaction document
     */
    suspend fun deleteReaction(reaction: ReactionEntityFirebase) {
        val id = docId(reaction.postId, reaction.reactorId)
        postsCollection
            .document(reaction.postId)
            .collection("reactions")
            .document(id)
            .delete()
            .await()
    }
}