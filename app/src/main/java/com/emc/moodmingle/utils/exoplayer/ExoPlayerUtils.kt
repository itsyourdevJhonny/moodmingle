package com.emc.moodmingle.utils.exoplayer

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

object ExoPlayerUtils {
    suspend fun getDurationTextFrom(content: Any, context: Context, durationText: String, onDuration: (String) -> Unit) {
        val exoPlayer = ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(if (content is String) content.toUri() else content as Uri))
                prepare()
            }

        while (durationText.isEmpty()) {
            val durationMs = exoPlayer.duration
            if (durationMs > 0) {
                 onDuration("%02d:%02d".format(durationMs / 1000 / 60, (durationMs / 1000) % 60))
            }
            delay(100)
        }
    }
}