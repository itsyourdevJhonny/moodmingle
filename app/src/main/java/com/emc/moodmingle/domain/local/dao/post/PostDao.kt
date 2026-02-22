package com.emc.moodmingle.domain.local.dao.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.emc.moodmingle.domain.local.model.post.PostEntity
import com.emc.moodmingle.domain.local.model.post.PostWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Transaction
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllPostsWithUser(): Flow<List<PostWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(postEntity: PostEntity)

    @Query("SELECT * FROM posts")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getPostsByUserId(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Int): PostEntity?

    @Query("SELECT COUNT(*) FROM reactions WHERE postId = :postId")
    suspend fun getReactionCountByPostId(postId: Int): Long

    @Update
    suspend fun updatePost(postEntity: PostEntity)

    @Delete
    suspend fun deletePost(postEntity: PostEntity)
}