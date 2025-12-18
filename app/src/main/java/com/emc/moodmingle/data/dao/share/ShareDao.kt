package com.emc.moodmingle.data.dao.share

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.data.model.share.ShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shareEntity: ShareEntity)

    @Query("SELECT * FROM share WHERE userUid = :userUid")
    fun getSharedByUserUid(userUid: String): Flow<List<ShareEntity>>

    @Query("SELECT * FROM share WHERE postId = :postId")
    fun getSharedByPostId(postId: Int): Flow<ShareEntity?>

    @Query("SELECT * FROM share WHERE postId = :postId AND userUid = :userUid")
    fun getSharedByPostIdAndUserUid(postId: Int, userUid: String): Flow<ShareEntity?>

    @Query("SELECT COUNT(*) FROM share WHERE postId = :postId")
    fun getShareCountByPostId(postId: Int): Flow<Long>

    @Update
    suspend fun update(shareEntity: ShareEntity)

    @Delete
    suspend fun delete(shareEntity: ShareEntity)
}