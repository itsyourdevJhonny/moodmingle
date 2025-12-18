package com.emc.moodmingle.data.dao.hide

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.data.model.hide.HideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hideEntity: HideEntity)

    @Query("SELECT * FROM hide WHERE userUid = :userUid")
    fun getHiddenByUserUid(userUid: String): Flow<List<HideEntity>>

    @Query("SELECT * FROM favorites WHERE postId = :postId AND userUid = :userUid")
    fun getHiddenByPostIdAndUserUid(postId: Int, userUid: String): Flow<HideEntity?>

    @Update
    suspend fun update(hideEntity: HideEntity)

    @Delete
    suspend fun delete(hideEntity: HideEntity)
}