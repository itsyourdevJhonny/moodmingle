package com.emc.moodmingle.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.model.post.ReactionEntity
import com.emc.moodmingle.data.service.ReactionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReactionViewModel @Inject constructor(
    private val reactionService: ReactionService
) : ViewModel() {

    fun createReaction(reactionEntity: ReactionEntity) = viewModelScope.launch {
        reactionService.createReaction(reactionEntity)
    }

    fun getReactionsByPostId(postId: Int) = flow {
        emit(reactionService.getReactionsByPostId(postId))
    }.flowOn(Dispatchers.IO)

    fun getReactionsCountByPostId(postId: Int): Flow<Long> {
        return reactionService.getReactionsCountByPostId(postId)
    }

    fun getReactionById(id: Int) = flow {
        emit(reactionService.getReactionById(id))
    }.flowOn(Dispatchers.IO)

    fun getReactionByReactorIdAndPostId(reactorId: String, postId: Int) = flow {
        emit(reactionService.getReactionByReactorIdAndPostId(reactorId, postId))
    }.flowOn(Dispatchers.IO)

    fun updateReaction(reactionEntity: ReactionEntity) = viewModelScope.launch {
        reactionService.updateReaction(reactionEntity)
    }

    fun deleteReaction(reactionEntity: ReactionEntity) = viewModelScope.launch {
        reactionService.deleteReaction(reactionEntity)
    }
}