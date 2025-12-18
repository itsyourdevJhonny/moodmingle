package com.emc.moodmingle.viewmodel.local

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.model.post.CommentEntity
import com.emc.moodmingle.data.model.post.PostEntity
import com.emc.moodmingle.data.model.post.user.CombinedPost
import com.emc.moodmingle.data.model.post.user.PostType
import com.emc.moodmingle.data.service.CommentService
import com.emc.moodmingle.data.service.PostService
import com.emc.moodmingle.data.service.ShareService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postService: PostService,
    private val commentService: CommentService,
    private val shareService: ShareService
) : ViewModel() {

    val post = PostDelegate()
    val comment = CommentDelegate()

    inner class PostDelegate {
        private val _uiState = MutableStateFlow(PostUiState(isLoading = true))
        val uiState: StateFlow<PostUiState> = _uiState

        /*private val thumbnailCache = mutableMapOf<String, Bitmap>()

        fun getCachedThumbnail(url: String): Bitmap? = thumbnailCache[url]

        fun cacheThumbnail(url: String, bitmap: Bitmap) {
            thumbnailCache[url] = bitmap
        }*/

        // cache that stores safe, cloned thumbnails
        private val thumbnailCache = mutableMapOf<String, Bitmap>()

        fun getCachedThumbnail(url: String): Bitmap? {
            val key = generateThumbnailKey(url)
            return thumbnailCache[key]
        }

        fun cacheThumbnail(url: String, bitmap: Bitmap) {
            val key = generateThumbnailKey(url)

            // clone bitmap so it never reuses shared memory
            val clone = bitmap.copy(Bitmap.Config.ARGB_8888, false)

            thumbnailCache[key] = clone
        }

        private fun generateThumbnailKey(url: String): String {
            // make sure each video gets a unique key
            return url.trim().lowercase() + "_thumb"
        }


        fun loadPosts() {
            viewModelScope.launch {
                getAllPosts()
                    .onStart {
                        _uiState.update { it.copy(isLoading = true) }
                        println("✅ loadPosts started")
                    }
                    .catch { e ->
                        println("❌ loadPosts error: ${e.message}")
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                    .collect { posts ->
                        println("✅ posts collected: ${posts.size}")
                        _uiState.update { it.copy(isLoading = false, posts = posts) }
                    }
            }
        }

        fun createPost(postEntity: PostEntity) = viewModelScope.launch {
            postService.createPost(postEntity)
        }

        fun getReactionCountByPostId(postId: Int) = flow {
            emit(postService.getReactionCountByPostId(postId))
        }.flowOn(Dispatchers.IO)

        fun getAllPosts(): Flow<List<PostEntity>> {
            return postService.getAllPosts()
        }

        fun getPostsByUserId(userId: String): Flow<List<PostEntity>> {
            return postService.getPostsByUserId(userId)
        }

        fun getPostById(id: Int) = flow {
            emit(postService.getPostById(id))
        }.flowOn(Dispatchers.IO)

        fun updatePost(postEntity: PostEntity) = viewModelScope.launch {
            postService.updatePost(postEntity)
        }

        fun deletePost(postEntity: PostEntity) = viewModelScope.launch {
            postService.deletePost(postEntity)
        }

        /*fun getCombinedPostsByUser(userId: String): Flow<List<CombinedPost>> {
            val userPostsFlow = postService.getPostsByUserId(userId).map { posts ->
                posts.map {
                    CombinedPost(
                        id = it.id,
                        type = PostType.USER_POST,
                        postEntity = it,
                        shareEntity = null,
                        createdAt = it.timeAgo
                    )
                }
            }

            val sharedPostsFlow = shareService.getSharedByUserUid(userId).map { shares ->
                shares.map {
                    CombinedPost(
                        id = it.id,
                        type = PostType.SHARED_POST,
                        postEntity = null,
                        shareEntity = it,
                        createdAt = it.time
                    )
                }
            }

            return combine(userPostsFlow, sharedPostsFlow) { userPosts, sharedPosts ->
                (userPosts + sharedPosts).sortedByDescending { it.createdAt }
            }
        }*/
    }

    inner class CommentDelegate {
        fun createComment(commentEntity: CommentEntity) = viewModelScope.launch {
            commentService.createComment(commentEntity)
        }

        fun getCommentsByPostId(postId: Int): Flow<List<CommentEntity>> {
            return commentService.getCommentsByPostId(postId)
        }

        fun getCommentById(id: Int) = liveData {
            emit(commentService.getCommentById(id))
        }

        fun getCommentCountByPostId(postId: Int): Flow<Long> {
            return commentService.getCommentCountByPostId(postId)
        }

        fun updateComment(commentEntity: CommentEntity) = viewModelScope.launch {
            commentService.updateComment(commentEntity)
        }

        fun deleteComment(commentEntity: CommentEntity) = viewModelScope.launch {
            commentService.deleteComment(commentEntity)
        }
    }
}

data class PostUiState(
    val isLoading: Boolean = false,
    val posts: List<PostEntity> = emptyList(),
    val error: String? = null
)