package com.emc.moodmingle.data.firebase.repository

import com.emc.moodmingle.data.firebase.datasource.FirebaseUserDataSource
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.remote.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryFirebase @Inject constructor(
    private val firebaseSource: FirebaseUserDataSource
) {

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        avatarUrl: String
    ): Result<UserEntityFirebase> = firebaseSource.registerUser(email, password, username, avatarUrl)

    suspend fun loginUser(email: String, password: String): Result<UserEntityFirebase> =
        firebaseSource.loginUser(email, password)

    fun getLoggedUserByUid(): Flow<UserEntityFirebase?> = firebaseSource.getCurrentLoggedUser()

    fun getUserByUid(uid: String) = firebaseSource.getUserByUid(uid)
    suspend fun getUserByUidOnce(uid: String) = firebaseSource.getUserByUidOnce(uid)

    fun getAllUsers(): Flow<List<UserEntityFirebase>> = firebaseSource.getAllUsers()

    fun searchByUsername(username: String): Flow<List<UserEntityFirebase>> =
        firebaseSource.searchUsersByUsername(username)

    suspend fun updateUser(user: UserEntityFirebase) = firebaseSource.updateUser(user)

    suspend fun logout() = firebaseSource.logout()

    suspend fun verifyCurrentPassword(currentPassword: String) = firebaseSource.verifyCurrentPassword(currentPassword)

    suspend fun updatePassword(newPassword: String) = firebaseSource.updatePassword(newPassword)

    suspend fun sendVerificationCode(email: String) = firebaseSource.sendVerificationCode(email)

    suspend fun verifyCode(email: String, code: String) = firebaseSource.verifyCode(email, code)

    suspend fun resetPassword(newPassword: String) = firebaseSource.resetPassword(newPassword)
}
