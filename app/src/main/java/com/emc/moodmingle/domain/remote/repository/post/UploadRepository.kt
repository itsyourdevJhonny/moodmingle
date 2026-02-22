package com.emc.moodmingle.domain.remote.repository.post

import android.content.Context
import android.net.Uri
import com.emc.moodmingle.api.cloudinary.CloudinaryService
import com.emc.moodmingle.api.nominatim.NominatimPlace
import com.emc.moodmingle.domain.local.dao.user.UserDao
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodAudience
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.model.post.dailymood.gif.Gif
import com.emc.moodmingle.domain.remote.model.post.dailymood.media.DailyMoodMedia
import com.emc.moodmingle.domain.remote.model.post.dailymood.music.MusicTrack
import com.emc.moodmingle.domain.remote.model.post.dailymood.settings.DailyMoodSettings
import com.emc.moodmingle.domain.remote.model.post.dailymood.text.DailyMoodText
import com.emc.moodmingle.domain.remote.model.post.normal.PostEntityFirebase
import com.emc.moodmingle.domain.remote.model.post.normal.NormalPostEntity
import com.emc.moodmingle.domain.remote.model.post.normal.PostDescription
import com.emc.moodmingle.domain.remote.model.post.settings.PostSettings
import com.emc.moodmingle.domain.remote.model.post.remix.Mood
import com.emc.moodmingle.domain.remote.model.video.VideoComment
import com.emc.moodmingle.domain.remote.repository.dailymood.DailyMoodRepository
import com.emc.moodmingle.domain.remote.repository.post.normal.NormalPostRepository
import com.emc.moodmingle.domain.remote.repository.video.VideoCommentRepository
import com.emc.moodmingle.ui.create.post.dialogs.LinkMetadata
import javax.inject.Inject

class UploadRepository @Inject constructor(
    private val postRepository: PostRepositoryFirebase,
    private val normalPostRepository: NormalPostRepository,
    private val videoCommentRepository: VideoCommentRepository,
    private val dailyMoodRepository: DailyMoodRepository,
    private val userDao: UserDao,
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
        onProgress: (Float) -> Unit,
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
        onProgress: (Float) -> Unit,
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
        onProgress: (Float) -> Unit,
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

    /**
     * Creates a DailyMoodEntity with uploaded media and saves it to Firestore.
     *
     * - Uploads all selected media files to Cloudinary
     * - Tracks overall upload progress
     * - Builds DailyMoodEntity
     * - Computes expiration automatically (inside repository)
     */
    suspend fun createDailyMood(
        context: Context,
        uris: List<Uri>,
        mood: Mood,
        description: PostDescription?,
        text: DailyMoodText,
        audience: DailyMoodAudience,
        settings: DailyMoodSettings,
        musicTrack: MusicTrack? = null,
        gif: Gif = Gif(),
        location: NominatimPlace? = null,
        onProgress: (Float) -> Unit,
    ) {

        val uploadedUrls = mutableListOf<String>()

        // Upload media files to Cloudinary
        uris.forEachIndexed { index, uri ->
            val url = CloudinaryService.uploadFile(
                context = context,
                fileUri = uri
            ) { bytes, totalBytes ->
                val fileProgress = bytes.toFloat() / totalBytes
                val overallProgress = (index.toFloat() + fileProgress) / uris.size

                onProgress(overallProgress)
            }

            url?.let { uploadedUrls.add(it) }
        }

        // Get logged user
        val loggedUser = userDao.getLoggedUser()

        // Build media object
        val media = DailyMoodMedia(urls = uploadedUrls)

        // Build DailyMoodEntity
        val dailyMood = DailyMoodEntity(
            userId = loggedUser?.uid.orEmpty(),
            mood = mood,
            description = description,
            media = media,
            gif = gif,
            musicTrack = musicTrack,
            location = location,
            text = text,
            audience = audience,
            settings = settings,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // Save to Firestore
        dailyMoodRepository.createDailyMood(dailyMood = dailyMood)
    }
}