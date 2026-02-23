package com.emc.moodmingle.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.local.model.share.ShareEntity
import com.emc.moodmingle.domain.local.service.ShareService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(private val shareService: ShareService) : ViewModel() {

    fun insert(shareEntity: ShareEntity) = viewModelScope.launch {
        shareService.insert(shareEntity)
    }

    fun getSharedByUserUid(userUid: String): Flow<List<ShareEntity>> {
        return shareService.getSharedByUserUid(userUid)
    }

    fun getSharedByPostId(postId: Int): Flow<ShareEntity?> {
        return shareService.getSharedByPostId(postId)
    }

    fun getSharedByPostIdAndUserUid(postId: Int, userUid: String): Flow<ShareEntity?> {
        return shareService.getSharedByPostIdAndUserUid(postId, userUid)
    }

    fun getShareCountByPostId(postId: Int): Flow<Long> {
        return shareService.getShareCountByPostId(postId)
    }

    /*fun getShareCountByPostId(postId: Int) = flow {
        emit(shareService.getShareCountByPostId(postId))
    }.flowOn(Dispatchers.IO)*/

    fun update(shareEntity: ShareEntity) = viewModelScope.launch {
        shareService.update(shareEntity)
    }

    fun delete(shareEntity: ShareEntity) = viewModelScope.launch {
        shareService.delete(shareEntity)
    }
}