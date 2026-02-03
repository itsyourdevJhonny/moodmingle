package com.emc.moodmingle.data.firebase.repository.post

import android.content.Context
import android.net.Uri
import com.emc.moodmingle.cloudinary.CloudinaryService
import com.emc.moodmingle.data.dao.UserDao
import com.emc.moodmingle.data.firebase.model.post.PostEntityFirebase
import com.emc.moodmingle.data.firebase.model.post.normal.NormalPostEntity
import com.emc.moodmingle.data.firebase.model.post.normal.PostDescription
import com.emc.moodmingle.data.firebase.model.post.settings.PostSettings
import com.emc.moodmingle.data.firebase.model.remix.Mood
import com.emc.moodmingle.data.firebase.model.video.VideoComment
import com.emc.moodmingle.data.firebase.repository.post.normal.NormalPostRepository
import com.emc.moodmingle.data.firebase.repository.video.VideoCommentRepository
import com.emc.moodmingle.ui.create.post.dialogs.LinkMetadata
import javax.inject.Inject

class UploadRepository @Inject constructor(
    private val postRepository: PostRepositoryFirebase,
    private val normalPostRepository: NormalPostRepository,
    private val videoCommentRepository: VideoCommentRepository,
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

    suspend fun createNormalPost(
        context: Context,
        description: PostDescription,
        hashtag: String,
        mood: Mood,
        uris: List<Uri>,
        mentionedUserIds: List<String>,
        taggedUserIds: List<String>,
        location: String,
        linkMetadata: LinkMetadata?,
        settings: PostSettings,
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

        val post = NormalPostEntity(
            userId = loggedUser?.uid.orEmpty(),
            description = description,
            hashtag = hashtag,
            mood = mood,
            urls = uploadedUrls,
            mentionedUserIds = mentionedUserIds,
            taggedUserIds = taggedUserIds,
            location = location,
            linkMetadata = linkMetadata,
            settings = settings
        )

        normalPostRepository.insertPost(post)
    }

    suspend fun createVideoComment(
        context: Context,
        videoUrl: String,
        commentText: String,
        currentUserId: String,
        uris: List<Uri>,
        emotion: String,
        isAnonymous: Boolean,
        onProgress: (Float) -> Unit
    ) {
        var newComment = VideoComment(
            videoUrl = videoUrl,
            commenterId = currentUserId,
            comment = commentText,
            emotion = emotion,
            anonymous = isAnonymous
        )

        if (uris.isNotEmpty()) {
            val uploadedUrls = mutableListOf<String>()

            uris.forEachIndexed { index, uri ->
                val url = CloudinaryService.uploadFile(context, uri) { bytes, totalBytes ->
                    val fileProgress = bytes.toFloat() / totalBytes
                    val overallProgress = (index.toFloat() + fileProgress) / uris.size
                    onProgress(overallProgress)
                }

                url?.let { uploadedUrls.add(it) }
            }

            newComment = newComment.copy(mediaUrls = uploadedUrls)
        }

        videoCommentRepository.createComment(newComment)
    }
}