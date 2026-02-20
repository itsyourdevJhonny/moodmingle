package com.emc.moodmingle.domain.local.service

import com.emc.moodmingle.domain.local.model.post.CommentEntity
import com.emc.moodmingle.domain.local.repository.post.CommentRepository
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