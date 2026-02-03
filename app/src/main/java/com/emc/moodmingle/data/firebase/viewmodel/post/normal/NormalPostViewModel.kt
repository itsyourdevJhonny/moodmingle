package com.emc.moodmingle.data.firebase.viewmodel.post.normal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.post.normal.NormalPostEntity
import com.emc.moodmingle.data.firebase.repository.post.ShareRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.post.normal.NormalPostRepository
import com.emc.moodmingle.data.firebase.repository.remix.RemixRepository
import com.emc.moodmingle.data.model.post.user.PostType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NormalPostViewModel @Inject constructor(
    private val postRepository: NormalPostRepository,
    private val shareRepository: ShareRepositoryFirebase,
    private val remixRepository: RemixRepository
) : ViewModel() {

    fun getAllPosts() = postRepository.getAllPosts()

    fun createPost(postEntity: NormalPostEntity) = viewModelScope.launch {
        postRepository.insertPost(postEntity)
    }

    fun getPostsByUserId(userId: String) = postRepository.getPostsByUserId(userId)

    fun updatePost(postEntity: NormalPostEntity) = viewModelScope.launch {
        postRepository.updatePost(postEntity)
    }

    fun deletePost(post: NormalPostEntity) = viewModelScope.launch {
        postRepository.deletePost(post)
    }

    fun getPostById(id: String) = postRepository.getPostById(id)

    fun getPostByVideoUrl(videoUrl: String) = postRepository.getPostByVideoUrl(videoUrl)

    suspend fun getCombinedPostsByUser(userId: String): List<CombinedPost> {
        // get user posts
        val userPosts = postRepository.getPostsByUserId(userId)
            .first()
            .map {
                CombinedPost(
                    id = it.id,
                    type = PostType.USER_POST,
                    entity = it,
                    createdAt = it.timestamp
                )
            }

        // get shared posts
        val sharedPosts = shareRepository.getSharedByUserUid(userId)
            .first()
            .map {
                CombinedPost(
                    id = it.id,
                    type = PostType.SHARED_POST,
                    entity = it,
                    createdAt = it.time
                )
            }

        // merge + sort
        return (userPosts + sharedPosts)
            .sortedByDescending { it.createdAt }
    }

    fun getCombinedPostsByUserFlow(userId: String): Flow<List<CombinedPost>> {
        val userPostsFlow: Flow<List<CombinedPost>> = flow {
            val posts = postRepository.getPostsByUserId(userId).first()
            emit(posts.map {
                CombinedPost(
                    id = it.id,
                    type = PostType.USER_POST,
                    entity = it,
                    createdAt = it.timestamp
                )
            })
        }

        val sharedPostsFlow: Flow<List<CombinedPost>> =
            shareRepository.getSharedByUserUid(userId).map { shares ->
                shares.map {
                    CombinedPost(
                        id = it.id,
                        type = PostType.SHARED_POST,
                        entity = it,
                        createdAt = it.time
                    )
                }
            }

        return combine(userPostsFlow, sharedPostsFlow) { userPosts, sharedPosts ->
            (userPosts + sharedPosts).sortedByDescending { it.createdAt }
        }
    }

    fun getAllCombinedPosts(): Flow<List<CombinedPost>> {
        val postsFlow: Flow<List<CombinedPost>> = flow {
            val posts = postRepository.getAllPosts().first()
            emit(posts.map {
                CombinedPost(
                    id = it.id,
                    type = PostType.USER_POST,
                    entity = it,
                    createdAt = it.timestamp
                )
            })
        }

        val sharesFlow: Flow<List<CombinedPost>> =
            shareRepository.getAllShares().map { shares ->
                shares.map {
                    CombinedPost(
                        id = it.id,
                        type = PostType.SHARED_POST,
                        entity = it,
                        createdAt = it.time
                    )
                }
            }

        val remixesFlow: Flow<List<CombinedPost>> =
            remixRepository.getAllRemixes().map { remixes ->
                remixes.map {
                    CombinedPost(
                        id = it.id,
                        type = PostType.REMIX_POST,
                        entity = it,
                        createdAt = it.timestamp
                    )
                }
            }

        return combine(postsFlow, sharesFlow, remixesFlow) { userPosts, sharedPosts, remixPosts ->
            (userPosts + sharedPosts + remixPosts).sortedByDescending { it.createdAt }
        }
    }
}

data class CombinedPost(
    val id: String,
    val entity: Any,
    val type: PostType,
    val createdAt: Long
)

data class PostUiState(
    val isLoading: Boolean = false,
    val posts: List<NormalPostEntity> = emptyList(),
    val error: String? = null
)
