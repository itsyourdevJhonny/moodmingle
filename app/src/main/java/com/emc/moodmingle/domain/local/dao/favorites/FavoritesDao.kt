package com.emc.moodmingle.domain.local.dao.favorites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.domain.local.model.favorites.FavoritesEntity

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorites WHERE userUid = :userUid")
    suspend fun getFavoritesByUserUid(userUid: String): List<FavoritesEntity>

    @Query("SELECT * FROM favorites WHERE postId = :postId AND userUid = :userUid")
    suspend fun getFavoritesByPostIdAndUserUid(postId: Int, userUid: String): FavoritesEntity?

    @Update
    suspend fun update(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun delete(favoritesEntity: FavoritesEntity)
}