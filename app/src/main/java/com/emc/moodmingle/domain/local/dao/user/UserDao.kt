package com.emc.moodmingle.domain.local.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emc.moodmingle.domain.local.model.user.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getLoggedUser(): UserEntity?

    @Query("SELECT * FROM user WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserEntity?

    @Query("SELECT * FROM user LIMIT 1")
    fun getCurrentLoggedUser(): Flow<UserEntity>

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<UserEntity>

    @Query(
        """
        SELECT * FROM user
        WHERE LOWER(REPLACE(username, ' ', '')) LIKE '%' || LOWER(REPLACE(:usernameQuery, ' ', '')) || '%'
        ORDER BY joinedDate DESC
    """
    )
    fun searchUserByUsername(usernameQuery: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM user WHERE uid = :uid")
    suspend fun clearUser(uid: String)
}