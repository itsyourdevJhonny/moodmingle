package com.emc.moodmingle.domain.remote.repository.user

import com.emc.moodmingle.domain.remote.datasource.FirebaseUserDataSource
import com.emc.moodmingle.domain.remote.model.user.UserEntityFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    fun getLoggedUser(): Flow<UserEntityFirebase?> = firebaseSource.getCurrentLoggedUser()

    fun getUserByUid(uid: String) = firebaseSource.getUserByUid(uid)

    fun getUserById(id: String): Flow<UserEntityFirebase?> = callbackFlow {
        if (id.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = FirebaseFirestore.getInstance()
            .collection("users")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(UserEntityFirebase::class.java)
                trySend(user)
            }

        awaitClose { listener.remove() }
    }

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