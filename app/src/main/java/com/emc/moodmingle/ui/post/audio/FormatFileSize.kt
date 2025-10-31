package com.emc.moodmingle.ui.post.audio

import java.util.Locale

fun formatFileSize(size: Long): String {
    return if (size < 1024 * 1024) {
        "${size / 1024} KB"
    } else {
        String.format(Locale.getDefault(), "%.2f MB", size.toFloat() / 1024 / 1024)
    }
}