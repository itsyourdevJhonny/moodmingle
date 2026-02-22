package com.emc.moodmingle.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.domain.local.dao.hide.HideDao
import com.emc.moodmingle.domain.local.model.hide.HideEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HideViewModel @Inject constructor(
    private val hideDao: HideDao
) : ViewModel() {

    fun hidePost(hideEntity: HideEntity) = viewModelScope.launch {
        hideDao.insert(hideEntity)
    }

    fun unHidePost(hideEntity: HideEntity) = viewModelScope.launch {
        hideDao.delete(hideEntity)
    }

    fun getHiddenByUserUid(userUid: String) : Flow<List<HideEntity>> {
        return hideDao.getHiddenByUserUid(userUid)
    }

    fun getHiddenByPostIdAndUserUid(postId: Int, userUid: String): Flow<HideEntity?> {
        return hideDao.getHiddenByPostIdAndUserUid(postId, userUid)
    }
}