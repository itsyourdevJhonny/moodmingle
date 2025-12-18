package com.emc.moodmingle.ui.settings.saved.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

suspend fun extractDuration(
    context: Context,
    url: String,
    durationText: String,
    onDurationText: (String) -> Unit
) {
    val exoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(url))
        prepare()
    }

    // Continuously check until duration is valid
    while (durationText.isEmpty()) {
        val durationMs = exoPlayer.duration
        if (durationMs > 0) {
            onDurationText("%02d:%02d".format(durationMs / 1000 / 60, (durationMs / 1000) % 60))
        }
        delay(100)
    }
}