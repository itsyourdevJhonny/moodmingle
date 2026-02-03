package com.emc.moodmingle.service.post

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.emc.moodmingle.R
import com.emc.moodmingle.data.firebase.model.post.normal.NormalPostEntity
import com.emc.moodmingle.data.firebase.repository.post.UploadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NormalPostService : Service() {
    @Inject
    lateinit var uploadRepository: UploadRepository

    private val channelId = "upload_channel"
    private val notificationId = 101
    private var job: Job? = null

    companion object {
        const val ACTION_UPLOAD = "UPLOAD_NORMAL_POST"
        const val EXTRA_NORMAL_POST = "EXTRA_NORMAL_POST"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_UPLOAD) {
            val normalPost = intent.getParcelableCompat<NormalPostEntity>(EXTRA_NORMAL_POST) ?: NormalPostEntity()

            startForeground(
                notificationId,
                createNotification(0f, "Starting upload...", ongoing = true)
            )

            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    uploadRepository.createNormalPost(
                        context = applicationContext,
                        description = normalPost.description,
                        hashtag = normalPost.hashtag,
                        mood = normalPost.mood,
                        uris = normalPost.urls.map { it.toUri() },
                        mentionedUserIds = normalPost.mentionedUserIds,
                        taggedUserIds = normalPost.taggedUserIds,
                        location = normalPost.location,
                        linkMetadata  = normalPost.linkMetadata,
                        settings = normalPost.settings,
                        onProgress = { progress ->
                            updateNotification(progress, "Uploading post...", ongoing = true)
                        }
                    )
                    // UPLOAD FINISHED
                    updateNotification(1f, "Upload complete", ongoing = false)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Upload finished!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateNotification(0f, "Upload failed", ongoing = false)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Upload failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun createNotification(progress: Float, title: String, ongoing: Boolean): Notification {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(channelId, "Post Uploads", NotificationManager.IMPORTANCE_HIGH)
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

    inline fun <reified T> Intent.getParcelableCompat(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra(key)
        }
    }
}