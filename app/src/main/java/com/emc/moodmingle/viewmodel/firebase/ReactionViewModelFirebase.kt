package com.emc.moodmingle.viewmodel.firebase

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.ReactionEntityFirebase
import com.emc.moodmingle.data.firebase.repository.ReactionRepositoryFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for reactions with Firebase.
 * preserves all method names and exposes flows for realtime updates
 */
@HiltViewModel
class ReactionViewModelFirebase @Inject constructor(
    private val repository: ReactionRepositoryFirebase
) : ViewModel() {

    // holds all reactions made by the current user
    private val _userReactions = MutableStateFlow<Map<String, ReactionEntityFirebase>>(emptyMap())
    val userReactions: StateFlow<Map<String, ReactionEntityFirebase>> = _userReactions

    /**
     * observes the current user's reactions and updates state automatically
     */
    fun observeUserReactions(userId: String) {
        viewModelScope.launch {
            repository.getReactionsByUser(userId)
                .collect { reactions ->
                    _userReactions.value = reactions.associateBy { it.postId }
                }
        }
    }

    suspend fun getReactionSnapshot(reactorId: String, postId: String): ReactionEntityFirebase? {
        return try {
            repository.getReactionByUserAndPost(reactorId, postId)
        } catch (e: Exception) {
            Log.e("ReactionViewModel", "Failed to get reaction snapshot: ${e.message}")
            null
        }
    }

//    /*fun loadReactionsForUser(userId: String) {
//        viewModelScope.launch {
//            val snapshot = repository.getReactionsByUser(userId)
//            _userReactions.value = snapshot
//        }
//    }*/

    /**
     * insert a reaction
     */
    fun insertReaction(reactionEntity: ReactionEntityFirebase) = viewModelScope.launch {
        repository.insertReaction(reactionEntity)
    }

    fun hasUserReacted(postId: String, reactorId: String): Flow<Boolean> =
        repository.hasUserReacted(postId, reactorId)


    /**
     * get all reactions for a post as flow
     */
    fun getReactionsByPostId(postId: String): StateFlow<List<ReactionEntityFirebase>> =
        repository.getReactionsByPostId(postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * observe reaction count as flow
     */
    fun getReactionsCountByPostId(postId: String): StateFlow<Long> =
        repository.getReactionsCountByPostId(postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    /**
     * get reaction by id as flow
     */
    fun getReactionById(id: String, postId: String): StateFlow<ReactionEntityFirebase?> {
        Log.d("VIEWMODEL", "${repository.getReactionById(id, postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)}")
        return repository.getReactionById(id, postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    /**
     * get reaction by reactorId and postId as flow
     */
    fun getReactionByReactorIdAndPostId(reactorId: String, postId: String): Flow<ReactionEntityFirebase?> {
        return repository.getReactionByReactorIdAndPostId(reactorId, postId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    suspend fun getReactionByReactorIdAndPostIdOnce(reactorId: String, postId: String): ReactionEntityFirebase? {
        return repository.getReactionByReactorIdAndPostIdOnce(reactorId, postId)
    }

    /**
     * update a reaction
     */
    fun updateReaction(reactionEntity: ReactionEntityFirebase) = viewModelScope.launch {
        repository.updateReaction(reactionEntity)
    }

    /**
     * delete a reaction
     */
    fun deleteReaction(reactionEntity: ReactionEntityFirebase) = viewModelScope.launch {
        repository.deleteReaction(reactionEntity)
    }
}
