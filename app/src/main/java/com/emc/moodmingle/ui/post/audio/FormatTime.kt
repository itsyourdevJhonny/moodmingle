package com.emc.moodmingle.ui.post.audio

import java.util.Locale

fun formatTime(milliseconds: Int): String {
    val minutes = milliseconds / 1000 / 60
    val seconds = milliseconds / 1000 % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}