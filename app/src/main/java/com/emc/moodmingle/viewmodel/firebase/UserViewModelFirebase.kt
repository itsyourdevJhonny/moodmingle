package com.emc.moodmingle.viewmodel.firebase

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.firebase.model.UserEntityFirebase
import com.emc.moodmingle.data.firebase.repository.UserRepositoryFirebase
import com.emc.moodmingle.data.model.UserEntity
import com.emc.moodmingle.di.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserViewModelFirebase @Inject constructor(
    private val userRepository: UserRepositoryFirebase,
    private val userDao: UserDao
) : ViewModel() {

    private val _user = mutableStateOf<UserEntityFirebase?>(null)
    val user: State<UserEntityFirebase?> = _user

    init {
        // listen to current user in real time
        viewModelScope.launch {
            userRepository.getLoggedUserByUid().collectLatest {
                _user.value = it
            }
        }
    }

    fun register(
        email: String,
        password: String,
        username: String,
        avatarUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            userRepository.registerUser(email, password, username, avatarUrl)
                .onSuccess {
                    _user.value = it
                    onSuccess()
                }
                .onFailure { e ->
                    onError(e.message ?: "Registration failed")
                }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            userRepository.loginUser(email, password)
                .onSuccess {
                    _user.value = it

                    userDao.insertUser(
                        UserEntity(
                            uid = it.uid,
                            username = it.username,
                            email = it.email,
                            password = it.password,
                            avatarUrl = it.avatarUrl,
                            bio = it.bio,
                            joinedDate = it.joinedDate
                        )
                    )

                    onSuccess()
                }
                .onFailure { e ->
                    onError(e.message ?: "Login failed")
                }
        }
    }

    fun updateUser(userEntity: UserEntityFirebase) {
        viewModelScope.launch {
            userRepository.updateUser(userEntity)
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            _user.value = null
            onComplete()
        }
    }
}