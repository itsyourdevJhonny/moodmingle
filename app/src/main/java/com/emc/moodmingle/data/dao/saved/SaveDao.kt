package com.emc.moodmingle.data.dao.saved

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.data.model.save.SaveEntity

@Dao
interface SaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(saveEntity: SaveEntity)

    @Query("SELECT * FROM save WHERE userUid = :userUid")
    suspend fun getSavedByUserUid(userUid: String): List<SaveEntity>

    @Query("SELECT * FROM save WHERE postId = :postId AND userUid = :userUid")
    suspend fun getSavedByPostIdAndUserUid(postId: Int, userUid: String): SaveEntity?

    @Update
    suspend fun update(saveEntity: SaveEntity)

    @Delete
    suspend fun delete(saveEntity: SaveEntity)
}