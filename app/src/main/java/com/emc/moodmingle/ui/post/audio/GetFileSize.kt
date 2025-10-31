package com.emc.moodmingle.ui.post.audio

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileSize(context: Context, uri: Uri): Long {
    var fileSize = 0L

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        fileSize = cursor.getLong(sizeIndex)
    }

    return fileSize
}