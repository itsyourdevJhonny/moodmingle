package com.emc.moodmingle.ui.post.audio

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper

fun updateCurrentPosition(mediaPlayer: MediaPlayer, onUpdate: (Int) -> Unit) {
    if (mediaPlayer.isPlaying) {
        onUpdate(mediaPlayer.currentPosition)
        Handler(Looper.getMainLooper()).postDelayed({
            updateCurrentPosition(mediaPlayer, onUpdate)
        }, 1000)
    }
}