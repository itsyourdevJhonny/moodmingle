package com.emc.moodmingle.service

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
import com.emc.moodmingle.data.firebase.repository.post.UploadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class UploadPostService : Service() {
    @Inject
    lateinit var uploadRepository: UploadRepository

    private val channelId = "upload_channel"
    private val notificationId = 101
    private var job: Job? = null

    companion object {
        const val ACTION_UPLOAD = "UPLOAD_POST"
        const val EXTRA_URIS = "EXTRA_URIS"
        const val EXTRA_MOOD = "EXTRA_MOOD"
        const val EXTRA_MOOD_EMOJI = "EXTRA_MOOD_EMOJI"
        const val EXTRA_HASHTAG = "EXTRA_HASHTAG"
        const val EXTRA_CAPTION = "EXTRA_CAPTION"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_TYPE = "EXTRA_TYPE"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_UPLOAD) {
            val uris = intent.getStringArrayListExtra(EXTRA_URIS)?.map { it.toUri() } ?: emptyList()
            val mood = intent.getStringExtra(EXTRA_MOOD) ?: ""
            val moodEmoji = intent.getStringExtra(EXTRA_MOOD_EMOJI) ?: ""
            val hashtag = intent.getStringExtra(EXTRA_HASHTAG) ?: ""
            val caption = intent.getStringExtra(EXTRA_CAPTION) ?: ""
            val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: ""
            val type = intent.getStringExtra(EXTRA_TYPE) ?: ""

            startForeground(notificationId, createNotification(0f, "Starting upload...", ongoing = true))

            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    uploadRepository.createPost(
                        context = applicationContext,
                        uris = uris,
                        mood = mood,
                        moodEmoji = moodEmoji,
                        hashtag = hashtag,
                        caption = caption,
                        description = description,
                        type = type,
                        onProgress = { progress ->
                            updateNotification(progress, "Uploading post...", ongoing = true)
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
        val channel = NotificationChannel(channelId, "Post Uploads", NotificationManager.IMPORTANCE_LOW)
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