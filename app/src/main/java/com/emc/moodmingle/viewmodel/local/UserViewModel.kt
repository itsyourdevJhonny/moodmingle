package com.emc.moodmingle.viewmodel.local

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.local.model.user.UserEntity
import com.emc.moodmingle.domain.local.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = mutableStateOf<UserEntity?>(null)
    val user: State<UserEntity?> = _user

    init {
        viewModelScope.launch {
            _user.value = userRepository.getLocalUser()
        }
    }

    fun getLoggedUserByUid(): Flow<UserEntity> {
        return userRepository.getLoggedUserByUid()
    }

    fun getUserByUid(uid: String) = flow {
        emit(userRepository.getUserByUid(uid))
    }.flowOn(Dispatchers.IO)

    fun getAllUsers() = flow {
        emit(userRepository.getAllUsers())
    }.flowOn(Dispatchers.IO)

    fun searchByUsername(username: String): Flow<List<UserEntity>> {
        return userRepository.searchByUsername(username)
    }

    fun updateUser(userEntity: UserEntity) {
        viewModelScope.launch {
            userRepository.updateUser(userEntity)
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            userRepository.loginUser(email, password)
                .onSuccess {
                    _user.value = it
                    onSuccess()
                }
                .onFailure {
                    onError(it.message ?: "Login failed")
                }
        }
    }

}
