package com.emc.moodmingle.data.repository.insight

import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.dao.post.CommentDao
import com.emc.moodmingle.data.dao.post.PostDao
import com.emc.moodmingle.data.dao.post.ReactionDao
import com.emc.moodmingle.ui.screens.InsightData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository responsible for fetching and combining post, comment,
 * and reaction counts for a given user to generate insight statistics.
 */
class InsightRepository @Inject constructor(
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val reactionDao: ReactionDao
) {
    /**
     * Returns a Flow emitting InsightData for the specified user.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserInsights(userId: String, period: String): Flow<InsightData> {
        val now = System.currentTimeMillis()
        val startTime = when (period) {
            "Week" -> now - 7 * 24 * 60 * 60 * 1000L
            "Month" -> now - 30 * 24 * 60 * 60 * 1000L
            "3 Months" -> now - 90 * 24 * 60 * 60 * 1000L
            else -> 0L
        }

        val postsFlow = postDao.getPostsByUserId(userId).map { posts ->
            posts.filter { post -> post.timeAgo >= startTime }.size
        }

        val commentsFlow = postDao.getPostsByUserId(userId).flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(0)
            } else {
                combine(posts.map { post ->
                    commentDao.getCommentCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val reactionsFlow = postDao.getPostsByUserId(userId).flatMapLatest { posts ->
            if (posts.isEmpty()) {
                flowOf(0)
            } else {
                combine(posts.map { post ->
                    reactionDao.getReactionsCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        // (total reactions + total comments) / number of posts
        val avgScoreFlow = combine(postsFlow, commentsFlow, reactionsFlow) { posts, comments, reactions ->
            if (posts > 0) {
                ((comments + reactions).toDouble() / posts)
            } else 0.0
        }

        // combine all flows into a single InsightData stream
        return combine(postsFlow, commentsFlow, reactionsFlow, avgScoreFlow) { posts, comments, reactions, avgScore ->
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

        // define the current period duration
        val duration = when (period) {
            "Week" -> 7 * 24 * 60 * 60 * 1000L
            "Month" -> 30 * 24 * 60 * 60 * 1000L
            "3 Months" -> 90 * 24 * 60 * 60 * 1000L
            else -> 7 * 24 * 60 * 60 * 1000L
        }

        // previous period range
        val previousEndTime = now - duration
        val previousStartTime = previousEndTime - duration

        val postsFlow = postDao.getPostsByUserId(userId).map { posts ->
            posts.filter { post ->
                post.timeAgo in previousStartTime..previousEndTime
            }.size
        }

        val commentsFlow = postDao.getPostsByUserId(userId).flatMapLatest { posts ->
            val filteredPosts = posts.filter { post ->
                post.timeAgo in previousStartTime..previousEndTime
            }
            if (filteredPosts.isEmpty()) {
                flowOf(0)
            } else {
                combine(filteredPosts.map { post ->
                    commentDao.getCommentCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val reactionsFlow = postDao.getPostsByUserId(userId).flatMapLatest { posts ->
            val filteredPosts = posts.filter { post ->
                post.timeAgo in previousStartTime..previousEndTime
            }
            if (filteredPosts.isEmpty()) {
                flowOf(0)
            } else {
                combine(filteredPosts.map { post ->
                    reactionDao.getReactionsCountByPostId(post.id)
                }) { counts ->
                    counts.sum()
                }
            }
        }

        val avgScoreFlow = combine(postsFlow, commentsFlow, reactionsFlow) { posts, comments, reactions ->
            if (posts > 0) ((comments + reactions).toDouble() / posts) else 0.0
        }

        return combine(postsFlow, commentsFlow, reactionsFlow, avgScoreFlow) { posts, comments, reactions, avgScore ->
            InsightData(
                posts = posts,
                comments = comments.toInt(),
                reactions = reactions.toInt(),
                avgScore = avgScore
            )
        }
    }
}
