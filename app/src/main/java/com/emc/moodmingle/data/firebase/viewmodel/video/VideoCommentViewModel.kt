package com.emc.moodmingle.data.firebase.viewmodel.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
import com.emc.moodmingle.data.firebase.repository.video.VideoCommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoCommentViewModel @Inject constructor(
    private val commentRepository: VideoCommentRepository
) : ViewModel() {

    fun createComment(comment: VideoComment) = viewModelScope.launch {
        commentRepository.createComment(comment)
    }

    fun getCommentsByVideoUrl(videoUrl: String): Flow<List<VideoComment>> {
        return commentRepository.getCommentsByVideoUrl(videoUrl)
    }

    suspend fun getCommentById(id: String): VideoComment? {
        return commentRepository.getCommentById(id)
    }

    fun getCommentCountByVideoUrl(videoUrl: String): Flow<Long> {
        return commentRepository.getCommentCountByVideoUrl(videoUrl)
    }

    fun updateComment(comment: VideoComment) = viewModelScope.launch {
        commentRepository.updateComment(comment)
    }

    fun deleteComment(comment: VideoComment) = viewModelScope.launch {
        commentRepository.deleteComment(comment)
    }

    fun getRepliesForComment(commentId: String): Flow<List<VideoCommentReply>> {
        return commentRepository.getCommentByIdAsFlow(commentId)
            .map { it?.replies?.sortedByDescending { r -> r.timestamp } ?: emptyList() }
    }

    suspend fun addReply(commentId: String, reply: VideoCommentReply) {
        val comment = commentRepository.getCommentById(commentId) ?: return
        commentRepository.updateComment(comment = comment.copy(replies = comment.replies + reply))
    }

    suspend fun updateReplyReactors(
        comment: VideoComment,
        replyId: String,
        newReactorIds: List<String>
    ) {
        val updatedReplies = comment.replies.map { reply ->
            if (reply.id == replyId) {
                reply.copy(reactorIds = newReactorIds)
            } else reply
        }

        val updatedComment = comment.copy(replies = updatedReplies)

        commentRepository.updateComment(updatedComment)
    }

    suspend fun toggleReplyReaction(
        comment: VideoComment,
        reply: VideoCommentReply,
        currentUserId: String
    ) {
        val isReacted = reply.reactorIds.contains(currentUserId)
        val newReactorIds = if (isReacted) {
            reply.reactorIds - currentUserId
        } else {
            reply.reactorIds + currentUserId
        }
        updateReplyReactors(comment, reply.id, newReactorIds)
    }
}
