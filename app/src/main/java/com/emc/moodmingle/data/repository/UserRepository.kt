package com.emc.moodmingle.data.repository

import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.data.mapper.UserMapper
import com.emc.moodmingle.data.remote.FirebaseRepository
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseRepo: FirebaseRepository,
    private val userDao: UserDao
) {

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        avatarUrl: String
    ): Result<UserEntity> {
        val result = firebaseRepo.registerUser(email, password, username, avatarUrl)
        return result.mapCatching { firebaseUser ->
            val localUser = UserMapper.mapToLocal(firebaseUser)
            userDao.insertUser(localUser)
            localUser
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        val result = firebaseRepo.loginUser(email, password)
        return result.mapCatching { firebaseUser ->
            val localUser = UserMapper.mapToLocal(firebaseUser)
            userDao.insertUser(localUser)
            localUser
        }
    }

    suspend fun getUserByUid(uid: String) = userDao.getUserByUid(uid)

    fun getLoggedUserByUid() = userDao.getCurrentLoggedUser()

    suspend fun getAllUsers() = userDao.getAllUsers()

    suspend fun updateUser(userEntity: UserEntity) = userDao.updateUser(userEntity)

    fun searchByUsername(username: String) = userDao.searchUserByUsername(username)

    suspend fun getLocalUser(): UserEntity? = userDao.getLoggedUser()

    suspend fun logout(uid: String) {
        firebaseRepo.logout()
        userDao.clearUser(uid)
    }
}
