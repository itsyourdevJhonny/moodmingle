package com.emc.moodmingle.domain.remote.repository.user

import com.emc.moodmingle.domain.remote.model.user.StoryMoodEntity
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
class CurrentModeRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val currentMoodCollection = firestore.collection("story_moods")

    suspend fun createStoryMood(storyMoodEntity: StoryMoodEntity) {
        val docRef = currentMoodCollection.document()
        currentMoodCollection.document(docRef.id).set(storyMoodEntity.copy(id = docRef.id))
            .await()
    }

    fun getStoryMoodsByUserId(userId: String): Flow<List<StoryMoodEntity>> = callbackFlow {
        val listener = currentMoodCollection
            .whereEqualTo("userId", userId)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val storyMoods = snapshot?.toObjects(StoryMoodEntity::class.java).orEmpty()
                trySend(storyMoods)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getUnexpiredStoryMoodByUserId(userId: String): StoryMoodEntity? {
        return try {
            val snapshot = currentMoodCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("expired", false)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.toObject(StoryMoodEntity::class.java)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getStoryMoodById(id: String): StoryMoodEntity? {
        val doc = currentMoodCollection.document(id).get().await()
        return doc.toObject(StoryMoodEntity::class.java)
    }

    suspend fun updateStoryMood(storyMood: StoryMoodEntity) {
        currentMoodCollection.document(storyMood.id).set(storyMood).await()
    }

    suspend fun deleteStoryMood(storyMood: StoryMoodEntity) {
        currentMoodCollection.document(storyMood.id).delete().await()
    }
}
