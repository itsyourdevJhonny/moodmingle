package com.emc.moodmingle.data.dao.search

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.data.model.search.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchEntity: SearchEntity)

    @Query("SELECT * FROM search WHERE userUid = :userUid ORDER BY time DESC")
    fun getSearchesByUserUid(userUid: String): Flow<List<SearchEntity>>

    @Query("""
        SELECT s.* FROM search AS s
        INNER JOIN user AS u ON s.userUid = u.uid
        WHERE LOWER(REPLACE(u.username, ' ', '')) LIKE '%' || LOWER(REPLACE(:usernameQuery, ' ', '')) || '%'
        ORDER BY s.time DESC
    """)
    fun searchByUsername(usernameQuery: String): Flow<List<SearchEntity>>

    @Update
    suspend fun update(searchEntity: SearchEntity)

    @Delete
    suspend fun delete(searchEntity: SearchEntity)
}