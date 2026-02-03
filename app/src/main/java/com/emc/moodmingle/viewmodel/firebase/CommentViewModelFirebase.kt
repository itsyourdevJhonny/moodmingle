package com.emc.moodmingle.viewmodel.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.post.CommentEntityFirebase
import com.emc.moodmingle.data.firebase.repository.post.CommentRepositoryFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Exposes comment operations for the UI layer.
 */
@HiltViewModel
class CommentViewModelFirebase @Inject constructor(
    private val commentRepositoryFirebase: CommentRepositoryFirebase
) : ViewModel() {

    /** create comment */
    fun createComment(comment: CommentEntityFirebase) = viewModelScope.launch {
        commentRepositoryFirebase.createComment(comment)
    }

    /** get comments for a specific post */
    fun getCommentsByPostId(postId: String): Flow<List<CommentEntityFirebase>> {
        return commentRepositoryFirebase.getCommentsByPostId(postId)
    }

    /** get a single comment by id */
    suspend fun getCommentById(id: String): CommentEntityFirebase? {
        return commentRepositoryFirebase.getCommentById(id)
    }

    /** get comment count as realtime flow */
    fun getCommentCountByPostId(postId: String): Flow<Long> {
        return commentRepositoryFirebase.getCommentCountByPostId(postId)
    }

    /** update comment */
    fun updateComment(comment: CommentEntityFirebase) = viewModelScope.launch {
        commentRepositoryFirebase.updateComment(comment)
    }

    /** delete comment */
    fun deleteComment(comment: CommentEntityFirebase) = viewModelScope.launch {
        commentRepositoryFirebase.deleteComment(comment)
    }
}
