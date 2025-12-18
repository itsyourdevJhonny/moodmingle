package com.emc.moodmingle.data.firebase.repository

import android.content.Context
import android.net.Uri
import com.emc.moodmingle.cloudinary.CloudinaryService
import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.firebase.model.PostEntityFirebase
import javax.inject.Inject

class UploadRepository @Inject constructor(
    private val postRepository: PostRepositoryFirebase,
    private val userDao: UserDao
) {
    suspend fun createPost(
        context: Context,
        uris: List<Uri>,
        mood: String,
        moodEmoji: String,
        hashtag: String,
        caption: String,
        description: String,
        type: String,
        onProgress: (Float) -> Unit
    ) {
        val uploadedUrls = mutableListOf<String>()

        uris.forEachIndexed { index, uri ->
            val url = CloudinaryService.uploadFile(context, uri) { bytes, totalBytes ->
                val fileProgress = bytes.toFloat() / totalBytes
                val overallProgress = (index.toFloat() + fileProgress) / uris.size
                onProgress(overallProgress)
            }

            url?.let { uploadedUrls.add(it) }
        }

        val loggedUser = userDao.getLoggedUser()
        val post = PostEntityFirebase(
            userId = loggedUser?.uid ?: "",
            username = loggedUser?.username ?: "",
            avatarUrl = loggedUser?.avatarUrl ?: "",
            mood = mood,
            moodEmoji = moodEmoji,
            hashtag = hashtag,
            caption = caption,
            description = description,
            timeAgo = System.currentTimeMillis(),
            urls = uploadedUrls,
            type = type
        )

        postRepository.insertPost(post)
    }
}