package com.emc.moodmingle.domain.local.service

import com.emc.moodmingle.domain.local.model.post.PostEntity
import com.emc.moodmingle.domain.local.repository.post.PostRepository
import javax.inject.Inject

class PostService @Inject constructor(private val postRepository: PostRepository) {
    suspend fun createPost(postEntity: PostEntity) = postRepository.insertPost(postEntity)
    fun getAllPosts() = postRepository.getAllPosts()
    fun getPostsByUserId(userId: String) = postRepository.getPostsByUserId(userId)
    suspend fun getPostById(id: Int) = postRepository.getPostByUid(id)
    suspend fun getReactionCountByPostId(postId: Int) =
        postRepository.getReactionCountByPostId(postId)
    suspend fun updatePost(postEntity: PostEntity) = postRepository.updatePost(postEntity)
    suspend fun deletePost(postEntity: PostEntity) = postRepository.deletePost(postEntity)
}