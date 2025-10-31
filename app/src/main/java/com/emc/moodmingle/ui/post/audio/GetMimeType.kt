package com.emc.moodmingle.ui.post.audio

import android.content.Context
import android.net.Uri

fun getMimeType(context: Context, uri: Uri): String {
    return context.contentResolver.getType(uri) ?: ""
}