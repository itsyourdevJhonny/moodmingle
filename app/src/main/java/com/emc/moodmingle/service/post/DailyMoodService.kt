package com.emc.moodmingle.service.post

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.emc.moodmingle.R
import com.emc.moodmingle.domain.remote.model.post.dailymood.DailyMoodEntity
import com.emc.moodmingle.domain.remote.repository.post.UploadRepository
import com.emc.moodmingle.ui.post.action.toastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DailyMoodService : Service() {
    @Inject
    lateinit var uploadRepository: UploadRepository

    private val channelId = "daily_mood_channel"
    private val notificationId = 105
    private var job: Job? = null

    companion object {
        const val ACTION_UPLOAD = "UPLOAD_DAILY_MOOD"
        const val EXTRA_DAILY_MOOD = "EXTRA_DAILY_MOOD"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_UPLOAD) {
            val dailyMood =
                intent.getParcelableCompat<DailyMoodEntity>(EXTRA_DAILY_MOOD) ?: DailyMoodEntity()

            startForeground(
                notificationId,
                createNotification(0f, "Starting upload...", ongoing = true)
            )

            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    uploadRepository.createDailyMood(
                        context = applicationContext,
                        uris = dailyMood.media.urls.map { it.toUri() },
                        mood = dailyMood.mood,
                        description = dailyMood.description,
                        text = dailyMood.text,
                        audience = dailyMood.audience,
                        settings = dailyMood.settings,
                        musicTrack = dailyMood.musicTrack,
                        gif = dailyMood.gif,
                        location = dailyMood.location,
                        onProgress = { progress ->
                            updateNotification(progress, "Uploading mood...", ongoing = true)
                        }
                    )
                    // UPLOAD FINISHED
                    updateNotification(1f, "Upload complete", ongoing = false)
                    withContext(Dispatchers.Main) {
                        toastMessage(applicationContext, "Upload finished!")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateNotification(0f, "Upload failed", ongoing = false)
                    withContext(Dispatchers.Main) {
                        toastMessage(applicationContext, "Upload failed!")
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