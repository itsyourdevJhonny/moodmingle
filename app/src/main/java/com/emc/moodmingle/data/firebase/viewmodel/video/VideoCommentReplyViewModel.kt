package com.emc.moodmingle.data.firebase.viewmodel.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.video.VideoCommentReply
import com.emc.moodmingle.data.firebase.repository.video.VideoCommentReplyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoCommentReplyViewModel @Inject constructor(
    private val repository: VideoCommentReplyRepository
) : ViewModel() {

    /**
     * Creates a new VideoCommentReply in Firestore.
     *
     * @param reply The reply object to create.
     */
    fun createReply(reply: VideoCommentReply) = viewModelScope.launch {
        repository.createReply(reply)
    }

    /**
     * Returns a Flow of replies for a specific comment id, sorted by timestamp descending.
     *
     * @param commentId The id of the comment.
     * @return Flow<List<VideoCommentReply>>
     */
    fun getRepliesByCommentId(commentId: String): Flow<List<VideoCommentReply>> {
        return repository.getRepliesByCommentId(commentId)
    }

    /**
     * Returns a Flow of the number of replies for a specific comment.
     *
     * @param commentId The id of the comment.
     * @return Flow<Long>
     */
    fun getReplyCountByCommentId(commentId: String): Flow<Long> {
        return repository.getReplyCountByCommentId(commentId)
    }

    /**
     * Updates a specific reply in Firestore.
     *
     * @param reply The reply object to update.
     */
    fun updateReply(reply: VideoCommentReply) = viewModelScope.launch {
        repository.updateReply(reply)
    }

    /**
     * Deletes a specific reply in Firestore.
     *
     * @param reply The reply object to delete.
     */
    fun deleteReply(reply: VideoCommentReply) = viewModelScope.launch {
        repository.deleteReply(reply)
    }

    /**
     * Toggles a reactor for a specific reply.
     *
     * @param reply The reply object to update.
     * @param currentUserId The user id to toggle.
     */
    fun toggleReplyReaction(reply: VideoCommentReply, currentUserId: String) = viewModelScope.launch {
        val isReacted = reply.reactorIds.contains(currentUserId)
        val newReactorIds = if (isReacted) reply.reactorIds - currentUserId else reply.reactorIds + currentUserId
        updateReply(reply.copy(reactorIds = newReactorIds))
    }

    /**
     * Returns a Flow for a single reply by id.
     *
     * @param replyId The id of the reply.
     * @return Flow<VideoCommentReply?>
     */
    fun getReplyByIdAsFlow(replyId: String): Flow<VideoCommentReply?> = callbackFlow {
        val listener = repository.collection.document(replyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject(VideoCommentReply::class.java))
            }

        awaitClose { listener.remove() }
    }
}
