package com.emc.moodmingle.service.upload

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.repository.post.UploadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UploadVideoCommentService : Service() {
    @Inject
    lateinit var uploadRepository: UploadRepository

    private val channelId = "upload_comment_channel"
    private val notificationId = 102
    private var job: Job? = null

    companion object {
        const val ACTION_UPLOAD = "UPLOAD_COMMENT"

        const val EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL"
        const val EXTRA_COMMENT_TEXT = "EXTRA_COMMENT_TEXT"
        const val EXTRA_CURRENT_USER_ID = "EXTRA_CURRENT_USER_ID"
        const val EXTRA_URIS = "EXTRA_URIS"
        const val EXTRA_EMOTION = "EXTRA_EMOTION"
        const val EXTRA_ANONYMOUS = "EXTRA_ANONYMOUS"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_UPLOAD) {
            val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: ""
            val commentText = intent.getStringExtra(EXTRA_COMMENT_TEXT) ?: ""
            val currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID) ?: ""
            val uris = intent.getStringArrayListExtra(EXTRA_URIS)?.map { it.toUri() } ?: emptyList()
            val emotion = intent.getStringExtra(EXTRA_EMOTION) ?: ""
            val isAnonymous = intent.getBooleanExtra(EXTRA_ANONYMOUS, false)

            startForeground(notificationId, createNotification(0f, "Starting upload...", ongoing = true))

            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    uploadRepository.createVideoComment(
                        context = applicationContext,
                        videoUrl = videoUrl,
                        commentText = commentText,
                        currentUserId = currentUserId,
                        uris = uris,
                        emotion = emotion,
                        isAnonymous = isAnonymous,
                        onProgress = { progress ->
                            updateNotification(progress, "Uploading comment...", ongoing = true)
                        }
                    )
                    // UPLOAD FINISHED
                    updateNotification(1f, "Upload complete", ongoing = false)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Upload finished!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateNotification(0f, "Upload failed", ongoing = false)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Upload failed!", Toast.LENGTH_SHORT).show()
                    }
                }
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun createNotification(progress: Float, title: String, ongoing: Boolean): Notification {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Comment Uploads", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText("${(progress * 100).toInt()}% completed")
            .setSmallIcon(R.drawable.logo)
            .setProgress(100, (progress * 100).toInt(), false)
            .setOngoing(ongoing)
            .build()
    }

    private fun updateNotification(progress: Float, title: String, ongoing: Boolean) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(progress, title, ongoing)
        manager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}