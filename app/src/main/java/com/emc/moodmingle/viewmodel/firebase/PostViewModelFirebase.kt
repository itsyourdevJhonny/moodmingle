package com.emc.moodmingle.viewmodel.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import com.emc.moodmingle.data.firebase.repository.PostRepositoryFirebase
import com.emc.moodmingle.data.firebase.repository.ShareRepositoryFirebase
import com.emc.moodmingle.data.model.post.user.CombinedPost
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
import kotlinx.coroutines.flow.filterNotNull
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
    private val shareRepository: ShareRepositoryFirebase
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

    /*fun getFilteredPostsByMood(mood: String) {
        viewModelScope.launch {
            postRepository.getFilteredPostsByMood(mood) // your callbackFlow
                .filterNotNull()
                .collect { post ->
                    _filteredPosts.value = post
                }
        }
    }*/


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

//    fun getFilteredPostsByMood(mood: String) = postRepository.getFilteredPostsByMood(mood)

    suspend fun getCombinedPostsByUser(userId: String): List<CombinedPost> {
        // get user posts
        val userPosts = postRepository.getPostsByUserId(userId).map {
            CombinedPost(
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
            CombinedPost(
                id = it.id,
                type = PostType.SHARED_POST,
                postEntity = null,
                shareEntity = it,
                createdAt = it.time
            )
        }

        // merge + sort
        return (userPosts + sharedPosts)
            .sortedByDescending { it.createdAt }
    }

    fun getCombinedPostsByUserFlow(userId: String): Flow<List<CombinedPost>> {
        val userPostsFlow: Flow<List<CombinedPost>> = flow {
            val posts = postRepository.getPostsByUserId(userId)
            emit(posts.map {
                CombinedPost(
                    id = it.id,
                    type = PostType.USER_POST,
                    postEntity = it,
                    shareEntity = null,
                    createdAt = it.timeAgo
                )
            })
        }

        val sharedPostsFlow: Flow<List<CombinedPost>> =
            shareRepository.getSharedByUserUid(userId).map { shares ->
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
    }
}

data class PostUiState(
    val isLoading: Boolean = false,
    val posts: List<PostEntityFirebase> = emptyList(),
    val error: String? = null
)
