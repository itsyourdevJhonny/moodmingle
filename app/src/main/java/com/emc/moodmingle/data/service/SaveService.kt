package com.emc.moodmingle.data.service

import com.emc.moodmingle.data.dao.saved.SaveDao
import com.emc.moodmingle.data.model.save.SaveEntity
import javax.inject.Inject

class SaveService @Inject constructor(private val saveDao: SaveDao) {
    suspend fun insert(saveEntity: SaveEntity) = saveDao.insert(saveEntity)
    suspend fun getSavedByUserUid(userUid: String) = saveDao.getSavedByUserUid(userUid)
    suspend fun getSavedByPostIdAndUserUid(postId: Int, userUid: String) =
        saveDao.getSavedByPostIdAndUserUid(postId, userUid)

    suspend fun update(saveEntity: SaveEntity) = saveDao.update(saveEntity)
    suspend fun delete(saveEntity: SaveEntity) = saveDao.delete(saveEntity)
}