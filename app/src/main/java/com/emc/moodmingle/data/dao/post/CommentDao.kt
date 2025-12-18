package com.emc.moodmingle.data.dao.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.data.model.post.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(commentEntity: CommentEntity)

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY time DESC")
    fun getCommentsByPostId(postId: Int): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun getCommentById(id: Int): CommentEntity?

    @Query("SELECT COUNT(*) FROM comments WHERE postId = :postId")
    fun getCommentCountByPostId(postId: Int): Flow<Long>

    @Update
    suspend fun updateComment(commentEntity: CommentEntity)

    @Delete
    suspend fun deleteComment(commentEntity: CommentEntity)
}