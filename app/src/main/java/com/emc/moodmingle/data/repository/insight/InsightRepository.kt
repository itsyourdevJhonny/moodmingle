package com.emc.moodmingle.data.repository.insight

import com.emc.moodmingle.data.firebase.repository.post.CommentRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.post.PostRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.post.reaction.ReactionRepositoryFirebase
import com.emc.moodmingle.ui.screens.InsightData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InsightRepository @Inject constructor(
    private val postRepository: PostRepositoryFirebase,
    private val commentRepository: CommentRepositoryFirebase,
    private val reactionRepository: ReactionRepositoryFirebase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserInsights(userId: String, period: String): Flow<InsightData> {
        val now = System.currentTimeMillis()
        val startTime = when (period) {
            "Week" -> now - 7 * 24 * 60 * 60 * 1000L
            "Month" -> now - 30 * 24 * 60 * 60 * 1000L
            "3 Months" -> now - 90 * 24 * 60 * 60 * 1000L
            else -> 0L
        }

        val postsFlow = postRepository.getPostsByUserIdFlow(userId).map { posts ->
            posts.filter { post -> post.timeAgo >= startTime }.size
        }

        val commentsFlow = postRepository.getPostsByUserIdFlow(userId).flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(0)
            } else {
                combine(posts.map {
                    commentRepository.getCommentCountByPostId(it.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val reactionsFlow = postRepository.getPostsByUserIdFlow(userId).flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(0)
            } else {
                combine(posts.map { post ->
                    reactionRepository.getReactionsCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val avgScoreFlow =
            combine(postsFlow, commentsFlow, reactionsFlow) { posts, comments, reactions ->
                if (posts > 0) {
                    ((comments + reactions).toDouble() / posts)
                } else 0.0
            }

        return combine(
            postsFlow,
            commentsFlow,
            reactionsFlow,
            avgScoreFlow
        ) { posts, comments, reactions, avgScore ->
            InsightData(
                posts = posts,
                comments = comments.toInt(),
                reactions = reactions.toInt(),
                avgScore = avgScore
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPreviousUserInsights(userId: String, period: String): Flow<InsightData> {
        val now = System.currentTimeMillis()

        val duration = when (period) {
            "Week" -> 7 * 24 * 60 * 60 * 1000L
            "Month" -> 30 * 24 * 60 * 60 * 1000L
            "3 Months" -> 90 * 24 * 60 * 60 * 1000L
            else -> 7 * 24 * 60 * 60 * 1000L
        }

        val previousEndTime = now - duration
        val previousStartTime = previousEndTime - duration

        val postsFlow = postRepository.getPostsByUserIdFlow(userId).map { posts ->
            posts.filter { post ->
                post.timeAgo in previousStartTime..previousEndTime
            }.size
        }

        val commentsFlow = postRepository.getPostsByUserIdFlow(userId).flatMapLatest { posts ->
            val filteredPosts = posts.filter { post ->
                post.timeAgo in previousStartTime..previousEndTime
            }
            if (filteredPosts.isEmpty()) {
                flowOf(0)
            } else {
                combine(filteredPosts.map { post ->
                    commentRepository.getCommentCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val reactionsFlow = postRepository.getPostsByUserIdFlow(userId).flatMapLatest { posts ->
            val filteredPosts = posts.filter { post ->
                post.timeAgo in previousStartTime..previousEndTime
            }
            if (filteredPosts.isEmpty()) {
                flowOf(0)
            } else {
                combine(filteredPosts.map { post ->
                    reactionRepository.getReactionsCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val avgScoreFlow =
            combine(postsFlow, commentsFlow, reactionsFlow) { posts, comments, reactions ->
                if (posts > 0) ((comments + reactions).toDouble() / posts) else 0.0
            }

        return combine(
            postsFlow,
            commentsFlow,
            reactionsFlow,
            avgScoreFlow
        ) { posts, comments, reactions, avgScore ->
            InsightData(
                posts = posts,
                comments = comments.toInt(),
                reactions = reactions.toInt(),
                avgScore = avgScore
            )
        }
    }
}
