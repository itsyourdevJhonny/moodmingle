package com.emc.moodmingle.data.repository.post

import com.emc.moodmingle.data.dao.post.CommentDao
import com.emc.moodmingle.data.model.post.CommentEntity
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentDao: CommentDao
) {
    suspend fun insertComment(commentEntity: CommentEntity) = commentDao.insertComment(commentEntity)
    fun getCommentsByPostId(postId: Int) = commentDao.getCommentsByPostId(postId)
    suspend fun getCommentById(id: Int) = commentDao.getCommentById(id)
    suspend fun updateComment(commentEntity: CommentEntity) = commentDao.updateComment(commentEntity)
    suspend fun deleteComment(commentEntity: CommentEntity) = commentDao.deleteComment(commentEntity)
    fun getCommentCountByPostUid(postId: Int) = commentDao.getCommentCountByPostId(postId)
}