package com.emc.moodmingle.data.service

import com.emc.moodmingle.data.model.post.PostEntity
import com.emc.moodmingle.data.repository.post.PostRepository
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