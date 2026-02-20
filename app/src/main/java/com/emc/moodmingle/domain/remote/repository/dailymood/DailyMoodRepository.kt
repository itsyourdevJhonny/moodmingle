package com.emc.moodmingle.domain.remote.repository.dailymood

import android.util.Log
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.computeExpiresAt
import com.emc.moodmingle.domain.remote.model.post.dailymood.isActiveAndNotExpired
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.ifEmpty

@Singleton
class DailyMoodRepository @Inject constructor(firestore: FirebaseFirestore) {
    private val collection = firestore.collection("daily_moods")

    /**
     * Create a new daily mood with precomputed expiration.
     */
    suspend fun createDailyMood(dailyMood: DailyMoodEntity) {
        val id = dailyMood.id.ifEmpty { collection.document().id }
        val expiresAt = dailyMood.settings.computeExpiresAt(dailyMood.createdAt)
        collection.document(id).set(dailyMood.copy(id = id, expiresAt = expiresAt)).await()

        Log.d("DailyMoodService", getDailyMoodById(id).getOrNull().toString())
    }

    /**
     * Observe all active and non-expired daily moods in real-time.
     *
     * This listens to the entire collection ordered by creation date,
     * then filters only moods that:
     * - Are already posted (based on timing)
     * - Are not expired (based on duration)
     */
    /**
     * Observe all active and non-expired daily moods.
     * Server-side filtered.
     */
    fun getAllActiveDailyMoods(): Flow<List<DailyMoodEntity>> =
        callbackFlow {
            val now = System.currentTimeMillis()

            val listener = collection
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", now)
                .orderBy("expiresAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val moods = snapshot?.documents
                        ?.mapNotNull {
                            it.toObject(DailyMoodEntity::class.java)
                        } ?: emptyList()

                    trySend(moods)
                }

            awaitClose { listener.remove() }
        }

    /**
     * Get a single daily mood by document id.
     */
    suspend fun getDailyMoodById(id: String): Result<DailyMoodEntity?> {
        return try {
            val snapshot = collection.document(id).get().await()
            Result.success(snapshot.toObject(DailyMoodEntity::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Listen to all daily moods of a specific user.
     * Emits real-time updates ordered by creation date descending.
     */
    fun getDailyMoodsByUserId(userId: String): Flow<List<DailyMoodEntity>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val moods = snapshot?.documents
                    ?.mapNotNull { it.toObject(DailyMoodEntity::class.java) }
                    ?: emptyList()

                trySend(moods)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Observe only active and non-expired daily moods by user id.
     */
    fun getActiveDailyMoodsByUserId(
        userId: String
    ): Flow<List<DailyMoodEntity>> {

        return getDailyMoodsByUserId(userId)
            .map { moods ->
                moods.filter { it.isActiveAndNotExpired() }
            }
    }

    /**
     * Update an existing daily mood.
     */
    suspend fun updateDailyMood(dailyMood: DailyMoodEntity): Result<Unit> {
        return try {
            collection.document(dailyMood.id)
                .set(dailyMood.copy(updatedAt = System.currentTimeMillis()))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a daily mood by id.
     */
    suspend fun deleteDailyMood(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}