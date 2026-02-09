package com.emc.moodmingle.ui.create

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore

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

fun getRealPath(context: Context, uri: Uri): String {
    val proj = arrayOf(MediaStore.Video.Media.DATA)
    val cursor = context.contentResolver.query(uri, proj, null, null, null)
    cursor?.moveToFirst()
    val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATA) ?: 0
    val path = cursor?.getString(columnIndex) ?: ""
    cursor?.close()
    return path
}

fun loadVideoThumbnail(context: Context, uri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bmp = retriever.getFrameAtTime(1) // first frame
        retriever.release()
        bmp
    } catch (e: Exception) {
        null
    }
}

