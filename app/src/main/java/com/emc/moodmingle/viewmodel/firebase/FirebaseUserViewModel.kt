package com.emc.moodmingle.viewmodel.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.cloudinary.CloudinaryService
import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.firebase.model.user.UserEntityFirebase
import com.emc.moodmingle.data.firebase.repository.user.UserRepositoryFirebase
import com.emc.moodmingle.data.model.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FirebaseUserViewModel @Inject constructor(
    private val userRepositoryFirebase: UserRepositoryFirebase,
    private val userDao: UserDao
) : ViewModel() {
    private val userCache = mutableMapOf<String, UserEntityFirebase?>()
    private val _user = mutableStateOf<UserEntityFirebase?>(null)
    val loggedUser: State<UserEntityFirebase?> = _user

    private val _pagedUsers = MutableStateFlow<List<UserEntityFirebase>>(emptyList())
    val pagedUsers: StateFlow<List<UserEntityFirebase>> = _pagedUsers

    private var lastUserSnapshot: DocumentSnapshot? = null
    private var isLoading = false
    private var hasMoreUsers = true

    init {
        viewModelScope.launch {
            userRepositoryFirebase.getLoggedUser().collectLatest {
                _user.value = it
            }
        }
    }

    fun loadNextUsersPage(limit: Long = 20) {
        if (isLoading || !hasMoreUsers) return
        isLoading = true

        viewModelScope.launch {
            try {
                var query = FirebaseFirestore.getInstance()
                    .collection("users")
                    .orderBy("joinedDate")
                    .limit(limit)

                lastUserSnapshot?.let { snapshot ->
                    query = query.startAfter(snapshot)
                }

                val result = query.get().await()

                val newUsers = result.toObjects(UserEntityFirebase::class.java)

                if (newUsers.isEmpty() || newUsers.size < limit) {
                    hasMoreUsers = false
                }

                lastUserSnapshot = result.documents.lastOrNull()

                _pagedUsers.value += newUsers
            } finally {
                isLoading = false
            }
        }
    }

    fun getLoggedUser() = userRepositoryFirebase.getLoggedUser()

    suspend fun getUserCached(uid: String): UserEntityFirebase? {
        if (userCache.containsKey(uid)) {
            return userCache[uid]
        }

        val result = getUserByUidOnce(uid).getOrNull()
        userCache[uid] = result
        return result
    }

    suspend fun updateUser(userEntity: UserEntityFirebase) {
        return userRepositoryFirebase.updateUser(userEntity)
    }

    fun getAllUsers() = userRepositoryFirebase.getAllUsers()

    fun getUserByUid(uid: String) = userRepositoryFirebase.getUserByUid(uid)

    fun getUserById(id: String) = userRepositoryFirebase.getUserById(id)

    suspend fun getUserByUidOnce(uid: String) = userRepositoryFirebase.getUserByUidOnce(uid)

    private val _isUploaded = MutableStateFlow(false)
    val isUploaded: StateFlow<Boolean> = _isUploaded

    private val _isProfileUpdated = MutableStateFlow(false)
    val isProfileUpdated: StateFlow<Boolean> = _isProfileUpdated

    fun updateAvatar(
        context: Context,
        avatarUrl: String,
        uri: Uri,
        userEntityFirebase: Result<UserEntityFirebase>?,
        loggedUser: UserEntity?
    ) {
        _isUploaded.value = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val publicId = CloudinaryService.getPublicIdFromUrl(avatarUrl)
                val newAvatarUrl = CloudinaryService.updateFile(context, publicId, uri)

                userEntityFirebase?.let { user ->
                    val updatedUser = user.getOrNull()?.copy(
                        avatarUrl = newAvatarUrl ?: user.getOrNull()?.avatarUrl ?: ""
                    )
                    updateUser(updatedUser!!)

                    loggedUser?.let {
                        userDao.updateUser(it.copy(avatarUrl = newAvatarUrl!!))
                    }
                }

                withContext(Dispatchers.Main) {
                    _isUploaded.value = false
                    _isProfileUpdated.value = true

                    Toast.makeText(
                        context,
                        "Avatar updated successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isUploaded.value = false
                    Toast.makeText(
                        context,
                        "Failed to update avatar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun searchByUsername(username: String): Flow<List<UserEntityFirebase>> =
        userRepositoryFirebase.searchByUsername(username)

    suspend fun verifyCurrentPassword(currentPassword: String): Result<Boolean> {
        return userRepositoryFirebase.verifyCurrentPassword(currentPassword)
    }

    suspend fun updatePassword(newPassword: String): Result<Boolean> {
        return userRepositoryFirebase.updatePassword(newPassword)
    }

    suspend fun sendVerificationCode(email: String): Result<Boolean> {
        return userRepositoryFirebase.sendVerificationCode(email)
    }

    suspend fun verifyCode(email: String, code: String): Result<Boolean> {
        return userRepositoryFirebase.verifyCode(email, code)
    }

    suspend fun resetPassword(newPassword: String): Result<Unit> {
        return userRepositoryFirebase.resetPassword(newPassword)
    }

    fun saveFcmToken(userId: String, token: String) {
        val userRef = Firebase.firestore.collection("users").document(userId)
        userRef.update("fcmToken", token)
            .addOnSuccessListener { Log.d("FCM", "Token saved") }
            .addOnFailureListener { Log.e("FCM", it.message ?: "") }
    }
}