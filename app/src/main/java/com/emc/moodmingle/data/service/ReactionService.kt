package com.emc.moodmingle.data.service

import com.emc.moodmingle.data.dao.post.ReactionDao
import com.emc.moodmingle.data.model.post.ReactionEntity
import javax.inject.Inject

class ReactionService @Inject constructor(
    private val reactionDao: ReactionDao
) {
    suspend fun createReaction(reactionEntity: ReactionEntity) =
        reactionDao.insertReaction(reactionEntity)

    suspend fun getReactionsByPostId(postId: Int) =
        reactionDao.getReactionsByPostId(postId)

    fun getReactionsCountByPostId(postId: Int) =
        reactionDao.getReactionsCountByPostId(postId)

    suspend fun getReactionById(id: Int) = reactionDao.getReactionById(id)

    suspend fun getReactionByReactorIdAndPostId(reactorId: String, postId: Int) =
        reactionDao.getReactionByReactorIdAndPostId(reactorId, postId)

    suspend fun updateReaction(reactionEntity: ReactionEntity) =
        reactionDao.updateReaction(reactionEntity)

    suspend fun deleteReaction(reactionEntity: ReactionEntity) =
        reactionDao.deleteReaction(reactionEntity)
}