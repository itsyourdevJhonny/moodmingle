package com.emc.moodmingle.data.repository.post

import com.emc.moodmingle.data.dao.post.PostDao
import com.emc.moodmingle.data.model.post.PostEntity
import javax.inject.Inject

class PostRepository @Inject constructor(private val postDao: PostDao
) {
    suspend fun insertPost(postEntity: PostEntity) = postDao.insertPost(postEntity)
    fun getAllPosts() = postDao.getAllPosts()
    fun getPostsByUserId(userId: String) = postDao.getPostsByUserId(userId)
    suspend fun getPostByUid(id: Int) = postDao.getPostById(id)
    suspend fun getReactionCountByPostId(postId: Int) = postDao.getReactionCountByPostId(postId)
    suspend fun updatePost(postEntity: PostEntity) = postDao.updatePost(postEntity)
    suspend fun deletePost(postEntity: PostEntity) = postDao.deletePost(postEntity)
}