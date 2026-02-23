package com.emc.moodmingle.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.local.model.save.SaveEntity
import com.emc.moodmingle.domain.local.service.SaveService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject constructor(private val saveService: SaveService) : ViewModel() {

    fun insert(saveEntity: SaveEntity) = viewModelScope.launch {
        saveService.insert(saveEntity)
    }

    fun getSavedByUserUid(userUid: String) = flow {
        emit(saveService.getSavedByUserUid(userUid))
    }.flowOn(Dispatchers.IO)

    fun getSavedByPostIdAndUserUid(postId: Int, userUid: String) = flow {
        emit(saveService.getSavedByPostIdAndUserUid(postId, userUid))
    }.flowOn(Dispatchers.IO)

    fun update(saveEntity: SaveEntity) = viewModelScope.launch {
        saveService.update(saveEntity)
    }

    fun delete(saveEntity: SaveEntity) = viewModelScope.launch {
        saveService.delete(saveEntity)
    }
}