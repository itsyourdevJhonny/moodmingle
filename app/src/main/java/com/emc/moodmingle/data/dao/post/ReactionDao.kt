package com.emc.moodmingle.data.dao.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.data.model.post.ReactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reactionEntity: ReactionEntity)

    @Query("SELECT * FROM reactions WHERE postId = :postId")
    suspend fun getReactionsByPostId(postId: Int): List<ReactionEntity>

    @Query("SELECT COUNT(*) FROM reactions WHERE postId = :postId")
    fun getReactionsCountByPostId(postId: Int): Flow<Long>

    @Query("SELECT * FROM reactions WHERE id = :id")
    suspend fun getReactionById(id: Int): ReactionEntity

    @Query("SELECT * FROM reactions WHERE reactorId = :reactorId AND postId = :postId")
    suspend fun getReactionByReactorIdAndPostId(reactorId: String, postId: Int): ReactionEntity?

    @Update
    suspend fun updateReaction(reactionEntity: ReactionEntity)

    @Delete
    suspend fun deleteReaction(reactionEntity: ReactionEntity)
}