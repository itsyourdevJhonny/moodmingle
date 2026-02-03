package com.emc.moodmingle.viewmodel.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.repository.post.PostRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.post.ShareRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.post.normal.NormalPostRepository
import com.emc.moodmingle.data.firebase.repository.remix.RemixRepository
import com.emc.moodmingle.data.model.post.user.PostType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModelFirebase @Inject constructor(
    private val postRepository: PostRepositoryFirebase,
    private val normalPostRepository: NormalPostRepository,
    private val shareRepository: ShareRepositoryFirebase,
    private val remixRepository: RemixRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState(isLoading = true))
    val uiState: StateFlow<PostUiState> = _uiState

    private val _filteredPosts = MutableStateFlow<List<PostEntityFirebase>>(emptyList())
    val filteredPosts = _filteredPosts.asStateFlow()

    fun getFilteredPostsByMood(mood: String) {
        FirebaseFirestore.getInstance()
            .collection("posts")
            .whereEqualTo("mood", mood)
            .orderBy("timeAgo", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _filteredPosts.value = emptyList()
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PostEntityFirebase::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _filteredPosts.value = posts
            }
    }

    fun loadPosts() {
        viewModelScope.launch {
            postRepository.getAllPosts()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { posts ->
                    _uiState.update { it.copy(isLoading = false, posts = posts) }
                }
        }
    }

    fun getAllPosts() = postRepository.getAllPosts()

    fun createPost(postEntity: PostEntityFirebase) = viewModelScope.launch {
        postRepository.insertPost(postEntity)
    }

    suspend fun getPostsByUserId(userId: String) = postRepository.getPostsByUserId(userId)

    fun updatePost(postEntity: PostEntityFirebase) = viewModelScope.launch {
        postRepository.updatePost(postEntity)
    }

    fun deletePost(postEntity: PostEntityFirebase) = viewModelScope.launch {
        postRepository.deletePost(postEntity)
    }

    fun getPostById(id: String) = postRepository.getPostById(id)
    suspend fun getPostByIdOnce(id: String) = postRepository.getPostByIdOnce(id)

    fun getPostByVideoUrl(videoUrl: String) = postRepository.getPostByVideoUrl(videoUrl)

    suspend fun getCombinedPostsByUser(userId: String): List<com.emc.moodmingle.data.model.post.user.CombinedPost> {
        // get user posts
        val userPosts = postRepository.getPostsByUserId(userId).map {
            com.emc.moodmingle.data.model.post.user.CombinedPost(
                id = it.id,
                type = PostType.USER_POST,
                postEntity = it,
                shareEntity = null,
                createdAt = it.timeAgo
            )
        }

        // get shared posts
        val sharedPosts = shareRepository.getSharedByUserUid(userId)
            .first()
            .map {
                com.emc.moodmingle.data.model.post.user.CombinedPost(
                    id = it.id,
                    type = PostType.SHARED_POST,
                    postEntity = null,
                    shareEntity = it,
                    createdAt = it.time
                )
            }

        // merge + sort
        return (userPosts + sharedPosts).sortedByDescending { it.createdAt }
    }

    fun getCombinedPostsByUserFlow(userId: String): Flow<List<com.emc.moodmingle.data.model.post.user.CombinedPost>> {
        val userPostsFlow: Flow<List<com.emc.moodmingle.data.model.post.user.CombinedPost>> = flow {
            val posts = postRepository.getPostsByUserId(userId)
            emit(posts.map {
                com.emc.moodmingle.data.model.post.user.CombinedPost(
                    id = it.id,
                    type = PostType.USER_POST,
                    postEntity = it,
                    shareEntity = null,
                    createdAt = it.timeAgo
                )
            })
        }

        val sharedPostsFlow: Flow<List<com.emc.moodmingle.data.model.post.user.CombinedPost>> =
            shareRepository.getSharedByUserUid(userId).map { shares ->
                shares.map {
                    com.emc.moodmingle.data.model.post.user.CombinedPost(
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
    }

    fun getAllCombinedPosts(): Flow<List<CombinedPost>> {
        val postsFlow: Flow<List<CombinedPost>> = flow {
            val posts = postRepository.getAllPosts().first()
            emit(posts.map {
                CombinedPost(
                    id = it.id,
                    type = PostType.USER_POST,
                    entity = it,
                    createdAt = it.timeAgo
                )
            })
        }

        val normalPostsFlow: Flow<List<CombinedPost>> = flow {
            val normalPosts = normalPostRepository.getAllPosts().first()
            emit(normalPosts.map {
                CombinedPost(
                    id = it.id,
                    type = PostType.NORMAL_POST,
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

        return combine(
            postsFlow,
            normalPostsFlow,
            sharesFlow,
            remixesFlow
        ) { userPosts, normalPosts, sharedPosts, remixPosts ->
            (userPosts + normalPosts + sharedPosts + remixPosts).sortedByDescending { it.createdAt }
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
    val posts: List<PostEntityFirebase> = emptyList(),
    val error: String? = null
)
