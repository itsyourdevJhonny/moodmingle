package com.emc.moodmingle.ui.create.util

import android.content.Context
import android.net.Uri

fun getMimeType(context: Context, uri: Uri): String? {
    return context.contentResolver.getType(uri)
}

fun countMediaTypes(context: Context, uris: List<Uri>): Triple<Int, Int, Int> {
    var imageCount = 0
    var videoCount = 0
    var audioCount = 0

    uris.forEach { uri ->
        val mimeType = getMimeType(context, uri) ?: ""

        when {
            mimeType.startsWith("image") -> imageCount++
            mimeType.startsWith("video") -> videoCount++
            mimeType.startsWith("audio") -> audioCount++
        }
    }

    return Triple(imageCount, videoCount, audioCount)
}

