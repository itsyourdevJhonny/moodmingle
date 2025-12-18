package com.emc.moodmingle.data.service

import com.emc.moodmingle.data.model.post.CommentEntity
import com.emc.moodmingle.data.repository.post.CommentRepository
import javax.inject.Inject

class CommentService @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend fun createComment(commentEntity: CommentEntity) =
        commentRepository.insertComment(commentEntity)

    fun getCommentsByPostId(postId: Int) =
        commentRepository.getCommentsByPostId(postId)

    suspend fun getCommentById(id: Int) = commentRepository.getCommentById(id)
    suspend fun updateComment(commentEntity: CommentEntity) =
        commentRepository.updateComment(commentEntity)

    suspend fun deleteComment(commentEntity: CommentEntity) =
        commentRepository.deleteComment(commentEntity)

    fun getCommentCountByPostId(postId: Int) = commentRepository.getCommentCountByPostUid(postId)
}