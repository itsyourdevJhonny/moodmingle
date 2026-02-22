package com.emc.moodmingle.domain.local.repository.user

import com.emc.moodmingle.domain.local.dao.user.UserDao
import com.emc.moodmingle.domain.remote.auth.FirebaseRepository
import com.emc.moodmingle.domain.local.mapper.UserMapper
import com.emc.moodmingle.domain.local.model.user.UserEntity
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