package com.emc.moodmingle.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.emc.moodmingle.R
import com.emc.moodmingle.ui.post.audio.formatTime

class AudioPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val channelId = "audio_channel"
    private val notificationId = 1
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val remoteViews = RemoteViews(this.packageName, R.layout.notification_player)

        when (intent?.action) {
            "PLAY" -> {
                val audioUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra("AUDIO_URI", Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra("AUDIO_URI")
                    }

                if (mediaPlayer == null && audioUri != null) {
                    startPlayback(audioUri)
                } else {
                    mediaPlayer?.start()
                }
                updateNotification()
                startProgressUpdates(remoteViews)
            }

            "PAUSE" -> {
                mediaPlayer?.pause()
                updateNotification()
            }

            "SEEK" -> {
                val pos = intent.getIntExtra("SEEK_TO", 0)
                mediaPlayer?.seekTo(pos)
            }

            "STOP" -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startPlayback(uri: Uri) {
        val remoteViews = RemoteViews(this.packageName, R.layout.notification_player)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@AudioPlayerService, uri)
            prepare()
            start()
            setOnCompletionListener {
                sendProgressBroadcast(0, duration, false)
                stopSelf()
            }
        }
        showNotification()
        startProgressUpdates(remoteViews)
    }

    private fun startProgressUpdates(remoteViews: RemoteViews) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        handler.post(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    sendProgressBroadcast(it.currentPosition, it.duration, it.isPlaying)

                    val current = it.currentPosition
                    val duration = it.duration

                    remoteViews.setProgressBar(R.id.progressBar, duration, current, false)

                    remoteViews.setTextViewText(R.id.currentTime, formatTime(current))
                    remoteViews.setTextViewText(R.id.durationTime, formatTime(duration))

                    val icon = if (it.isPlaying) R.drawable.pause else R.drawable.play
                    remoteViews.setImageViewResource(R.id.btnPlayPause, icon)

                    manager.notify(notificationId, buildNotification(remoteViews))

                    if (it.isPlaying) {
                        handler.postDelayed(this, 500)
                    }
                }
            }
        })
    }

    private fun buildNotification(remoteViews: RemoteViews): Notification {
        val action = if (mediaPlayer?.isPlaying == true) "PAUSE" else "PLAY"
        val playPauseIntent = Intent(this, AudioPlayerService::class.java).apply { this.action = action }
        val playPausePending = PendingIntent.getService(
            this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.btnPlayPause, playPausePending)

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setCustomContentView(remoteViews)
            .setOngoing(mediaPlayer?.isPlaying == true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun sendProgressBroadcast(current: Int, duration: Int, isPlaying: Boolean) {
        val intent = Intent("com.emc.moodmingle.AUDIO_PROGRESS_UPDATE").apply {
            setPackage(packageName)
            putExtra("currentPosition", current)
            putExtra("duration", duration)
            putExtra("isPlaying", isPlaying)
        }
        sendBroadcast(intent)
    }

    private fun showNotification() {
        val remoteViews = RemoteViews(this.packageName, R.layout.notification_player)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Audio Playback", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        remoteViews.setTextViewText(R.id.currentTime, "0:00")
        remoteViews.setTextViewText(R.id.durationTime, "0:00")
        remoteViews.setProgressBar(R.id.progressBar, 100, 0, false)
        remoteViews.setImageViewResource(R.id.btnPlayPause, R.drawable.play)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.music_note)
            .setCustomContentView(remoteViews)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(notificationId, notification)
    }

    private fun updateNotification() {
        val remoteViews = RemoteViews(this.packageName, R.layout.notification_player)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mediaPlayer?.let { player ->
            remoteViews.setProgressBar(R.id.progressBar, player.duration, player.currentPosition, false)
            remoteViews.setTextViewText(R.id.currentTime, formatTime(player.currentPosition))
            remoteViews.setTextViewText(R.id.durationTime, formatTime(player.duration))
            val icon = if (player.isPlaying) R.drawable.pause else R.drawable.play
            remoteViews.setImageViewResource(R.id.btnPlayPause, icon)
            manager.notify(notificationId, buildNotification(remoteViews))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}