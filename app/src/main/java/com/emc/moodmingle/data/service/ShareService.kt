package com.emc.moodmingle.data.service

import com.emc.moodmingle.data.dao.share.ShareDao
import com.emc.moodmingle.data.model.share.ShareEntity
import javax.inject.Inject

class ShareService @Inject constructor(private val shareDao: ShareDao) {
    suspend fun insert(shareEntity: ShareEntity) = shareDao.insert(shareEntity)
    fun getSharedByUserUid(userUid: String) = shareDao.getSharedByUserUid(userUid)
    fun getSharedByPostId(postId: Int) = shareDao.getSharedByPostId(postId)
    fun getSharedByPostIdAndUserUid(postId: Int, userUid: String) =
        shareDao.getSharedByPostIdAndUserUid(postId, userUid)

    fun getShareCountByPostId(postId: Int) = shareDao.getShareCountByPostId(postId)

    suspend fun update(shareEntity: ShareEntity) = shareDao.update(shareEntity)
    suspend fun delete(shareEntity: ShareEntity) = shareDao.delete(shareEntity)
}